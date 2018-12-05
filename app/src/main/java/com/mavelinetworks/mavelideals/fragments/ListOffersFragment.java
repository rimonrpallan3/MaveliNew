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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.activities.OfferDetailActivity;
import com.mavelinetworks.mavelideals.adapter.FilterListAdapter;
import com.mavelinetworks.mavelideals.adapter.OfferChildListAdapter;
import com.mavelinetworks.mavelideals.adapter.lists.OfferListAdapter;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Category;
import com.mavelinetworks.mavelideals.classes.Filter;
import com.mavelinetworks.mavelideals.classes.FilterChild;
import com.mavelinetworks.mavelideals.classes.Offer;
import com.mavelinetworks.mavelideals.controllers.ErrorsController;
import com.mavelinetworks.mavelideals.controllers.sessions.GuestController;
import com.mavelinetworks.mavelideals.controllers.stores.OffersController;
import com.mavelinetworks.mavelideals.dtmessenger.MessengerHelper;
import com.mavelinetworks.mavelideals.load_manager.ViewManager;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.FilterParser;
import com.mavelinetworks.mavelideals.parser.api_parser.OfferParser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;
import com.mavelinetworks.mavelideals.presenter.IOfferFilterPresenter;
import com.mavelinetworks.mavelideals.presenter.OfferFilterPresenter;
import com.mavelinetworks.mavelideals.utils.DateUtils;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.mavelinetworks.mavelideals.views.IOfferFilterView;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.norbsoft.typefacehelper.TypefaceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

