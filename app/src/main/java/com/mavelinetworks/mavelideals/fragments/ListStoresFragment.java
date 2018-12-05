package com.mavelinetworks.mavelideals.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
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
import com.mavelinetworks.mavelideals.activities.MainActivity;
import com.mavelinetworks.mavelideals.activities.StoreDetailActivity;
import com.mavelinetworks.mavelideals.adapter.lists.StoreListAdapter;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Store;
import com.mavelinetworks.mavelideals.classes.Category;
import com.mavelinetworks.mavelideals.controllers.ErrorsController;
import com.mavelinetworks.mavelideals.controllers.stores.StoreController;
import com.mavelinetworks.mavelideals.dtmessenger.MessengerHelper;
import com.mavelinetworks.mavelideals.load_manager.ViewManager;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.StoreParser;
import com.mavelinetworks.mavelideals.parser.tags.Tags;
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

public class ListStoresFragment extends android.support.v4.app.Fragment
        implements StoreListAdapter.ClickListener, SwipeRefreshLayout.OnRefreshListener, ViewManager.CustomView {

    public ViewManager mViewManager;
    //loading
    public SwipeRefreshLayout swipeRefreshLayout;
    //for scrolling params
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    private int listType = 1;
    private RecyclerView list;
    private StoreListAdapter adapter;
    //init request http
    private RequestQueue queue;
    private boolean loading = true;
    //pager
    private int COUNT = 0;
    private int REQUEST_PAGE = 1;
    private Category mCat;
    private GPStracker mGPS;
    private List<Store> listStores = new ArrayList<>();


    private int REQUEST_RANGE_RADIUS = -1;
    private String REQUEST_SEARCH = "";
    private int Fav = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        REQUEST_RANGE_RADIUS = getResources().getInteger(R.integer.radius_map);
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

        MenuItem item = menu.findItem(R.id.search_icon).setVisible(true);
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

                    getStores(1);

                }
            }).setHeader(getString(R.string.searchOnStores)).showDialog();

        }
        return super.onOptionsItemSelected(item);
    }

    private int owner_id = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_store_list, container, false);

        TypefaceHelper.typeface(rootView);

        Realm realm = Realm.getDefaultInstance();

        try {

            owner_id = getArguments().getInt("user_id");
        } catch (Exception e) {
        }



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

        adapter = new StoreListAdapter(getActivity(), listStores);
        adapter.setClickListener(this);


        list.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        //listcats.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        list.setItemAnimator(new DefaultItemAnimator());
        list.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        /*list.setLayoutManager(mLayoutManager);*/
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
                                getStores(REQUEST_PAGE);
                        }else
                        {
                            Toast.makeText(getContext(),"Network not available ",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        swipeRefreshLayout = (android.support.v4.widget.SwipeRefreshLayout)
                rootView.findViewById(R.id.refresh);

        swipeRefreshLayout.setOnRefreshListener(this);


        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary
        );


        if (ServiceHandler.isNetworkAvailable(this.getActivity())) {
            getStores(REQUEST_PAGE);
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


        Store store = adapter.getItem(position);

        if (store != null) {


            if(APP_DEBUG)
                Log.e("_1_store_id", String.valueOf(store.getId()));

            Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
            intent.putExtra("id", store.getId());
            startActivity(intent);
        }

    }

    @Override
    public void iconImageViewOnClick(View v, int position) {


    }

    public void getStores(final int page) {

        mGPS = new GPStracker(getActivity());
        swipeRefreshLayout.setRefreshing(true);

        if (adapter.getItemCount() == 0)
            mViewManager.loading();

        final int numCat = mCat.getNumCat();

        final String strIds = StoreController.getSavedStoresAsString();

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_USER_GET_STORES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    if (APP_DEBUG) {
                        Log.e("responseStoresString", response);
                    }

                    JSONObject jsonObject = new JSONObject(response);

                    //Log.e("response",response);

                    final StoreParser mStoreParser = new StoreParser(jsonObject);
                   // List<Store> list = mStoreParser.getEventFromDB();
                    COUNT = 0;
                    COUNT = mStoreParser.getIntArg(Tags.COUNT);
                    mViewManager.showResult();


                    //check server permission and display the errors
                    if(mStoreParser.getSuccess()==-1){
                        ErrorsController.serverPermissionError(getActivity());
                    }

                    if (page == 1) {

                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RealmList<Store> list = mStoreParser.getStore();

                                if(list.size()>0){
                                    StoreController.insertStores(list);
                                }
                                adapter.removeAll();
                                for (int i = 0; i < list.size(); i++) {
                                   // if (list.get(i).getDistance() <= REQUEST_RANGE_RADIUS)
                                        adapter.addItem(list.get(i));


                                }

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

                            }
                        }, 800);
                    } else {
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                RealmList<Store> list = mStoreParser.getStore();

                                if(list.size()>0){
                                    StoreController.insertStores(list);
                                }

                                for (int i = 0; i < list.size(); i++) {
                                    //if (list.get(i).getDistance() <=REQUEST_RANGE_RADIUS)
                                    adapter.addItem(list.get(i));

                                }
                                swipeRefreshLayout.setRefreshing(false);
                                mViewManager.showResult();
                                loading = true;
                                if (COUNT > adapter.getItemCount())
                                    REQUEST_PAGE++;

                                if (COUNT == 0  || adapter.getItemCount() == 0) {
                                    mViewManager.empty();
                                }
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

                if (mGPS.canGetLocation()) {
                    params.put("latitude", mGPS.getLatitude() + "");
                    params.put("longitude", mGPS.getLongitude() + "");
                }

              //  params.put("token", Utils.getToken(getActivity()));


                if(REQUEST_RANGE_RADIUS >-1){
                    if(REQUEST_RANGE_RADIUS<=99)
                        params.put("radius", String.valueOf((REQUEST_RANGE_RADIUS *1024)));
                }

                params.put("limit", "30");

                if(owner_id>0)
                    params.put("user_id", String.valueOf(owner_id));

                if (Fav == -1) {
                    if (!strIds.equals(""))
                        params.put("store_ids", strIds);
                    else {
                        params.put("store_ids", "0");
                    }
                } else {
                    if (numCat == Constances.initConfig.Tabs.BOOKMAKRS) {

                        if (!strIds.equals(""))
                            params.put("store_ids", strIds);
                        else {
                            params.put("store_ids", "0");
                        }
                    }
                    if (numCat == Constances.initConfig.Tabs.MOST_RATED) {

                        params.put("order_by", String.valueOf(Constances.initConfig.Tabs.MOST_RATED));

                    }
                    if (numCat == Constances.initConfig.Tabs.MOST_RECENT) {
                        params.put("order_by", String.valueOf(Constances.initConfig.Tabs.MOST_RECENT));

                    } else if (numCat== Constances.initConfig.Tabs.HOME) {

                    } else {
                        params.put("category_id", numCat+ "");
                    }

                }

                params.put("page", page + "");
                params.put("search", REQUEST_SEARCH);

                if (APP_DEBUG) {
                    Log.e("ListStoreFragment", "  params getStores :" + params.toString());
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


        getStores(1);

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

                getStores(1);
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
                mViewManager.loading();
                getStores(1);
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
}
