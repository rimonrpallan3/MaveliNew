package com.mavelinetworks.mavelideals.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.EventActivity;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.adapter.lists.EventListAdapter;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Category;
import com.mavelinetworks.mavelideals.classes.Event;
import com.mavelinetworks.mavelideals.controllers.ErrorsController;
import com.mavelinetworks.mavelideals.controllers.events.EventController;
import com.mavelinetworks.mavelideals.controllers.events.UpComingEventsController;
import com.mavelinetworks.mavelideals.dtmessenger.MessengerHelper;
import com.mavelinetworks.mavelideals.load_manager.ViewManager;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.EventParser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;
import com.mavelinetworks.mavelideals.utils.DateUtils;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.norbsoft.typefacehelper.TypefaceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

public class ListEventFragment extends android.support.v4.app.Fragment
        implements EventListAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener,ViewManager.CustomView {

    public ViewManager mViewManager;
    //loading
    public SwipeRefreshLayout swipeRefreshLayout;
    private int listType = 1;
    //to check if the event is liked
    private int isLiked = 0;
    private RecyclerView list;
    private EventListAdapter adapter;
    //init request http
    private RequestQueue queue;
    //for scrolling params
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private LinearLayoutManager mLayoutManager;
    //pager
    private int COUNT = 0;

    private Category mCat;
    private GPStracker mGPS;
    private List<Event> listEvents=new ArrayList<>();

    private int REQUEST_PAGE = 1;
    private String REQUEST_SEARCH ="";
    private int REQUEST_RANGE_RADIUS =-1;


    private String current_date;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        current_date = DateUtils.getUTC("dd-MM-yyyy hh:mm");
    }

    private void updateBadge(){
        if (MessengerHelper.NbrMessagesManager.getNbrTotalMessages() > 0) {
            ActionItemBadge.update(getActivity(), MainActivity.mainMenu.findItem(R.id.item_samplebadge),
                    CommunityMaterial.Icon.cmd_bell_ring_outline,
                    ActionItemBadge.BadgeStyles.RED,
                    MessengerHelper.NbrMessagesManager.getNbrTotalMessages());
        } else {
            ActionItemBadge.hide(MainActivity.mainMenu.findItem(R.id.item_samplebadge));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        updateBadge();
        MenuItem item = menu.findItem(R.id.search_icon);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.search_icon){

            SearchDialog.newInstance(getContext()).setOnSearchListener(new SearchDialog.Listener() {
                @Override
                public void onSearchClicked(SearchDialog mSearchDialog, String value, int radius) {
                    if(mSearchDialog.isShowing())
                        mSearchDialog.dismiss();

                    if(AppConfig.APP_DEBUG)
                        Toast.makeText(getContext(),value+" "+radius,Toast.LENGTH_LONG).show();

                    REQUEST_RANGE_RADIUS = radius;
                    REQUEST_SEARCH = value;
                    REQUEST_PAGE = 1;
                    getEvents(1);

                }
            }).setHeader(getString(R.string.searchOnEvents)).showDialog();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_list, container, false);

        TypefaceHelper.typeface(rootView);

        try {
            isLiked = getArguments().getInt("isLiked");

        }catch (Exception e){
            e.printStackTrace();
        }

        mGPS = new GPStracker(getActivity());

        if(!mGPS.canGetLocation() && listType==1)
            mGPS.showSettingsAlert();


        queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();


        mViewManager = new ViewManager(getActivity());
        mViewManager.setLoading(rootView.findViewById(R.id.loading));
        mViewManager.setNoLoading(rootView.findViewById(R.id.content_my_store));
        mViewManager.setError(rootView.findViewById(R.id.error));
        mViewManager.setEmpty(rootView.findViewById(R.id.empty));
        mViewManager.setCustumizeView(this);
        mViewManager.loading();


        list = (RecyclerView) rootView.findViewById(R.id.list);


        adapter = new EventListAdapter(getActivity(), listEvents);
        adapter.setClickListener(this);


        list.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        //listcats.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        list.setItemAnimator(new DefaultItemAnimator());
        list.setLayoutManager(mLayoutManager);
        list.setAdapter(adapter);

//
        list.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = false;

                        if (COUNT > adapter.getItemCount())
                            getEvents(REQUEST_PAGE);
                    }
                }
            }
        });



        swipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary
        );

        if(isLiked == 1) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    List<Event> myEvent = EventController.getLikeEventsAsArrayList();
                    for(int j = 0 ;j<myEvent.size();j++){
                        myEvent.get(j).setFeatured(0);
                        adapter.addItem(myEvent.get(j));
                    }
                }
            });



            mViewManager.showResult();
        }else{

            REQUEST_SEARCH = "";
            REQUEST_PAGE = 1;
            REQUEST_RANGE_RADIUS = -1;

            mViewManager.loading();
            getEvents(REQUEST_PAGE);
        }

        return rootView;
    }

    public List<Event> getData(){

        List<Event> list = new ArrayList<Event>();
        Event st = new Event();
        list.add(st);
        return list;

    }

    @Override
    public void itemClicked(View view, int position) {

        Event event_c = adapter.getItem(position);

        if(event_c!=null) {

            Intent intent = new Intent(getActivity(), EventActivity.class);
            intent.putExtra("id", event_c.getId());
            startActivity(intent);

        }

    }

    @Override
    public void iconImageViewOnClick(View v, int position) {


        Event event_c = adapter.getItem(position);

    }

    public void getEvents(final int page){


        mGPS = new GPStracker(getActivity());

        swipeRefreshLayout.setRefreshing(true);

        String ids = "";

        if(isLiked==2){
            ids = UpComingEventsController.getListAsString();
        }

        final String finalIds = ids;
        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_EVENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{


                    JSONObject jsonObject = new JSONObject(response);

                    if(APP_DEBUG)
                        Log.e("response", jsonObject.toString());

                    final EventParser mEventParser = new EventParser(jsonObject);
                    COUNT = mEventParser.getIntArg(Tags.COUNT);


                    //check server permission and display the errors
                    if(mEventParser.getSuccess()==-1){
                        ErrorsController.serverPermissionError(getActivity());
                    }

                    if(page==1)
                    {

                        adapter.removeAll();

                        if(APP_DEBUG) { Log.e("count",COUNT+""); }

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                RealmList<Event> list = mEventParser.getEvents();

                                if(list.size()>0){
                                    EventController.insertEvents(list);
                                }

                                for(int i=0;i<list.size();i++){

                                        //GET THE ID EVENT FROM EVENT LIKE IN DATABASE AND COMPARE IT WITH THIS ID
                                   adapter.addItem(list.get(i));
                                        if(APP_DEBUG) {
                                            Log.e("EventParser", "id event "+list.get(i).getId()+"   description   "+list.get(i).getAddress());
                                        }
                                }


                                if(APP_DEBUG) {
                                    Log.e("EventParserCount", adapter.getItemCount()+"");
                                }

                                swipeRefreshLayout.setRefreshing(false);

                                loading = true;

                                if(COUNT>adapter.getItemCount())
                                    REQUEST_PAGE++;


                                if(COUNT==0){
                                    mViewManager.empty();
                                }else {
                                    mViewManager.showResult();
                                }

                            }
                        }, 800);
                    }
                    else
                    {
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                RealmList<Event> list = mEventParser.getEvents();

                                if(list.size()>0){
                                    EventController.insertEvents(list);
                                }

                                for (int i = 0; i < list.size(); i++) {
                                    adapter.addItem(list.get(i));
                                    if(APP_DEBUG) {   Log.e("EventParser", "id event "+list.get(i).getId()+"   description   "+list.get(i).getAddress()); }
                                }
                                swipeRefreshLayout.setRefreshing(false);
                                mViewManager.showResult();
                                loading = true;

                                if(COUNT>adapter.getItemCount())
                                    REQUEST_PAGE++;

                                if(COUNT==0 || adapter.getItemCount() == 0){
                                    mViewManager.empty();
                                }
                            }
                        }, 800);

                    }

                }catch (JSONException e){
                    //send a rapport to support
                    Log.e("ERROR", response.toString());
                    e.printStackTrace();
                    mViewManager.error();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(APP_DEBUG)
                    if(APP_DEBUG) {
                    Log.e("ERROR", error.toString());
                }

                mViewManager.error();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                if(mGPS.canGetLocation()) {
                    params.put("latitude", mGPS.getLatitude() + "");
                    params.put("longitude", mGPS.getLongitude() + "");
                }


                if(REQUEST_RANGE_RADIUS !=-1){
                    if(REQUEST_RANGE_RADIUS<=99)
                        params.put("radius", String.valueOf((REQUEST_RANGE_RADIUS *1024)));
                }

                params.put("token", Utils.getToken(getActivity()));
                params.put("mac_adr", ServiceHandler.getMacAddr() );
                params.put("limit","12");
                params.put("page", page + "");
                params.put("search",REQUEST_SEARCH);

                params.put("date", current_date);

                if(isLiked==2){
                    params.put("event_ids", finalIds);
                }



                if(APP_DEBUG){Log.e("ListEventFragment","  params getEvent :"+params.toString());}

                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }


    @Override
    public void onRefresh() {

        REQUEST_SEARCH = "";
        REQUEST_PAGE = 1;
        REQUEST_RANGE_RADIUS = -1;

        getEvents(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        //onRefresh();
    }

    @Override
    public void customErrorView(View v) {

        Button retry = (Button) v.findViewById(R.id.btn);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGPS = new GPStracker(getActivity());
                if(!mGPS.canGetLocation()&& listType==1)
                    mGPS.showSettingsAlert();

                getEvents(1);
                REQUEST_PAGE = 1;
                mViewManager.loading();
            }
        });

    }

    @Override
    public void customLoadingView(View v) {




    }

    @Override
    public void customEmptyView(View v) {


        Button btn= (Button) v.findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mViewManager.loading();
                getEvents(1);
                REQUEST_PAGE = 1;
            }
        });



    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        //menu.findItem(R.id.action_map).setVisible(false);

    }
}