public class ListOffersFragment extends android.support.v4.app.Fragment
        implements OfferListAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener,
        IOfferFilterView,ViewManager.CustomView {

    public ViewManager mViewManager;
    //loading
    public SwipeRefreshLayout swipeRefreshLayout;
    //for scrolling params
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private int listType = 1;
    private RecyclerView list;
    private OfferListAdapter adapter;
    private FilterListAdapter filterListAdapter;
    //init request http
    private RequestQueue queue;
    private boolean loading = true;
    //pager
    private int COUNT = 0;
    private int REQUEST_PAGE = 1;
    private Category mCat;
    private GPStracker mGPS;
    private List<Offer> listStores = new ArrayList<>();
    private List<Filter> listFilter = new ArrayList<>();
    private int Fav = 0;
    private  int REQUEST_RANGE_RADIUS =-1;
    private String REQUEST_SEARCH ="";

    private String current_date;

    private int store_id = 0;


    private RecyclerView childListRecycleView;
    private RecyclerView filterListView;
    LinearLayout filterBtnClose;
    LinearLayout filterLayoutBtn;
    Button applyFilter;
    FrameLayout filterListLayout;
    FrameLayout filterApplyLayout;
    @BindView(R.id.closeFilterTitle)
    TextView closeFilterTitle;
    @BindView(R.id.revertFilterTxt)
    ImageView revertFilterTxt;
    @BindView(R.id.closeFiltersImg)
    ImageView closeFiltersImg;
    @BindView(R.id.applyBtnFilter)
    Button applyBtnFilter;

    String offertype_id="";
    IOfferFilterPresenter iOfferFilterPresenter;
    List<Filter> revertFilter;


    List<String> offerIdList=new ArrayList<String>();
    List<String> offerInnerIdList=new ArrayList<String>();
    List<String> offerChildInnerIdList=new ArrayList<String>();
    private OfferChildListAdapter adapterChildFilter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        REQUEST_RANGE_RADIUS = getResources().getInteger(R.integer.radius_map);

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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.search_icon).setVisible(true);
        updateBadge();
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
                    getOffers(1);

                }
            }).setHeader(getString(R.string.searchOnOffers)).showDialog();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_offers_list, container, false);

        TypefaceHelper.typeface(rootView);
        Realm realm = Realm.getDefaultInstance();

        iOfferFilterPresenter = new OfferFilterPresenter(this);
        try {
            store_id = getArguments().getInt("store_id");
        }catch (Exception e){}

        try {

            int CatId = getArguments().getInt("category");
            mCat = realm.where(Category.class).equalTo("numCat",CatId).findFirst();

            //load from assets
           /*if(!ServiceHandler.isNetworkAvailable(getActivity())){

               if(Constances.ENABLE_OFFLINE)
                listStores = loader.parseStoresFromAssets(getActivity(),mCat.getNumCat());
           }*/

        } catch (Exception e) {

            e.printStackTrace();
        }

        mGPS = new GPStracker(getActivity());
        queue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

        mViewManager = new ViewManager(getActivity());
        mViewManager.setLoading(rootView.findViewById(R.id.loading));
        mViewManager.setNoLoading(rootView.findViewById(R.id.content_my_store));
        mViewManager.setError(rootView.findViewById(R.id.error));
        mViewManager.setEmpty(rootView.findViewById(R.id.empty));
        mViewManager.setCustumizeView(this);


        list = (RecyclerView) rootView.findViewById(R.id.list);
        childListRecycleView = (RecyclerView) rootView.findViewById(R.id.childList);
        filterListView = (RecyclerView) rootView.findViewById(R.id.filterListView);
        filterBtnClose = (LinearLayout) rootView.findViewById(R.id.filterBtnClose);
        filterLayoutBtn = (LinearLayout) rootView.findViewById(R.id.filterLayoutBtn);
        filterListLayout = (FrameLayout) rootView.findViewById(R.id.filterListLayout);
        filterApplyLayout = (FrameLayout) rootView.findViewById(R.id.filterApplyLayout);
        closeFilterTitle = (TextView ) rootView.findViewById(R.id.closeFilterTitle);
        revertFilterTxt = rootView.findViewById(R.id.revertFilterTxt);
        closeFiltersImg = rootView.findViewById(R.id.closeFiltersImg);
        applyBtnFilter = rootView.findViewById(R.id.applyBtnFilter);

        revertFilterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childListRecycleView.setVisibility(View.GONE);
                filterListView.setVisibility(View.VISIBLE);
                revertFilterTxt.setVisibility(View.GONE);
                filterApplyLayout.setVisibility(View.GONE);
            }
        });

        adapter = new OfferListAdapter(getActivity(), listStores);
        adapter.setClickListener(this);


        try {
            filterListAdapter = new FilterListAdapter(listFilter,getActivity(),this);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" onCreateView error : "+e.getMessage());
        }
        //filterListAdapter.setClickListener(this);


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
                        if(ServiceHandler.isNetworkAvailable(getContext())) {
                            if (COUNT > adapter.getItemCount())
                                getOffers(REQUEST_PAGE);
                        }else
                        {
                            Toast.makeText(getContext(),"Network not available ",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        filterLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterListLayout.setVisibility(View.VISIBLE);
            }
        });

        filterBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childListRecycleView.setVisibility(View.GONE);
                filterListView.setVisibility(View.VISIBLE);
                revertFilterTxt.setVisibility(View.GONE);
                filterListLayout.setVisibility(View.GONE);
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


        if (ServiceHandler.isNetworkAvailable(this.getActivity())) {
            getOffers(REQUEST_PAGE);

        }else {

            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(),getString(R.string.check_network),Toast.LENGTH_LONG).show();
            if(adapter.getItemCount()==0)
                mViewManager.error();
        }


        return rootView;
    }

    @Override
    public void itemClicked(View view, int position) {

        Intent intent = new Intent(getActivity(), OfferDetailActivity.class);
        intent.putExtra("offer_id",adapter.getItem(position).getId());
        startActivity(intent);

    }

    @Override
    public void iconImageViewOnClick(View v, int position) {

    }

    public void getOffers(final int page) {

        mGPS = new GPStracker(getActivity());
        swipeRefreshLayout.setRefreshing(true);

        if (adapter.getItemCount() == 0)
            mViewManager.loading();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_GET_OFFERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (APP_DEBUG) {
                        Log.e("responseOffersString", response);
                    }
                    System.out.println("responseOffersString : "+response);

                    JSONObject jsonObject = new JSONObject(response);


                    //Log.e("response",response);

                    final OfferParser mOfferParser = new OfferParser(jsonObject);
                   // List<Store> list = mStoreParser.getEventFromDB();
                    COUNT = 0;
                    COUNT = mOfferParser.getIntArg(Tags.COUNT);
                    mViewManager.showResult();


                    //check server permission and display the errors
                    if(mOfferParser.getSuccess()==-1){
                        ErrorsController.serverPermissionError(getActivity());
                    }

                    if (page == 1) {

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RealmList<Offer> list = mOfferParser.getOffers();

                                adapter.removeAll();
                                for (int i = 0; i < list.size(); i++) {
                                   // if (list.get(i).getDistance() <= REQUEST_RANGE_RADIUS)
                                        adapter.addItem(list.get(i));
                                }

                                //set it into database
                                OffersController.insertOffers(list);

                                swipeRefreshLayout.setRefreshing(false);
                                loading = true;

                                mViewManager.showResult();

                                if (COUNT > adapter.getItemCount())
                                    REQUEST_PAGE++;
                                if (COUNT == 0  || adapter.getItemCount() == 0) {
                                    mViewManager.empty();
                                }



                                if (APP_DEBUG) {
                                    Log.e("count ", COUNT + " page = " + page);
                                }
                                getFilters();
                            }
                        }, 800);
                    } else {
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RealmList<Offer> list = mOfferParser.getOffers();

                                for (int i = 0; i < list.size(); i++) {
                                    //if (list.get(i).getDistance() <=REQUEST_RANGE_RADIUS)
                                    adapter.addItem(list.get(i));
                                }


                                //set it into database
                                OffersController.insertOffers(list);

                                swipeRefreshLayout.setRefreshing(false);
                                mViewManager.showResult();
                                loading = true;
                                if (COUNT > adapter.getItemCount())
                                    REQUEST_PAGE++;

                                if (COUNT == 0  || adapter.getItemCount() == 0) {
                                    mViewManager.empty();
                                }
                                getFilters();
                            }
                        }, 800);

                    }

                } catch (JSONException e) {
                    //send a rapport to support
                    if(APP_DEBUG)
                        e.printStackTrace();

                    if(adapter.getItemCount()==0)
                        mViewManager.error();


                    swipeRefreshLayout .setRefreshing(false);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (APP_DEBUG) {
                    Log.e("ERROR", error.toString());
                }

                mViewManager.error();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                if (mGPS.canGetLocation()) {
                    params.put("latitude", mGPS.getLatitude() + "");
                    params.put("longitude", mGPS.getLongitude() + "");
                    System.out.println("latitude : "+mGPS.getLatitude());
                    System.out.println("longitude : "+mGPS.getLongitude() );
                }
                System.out.println("token : " + Utils.getToken(getActivity()));
                System.out.println("mac_adr : " + ServiceHandler.getMacAddr());
                System.out.println("guest Id : " + String.valueOf(GuestController.getGuest().getId()));

                params.put("token", Utils.getToken(getActivity()));
                params.put("mac_adr", ServiceHandler.getMacAddr()  );

                params.put("limit", "30");
                params.put("page", page + "");
                params.put("search", REQUEST_SEARCH);

                params.put("date", current_date);

                if(store_id>0)
                    params.put("store_id", String.valueOf(store_id));

                if (APP_DEBUG) {
                    Log.e("ListStoreFragment", "  params getFilters :" + params.toString());
                }


                if(REQUEST_RANGE_RADIUS !=-1){
                    if(REQUEST_RANGE_RADIUS<=99)
                        params.put("radius", String.valueOf((REQUEST_RANGE_RADIUS *1024)));
                }

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

            if(ServiceHandler.isNetworkAvailable(getContext())) {

                REQUEST_SEARCH = "";
                REQUEST_PAGE = 1;
                REQUEST_RANGE_RADIUS = -1;
                getOffers(1);

            }else
            {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(),getString(R.string.check_network),Toast.LENGTH_LONG).show();

                if(adapter.getItemCount()==0)
                    mViewManager.error();
            }

    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            //Toast.makeText(getActivity(), "  is Liked  :"+args.get("isLiked"), Toast.LENGTH_LONG).show();
            Fav = args.getInt("fav");

        }
    }

    @Override
    public void customErrorView(View v) {

        Button retry = (Button) v.findViewById(R.id.btn);

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGPS = new GPStracker(getActivity());

                if (!mGPS.canGetLocation() && listType == 1)
                    mGPS.showSettingsAlert();

                getOffers(1);
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

        Button btn = (Button) v.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilterTitle.setVisibility(View.GONE);
                offertype_id= "";
                mViewManager.loading();
                getOffers(1);
                REQUEST_PAGE = 1;
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    /*### --------------------- Rimon Changes------------------------------ ####*/
    private void getFilters(){
        mGPS = new GPStracker(getActivity());
        //swipeRefreshLayout.setRefreshing(true);

        if (filterListAdapter.getItemCount() == 0) {
           // mViewManager.loading();
        }

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_GET_OFFERS_TYPES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("-----getFilters  response -------- : "+response);


                try{
                    JSONObject jsonObject = new JSONObject(response);
                    final FilterParser filterParser = new FilterParser(jsonObject);


                    // List<Store> list = mStoreParser.getEventFromDB();
                    COUNT = 0;
                    COUNT = filterParser.getIntArg(Tags.COUNT);
                    mViewManager.showResult();

                    //check server permission and display the errors
                    if(filterParser.getSuccess()==-1){
                        ErrorsController.serverPermissionError(getActivity());
                    }

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RealmList<Filter> list = filterParser.getFilters();
                            Gson gson = new Gson();
                            String jsonString = gson.toJson(list);
                           // iOfferFilterPresenter.setAdapter(mainOffersList);
                            //String jsonString2 = gson.toJson(expiredDateList);
                            //UserDetails user1 = gson.fromJson(jsonString,UserDetails.class);
                            System.out.println(" ----------- List Filter Fragment  : "+jsonString);
                            filterListAdapter.removeAll();
                            for (int i = 0; i < list.size(); i++) {
                                // if (list.get(i).getDistance() <= REQUEST_RANGE_RADIUS)
                                filterListAdapter.addItem(list.get(i));
                            }

                            iOfferFilterPresenter.setAdapter(list);

                            //set it into database
                           // FilterController.insertOffers(list);

                            //swipeRefreshLayout.setRefreshing(false);
                            //loading = true;

                            //mViewManager.showResult();

                         /*   if (COUNT > adapter.getItemCount())
                                REQUEST_PAGE++;
                            if (COUNT == 0  || adapter.getItemCount() == 0) {
                                mViewManager.empty();
                            }



                            if (APP_DEBUG) {
                                Log.e("count ", COUNT + " page = " + page);
                            }*/
                            //getFilters();
                        }
                    }, 800);

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("getFilters Exception : "+e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (APP_DEBUG) {
                    Log.e("ERROR", error.toString());
                }

                mViewManager.error();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                if (mGPS.canGetLocation()) {
                    params.put("latitude", mGPS.getLatitude() + "");
                    params.put("longitude", mGPS.getLongitude() + "");
                    System.out.println("latitude : "+mGPS.getLatitude());
                    System.out.println("longitude : "+mGPS.getLongitude() );
                }
                System.out.println("token : "+Utils.getToken(getActivity()));
                System.out.println("mac_adr : "+ServiceHandler.getMacAddr());
                System.out.println("guest Id : "+String.valueOf(GuestController.getGuest().getId()));

                params.put("token", Utils.getToken(getActivity()));
                params.put("mac_adr", ServiceHandler.getMacAddr()  );

                //params.put("limit", "30");
                //params.put("page", page + "");
                //params.put("search", REQUEST_SEARCH);

                //params.put("date", current_date);

               /* if(store_id>0)
                    params.put("store_id", String.valueOf(store_id));

                if (APP_DEBUG) {
                    Log.e("ListStoreFragment", "  params getFilters :" + params.toString());
                }*/


                if(REQUEST_RANGE_RADIUS !=-1){
                    if(REQUEST_RANGE_RADIUS<=99)
                        params.put("radius", String.valueOf((REQUEST_RANGE_RADIUS *1024)));
                }

                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);



    }



    public void getfiltered(final String num){
        swipeRefreshLayout.setRefreshing(true);

        if (adapter.getItemCount() == 0)
            mViewManager.loading();


        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_GET_OFFERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (APP_DEBUG) {
                        Log.e("responseOffersString", response);
                    }

                    JSONObject jsonObject = new JSONObject(response);


                    //Log.e("response",response);

                    final OfferParser mOfferParser = new OfferParser(jsonObject);
                    // List<Store> list = mStoreParser.getEventFromDB();
                    COUNT = 0;
                    COUNT = mOfferParser.getIntArg(Tags.COUNT);
                    mViewManager.showResult();


                    //check server permission and display the errors
                    if(mOfferParser.getSuccess()==-1){
                        ErrorsController.serverPermissionError(getActivity());
                    }

                    if (num!=null) {

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RealmList<Offer> list = mOfferParser.getOffers();

                                adapter.removeAll();
                                for (int i = 0; i < list.size(); i++) {
                                    // if (list.get(i).getDistance() <= REQUEST_RANGE_RADIUS)
                                    adapter.addItem(list.get(i));
                                }

                                //set it into database
                                OffersController.insertOffers(list);

                                swipeRefreshLayout.setRefreshing(false);
                                loading = true;

                                mViewManager.showResult();

                                if (COUNT > adapter.getItemCount())
                                    REQUEST_PAGE++;
                                if (COUNT == 0  || adapter.getItemCount() == 0) {
                                    mViewManager.empty();
                                }




                                getFilters();
                            }
                        }, 800);
                    } else {
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RealmList<Offer> list = mOfferParser.getOffers();

                                for (int i = 0; i < list.size(); i++) {
                                    //if (list.get(i).getDistance() <=REQUEST_RANGE_RADIUS)
                                    adapter.addItem(list.get(i));
                                }


                                //set it into database
                                OffersController.insertOffers(list);

                                swipeRefreshLayout.setRefreshing(false);
                                mViewManager.showResult();
                                loading = true;
                                if (COUNT > adapter.getItemCount())
                                    REQUEST_PAGE++;

                                if (COUNT == 0  || adapter.getItemCount() == 0) {
                                    mViewManager.empty();
                                }
                                getFilters();
                            }
                        }, 800);


                    }

                } catch (JSONException e) {
                    //send a rapport to support
                    if(APP_DEBUG)
                        e.printStackTrace();

                    if(adapter.getItemCount()==0)
                        mViewManager.error();


                    swipeRefreshLayout.setRefreshing(false);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (APP_DEBUG) {
                    Log.e("ERROR", error.toString());
                }

                mViewManager.error();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("token", Utils.getToken(getActivity()));
                params.put("mac_adr", ServiceHandler.getMacAddr()  );

                params.put("limit", "30");
                params.put("offertype_id",num);

                if(store_id>0)
                    params.put("store_id", String.valueOf(store_id));

                if (APP_DEBUG) {
                    Log.e("ListStoreFragment", "  params getOffers :" + params.toString());
                }

                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);

    }

    @OnClick(R.id.applyBtnFilter)
    public void applyInnerFilters() {

        if(filterListView.getVisibility()== View.VISIBLE){
            offertype_id = removeArrayBracket(offerIdList);
            filterListLayout.setVisibility(View.GONE);
            getfiltered(offertype_id);
            closeFilterTitle.setVisibility(View.VISIBLE);
            offerIdList.clear();
        }else if(childListRecycleView.getVisibility()== View.VISIBLE){
            offertype_id = removeArrayBracket(offerInnerIdList);
            filterListLayout.setVisibility(View.GONE);
            getfiltered(offertype_id);
            closeFilterTitle.setVisibility(View.VISIBLE);
            offerInnerIdList.clear();
            childListRecycleView.setVisibility(View.GONE);
            filterListView.setVisibility(View.VISIBLE);
        }

    }

    public String removeArrayBracket(List<String> offerLists){
        Gson gson = new Gson();
        String jsonString = gson.toJson(offerLists);
        jsonString = jsonString.replaceAll("\\[", "").replaceAll("\\]","");
        //UserDetails user1 = gson.fromJson(jsonString,UserDetails.class);
        System.out.println(" ----------- getFilters OfferList "+jsonString);
        if(jsonString!=null) {
            System.out.println("-----------getFilters OfferList"+jsonString);
        }
        return jsonString;
    }

    @OnClick(R.id.revertFilterTxt)
    public void backToMainFilter(){
        childListRecycleView.setVisibility(View.GONE);
        filterListView.setVisibility(View.VISIBLE);
        revertFilterTxt.setVisibility(View.GONE);
        filterApplyLayout.setVisibility(View.GONE);
        /*adapterFilter = new OfferFilterListAdapter(revertFilter, this);
        filterListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        filterListView.setItemAnimator(new DefaultItemAnimator());
        filterListView.setAdapter(adapterFilter);*/
    }

    @OnClick(R.id.closeFiltersImg)
    public void closeFiltersImg() {
        childListRecycleView.setVisibility(View.GONE);
        filterListView.setVisibility(View.VISIBLE);
        revertFilterTxt.setVisibility(View.GONE);
        filterApplyLayout.setVisibility(View.GONE);
        filterListLayout.setVisibility(View.GONE);
    }

    @Override
    public void setFilterList(List<Filter> filterList) {
        revertFilter = filterList;
        filterListAdapter = new FilterListAdapter(filterList, getActivity(), this);
        filterListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        filterListView.setItemAnimator(new DefaultItemAnimator());
        filterListView.setAdapter(filterListAdapter);
    }

    @Override
    public void setFilterListRecycle(View view, int position, int num) {
 /*filterListLayout.setVisibility(View.GONE);
        offertype_id = String.valueOf(num);
        System.out.println("offertype_id :"+offertype_id);
        getfiltered(offertype_id);
        closeFilterTitle.setVisibility(View.VISIBLE);*/
    }

    @Override
    public void setChildList(RealmList<FilterChild> filterChildren) {
        filterListView.setVisibility(View.INVISIBLE);
        childListRecycleView.setVisibility(View.VISIBLE);
        revertFilterTxt.setVisibility(View.VISIBLE);
        filterApplyLayout.setVisibility(View.VISIBLE);
        adapterChildFilter = new OfferChildListAdapter(filterChildren, this,getActivity());
        childListRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        childListRecycleView.setItemAnimator(new DefaultItemAnimator());
        childListRecycleView.setAdapter(adapterChildFilter);
    }

    @Override
    public void onItemCheck(String offerTypeCheckList) {
        offerIdList.add(offerTypeCheckList);
        filterApplyLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemUncheck(String offerTypeCheckedLists) {
        offerIdList.remove(offerTypeCheckedLists);
        filterApplyLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemInnerCheck(String offerTypeCheckList) {
        System.out.println("offerTypeCheckList: "+offerTypeCheckList);
        offerInnerIdList.add(offerTypeCheckList);
        filterApplyLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemInnerUncheck(String offerTypeCheckedLists) {
        offerInnerIdList.remove(offerTypeCheckedLists);
        setFilterApplyBtnVisiblity();
    }

    public void setFilterApplyBtnVisiblity(){
        if(offerIdList!=null&&offerIdList.size()>0){
            filterApplyLayout.setVisibility(View.VISIBLE);
        }else if(offerInnerIdList!=null&&offerInnerIdList.size()>0){
            filterApplyLayout.setVisibility(View.VISIBLE);
        }   else {
            filterApplyLayout.setVisibility(View.GONE);
        }
    }
}
