package com.mavelinetworks.mavelideals.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mavelinetworks.mavelideals.GPS.GPStracker;
import com.mavelinetworks.mavelideals.GPS.Position;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Offer;
import com.mavelinetworks.mavelideals.controllers.CampagneController;
import com.mavelinetworks.mavelideals.controllers.stores.OffersController;
import com.mavelinetworks.mavelideals.load_manager.ViewManager;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.mavelinetworks.mavelideals.network.api_request.SimpleRequest;
import com.mavelinetworks.mavelideals.parser.api_parser.OfferParser;
import com.mavelinetworks.mavelideals.utils.DateUtils;
import com.mavelinetworks.mavelideals.utils.OfferUtils;
import com.mavelinetworks.mavelideals.utils.TextUtils;
import com.mavelinetworks.mavelideals.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.norbsoft.typefacehelper.TypefaceHelper;
import com.squareup.picasso.Picasso;
import com.wuadam.awesomewebview.AwesomeWebView;

import org.bluecabin.textoo.LinksHandler;
import org.bluecabin.textoo.Textoo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.SHOW_ADS;
import static com.mavelinetworks.mavelideals.appconfig.AppConfig.SHOW_ADS_IN_OFFER;

/**
 * Created by Droideve on 11/13/2017.
 */

public class OfferDetailActivity extends AppCompatActivity implements ViewManager.CustomView {




    private int offer_id;
    private  ViewManager mViewManager;
    private ImageView image;
    private TextView priceView,distanceView/*,typeView*/;
    private TextView detail_offer,offer_up_to;
    private TextView storeBtn;
    private LinearLayout storeBtnLayout;
    private AdView mAdView;

    @Override
    protected void onResume() {

        if(mAdView!=null)
            mAdView.resume();

        super.onResume();
    }

    @Override
    protected void onPause() {

        if(mAdView!=null)
            mAdView.pause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if(mAdView!=null)
            mAdView.destroy();

        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);

        setupToolbar();

        //ADD ADMOB BANNER
        if (SHOW_ADS && SHOW_ADS_IN_OFFER) {

            mAdView = (AdView) findViewById(R.id.adView);
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("4A55E4EA2535643C0D74A5A73C4F550A")
                    .addTestDevice("3CB74DFA141BF4D0823B8EA7D94531B5")
                    .build();
            mAdView.loadAd(adRequest);
        }

        //INIT VIEW MANAGER
        mViewManager = new ViewManager(this);
        mViewManager.setLoading(findViewById(R.id.loading));
        mViewManager.setNoLoading(findViewById(R.id.content_offer));
        mViewManager.setError(findViewById(R.id.error));
        mViewManager.setEmpty(findViewById(R.id.empty));
        mViewManager.setCustumizeView(this);

        mViewManager.loading();

        TypefaceHelper.typeface(this);

        image = (ImageView) findViewById(R.id.image);
        priceView = (TextView) findViewById(R.id.priceView);
       // typeView = (TextView) findViewById(R.id.type);
        distanceView = (TextView) findViewById(R.id.distanceView);
        detail_offer = (TextView) findViewById(R.id.detail_offer);
        storeBtn = (TextView) findViewById(R.id.storeBtn);
        offer_up_to = (TextView) findViewById(R.id.offer_up_to);
        storeBtnLayout = (LinearLayout) findViewById(R.id.storeBtnLayout);

        try {
            offer_id = getIntent().getExtras().getInt("offer_id");

            if(offer_id==0){
                offer_id = getIntent().getExtras().getInt("id");
            }


            if(offer_id==0){
                offer_id = Integer.parseInt(getIntent().getExtras().getString("id"));
            }

            if(AppConfig.APP_DEBUG)
                Toast.makeText(this,String.valueOf(offer_id),Toast.LENGTH_LONG).show();
        }catch (Exception e){
            finish();
        }

        final Offer mOffer = OffersController.findOfferById(offer_id);
        if(mOffer!=null && mOffer.isLoaded() && mOffer.isValid()){

            mViewManager.showResult();
            putInsideViews(mOffer);

        }else{
            getOffer(offer_id);
        }

        /*
         *
         *   DATE & COUNTDOWN
         *
         */

        String date = "";


        try {
            date = mOffer.getDate_start();
            date = DateUtils.prepareOutputDate(date,"dd MMMM yyyy  hh:mm",this);
        }catch (Exception e){

            getOffer(offer_id);
            return;

        }



    }

    private void putInsideViews(final Offer mOffer){

        APP_TITLE_VIEW.setText(mOffer.getName());
        if(mOffer.getStore_id()>0){

            Drawable storeDrawable = new IconicsDrawable(this)
                    .icon(CommunityMaterial.Icon.cmd_map_marker)
                    .color(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null))
                    .sizeDp(18);


            GPStracker mGPS = new GPStracker(this);
            Position newPosition = new Position();
            Double mDistance = newPosition.distance(mGPS.getLatitude(),mGPS.getLongitude(),mOffer.getLat(),mOffer.getLng());

            String disStr = Utils.preparDistance(mDistance)
                    +" "+
                    Utils.getDistanceBy(mDistance).toUpperCase();


            distanceView.setText(
                    String.format(getString(R.string.offerIn), disStr)
            );


            storeBtn.setText(mOffer.getStore_name());
            storeBtn.setCompoundDrawables(storeDrawable,null,null,null);
            storeBtn.setCompoundDrawablePadding(20);
            storeBtn.setPaintFlags(storeBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            storeBtnLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        if (!StoreDetailActivity.isOpend()) {
                            Intent intent = new Intent(OfferDetailActivity.this, StoreDetailActivity.class);
                            intent.putExtra("id", mOffer.getStore_id());
                            startActivity(intent);
                        }

                        realm.commitTransaction();

                    }catch (Exception e){
                        if(AppConfig.APP_DEBUG)
                            e.printStackTrace();

                        Toast.makeText(OfferDetailActivity.this,getString(R.string.not_found),Toast.LENGTH_LONG).show();

                    }

                }
            });

            storeBtnLayout.setVisibility(View.VISIBLE);
        }else
            storeBtnLayout.setVisibility(View.GONE);

        if(mOffer.getContent().getPrice()!=0.0f && mOffer.getContent().getPrice()>0)
            priceView.setText(
                    OfferUtils.parseCurrencyFormat(
                            mOffer.getContent().getPrice(),
                            mOffer.getContent().getCurrency()
                    )
            );
        else if(mOffer.getContent().getPercent()!=0){
            priceView.setText(mOffer.getContent().getPercent()+"%");
        }else {
            priceView.setText(getString(R.string.promo));
        }

        if(mOffer.getImages()!=null)
            Picasso.with(this).load(mOffer.getImages().getUrl500_500()).fit().centerCrop().into(image);


        detail_offer.setText(mOffer.getContent().getDescription());
        new TextUtils.decodeHtml(detail_offer).execute(mOffer.getContent().getDescription());

        Textoo
                .config(detail_offer)
                .linkifyWebUrls()  // or just .linkifyAll()
                .addLinksHandler(new LinksHandler() {
                    @Override
                    public boolean onClick(View view, String url) {

                        if (Utils.isValidURL(url)) {

                            new AwesomeWebView.Builder(OfferDetailActivity.this)
                                    .showMenuOpenWith(false)
                                    .statusBarColorRes(R.color.colorPrimary)
                                    .theme(R.style.FinestWebViewAppTheme)
                                    .titleColor(
                                            ResourcesCompat.getColor(getResources(),R.color.defaultColor,null)
                                    ).urlColor(
                                    ResourcesCompat.getColor(getResources(),R.color.defaultColor,null)
                            ).show(url);

                            return true;
                        } else {
                            return false;
                        }
                    }
                })
                .apply();

        try {

            int  cid = Integer.parseInt(getIntent().getExtras().getString("cid")) ;
            CampagneController.markView(cid);
            // Toast.makeText(this,"CMarkViewClicked",Toast.LENGTH_LONG).show();
        }catch (Exception e){
            if(AppConfig.APP_DEBUG)
                e.printStackTrace();
        }


        String dateStartAt ="";
        String dateEndAt ="";

        try {
            dateStartAt = mOffer.getDate_start();
            dateStartAt = DateUtils.prepareOutputDate(dateStartAt,"dd MMMM yyyy  HH:mm",this);
        }catch (Exception e){
            return;
        }

        try {
            dateEndAt = mOffer.getDate_end();
            dateEndAt = DateUtils.prepareOutputDate(dateEndAt,"dd MMMM yyyy  HH:mm",this);
        }catch (Exception e){
            return;
        }

//        if(mOffer.getType()==0){
//            CountdownView mCvCountdownView = (CountdownView)findViewById(R.id.cv_countdownViewTest1);
//            mCvCountdownView.setVisibility(View.GONE);
//            typeView.setVisibility(View.GONE);
//        }else {

            String inputDateSatrt = DateUtils.prepareOutputDate(mOffer.getDate_start(),"yyyy-MM-dd HH:mm:ss",this);
            long diff_Will_Start = DateUtils.getDiff(inputDateSatrt,"yyyy-MM-dd HH:mm:ss");

            if(AppConfig.APP_DEBUG){

                Log.e("_start_at_server",mOffer.getDate_start());
                Log.e("_start_at_device ",dateStartAt);
                Log.e("_start_at_diff ", String.valueOf(diff_Will_Start));
            }

            if(diff_Will_Start>0){

//                CountdownView mCvCountdownView = (CountdownView)findViewById(R.id.cv_countdownViewTest1);
//                mCvCountdownView.setVisibility(View.VISIBLE);
//                mCvCountdownView.start( diff_Will_Start ); // Millisecond

                offer_up_to.setText(    String.format(    getString(R.string.offer_start_at)  ,dateStartAt.toString()     )         );

            }


            String inputDateEnd = DateUtils.prepareOutputDate(mOffer.getDate_end(),"yyyy-MM-dd HH:mm:ss",this);
            long diff_will_end = DateUtils.getDiff(inputDateEnd,"yyyy-MM-dd HH:mm:ss");


            if(AppConfig.APP_DEBUG){
                Log.e("_end_at_server",mOffer.getDate_end());
                Log.e("_end_at_device ",dateEndAt);
                Log.e("_end_at_diff ", String.valueOf(diff_will_end));

            }

            if(diff_will_end>0 && diff_Will_Start<0){

//                CountdownView mCvCountdownView = (CountdownView)findViewById(R.id.cv_countdownViewTest1);
//                mCvCountdownView.setVisibility(View.VISIBLE);
//                mCvCountdownView.start(  diff_will_end  ); // Millisecond

                offer_up_to.setText(    String.format(    getString(R.string.offer_end_at)  ,dateEndAt.toString()     )         );

            }


            if(diff_Will_Start<0 && diff_will_end<0){
                offer_up_to.setText(    String.format(    getString(R.string.offer_ended_at)  ,dateEndAt.toString()     )         );
            }

           // typeView.setVisibility(View.VISIBLE);


//        }
//

        Drawable storeDrawable = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_calendar)
                .color(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null))
                .sizeDp(18);
        offer_up_to.setCompoundDrawables(storeDrawable,null,null,null);
        offer_up_to.setCompoundDrawablePadding(20);


    }


    private TextView APP_TITLE_VIEW,APP_DESC_VIEW;
    public void setupToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setSubtitle("E-shop");
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_36dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        APP_TITLE_VIEW = (TextView) toolbar.findViewById(R.id.toolbar_title);
        APP_DESC_VIEW = (TextView) toolbar.findViewById(R.id.toolbar_description);

        APP_DESC_VIEW.setVisibility(View.GONE);
        Utils.setFont(this, APP_DESC_VIEW, Constances.Fonts.BOLD);
        Utils.setFont(this, APP_TITLE_VIEW, Constances.Fonts.BOLD);

        APP_TITLE_VIEW.setText(R.string.store_title_detail);
        APP_DESC_VIEW.setVisibility(View.GONE);

    }



    public void getOffer(final int offer_id) {

        mViewManager.loading();

        final GPStracker mGPS = new GPStracker(this);

        SimpleRequest request = new SimpleRequest(Request.Method.POST,
                Constances.API.API_GET_OFFERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                mViewManager.showResult();


                try {

                    if (APP_DEBUG) {
                        Log.e("responseOffersString", response);
                    }

                    JSONObject jsonObject = new JSONObject(response);
                    final OfferParser mOfferParser = new OfferParser(jsonObject);
                    RealmList<Offer> list = mOfferParser.getOffers();

                    if(list.size()>0){

                        OffersController.insertOffers(list);

                        putInsideViews(list.get(0));

                    }else {

                        Toast.makeText(OfferDetailActivity.this,getString(R.string.not_found),Toast.LENGTH_LONG).show();
                        finish();

                    }

                } catch (JSONException e) {
                    //send a rapport to support
                    if (APP_DEBUG)
                        e.printStackTrace();

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
                    params.put("lat", mGPS.getLatitude() + "");
                    params.put("lng", mGPS.getLongitude() + "");
                }

                params.put("limit", "1");
                params.put("offer_id", offer_id + "");

                if (APP_DEBUG) {
                    Log.e("ListStoreFragment", "  params getFilters :" + params.toString());
                }

                return params;
            }

        };


        request.setRetryPolicy(new DefaultRetryPolicy(SimpleRequest.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).getRequestQueue().add(request);

    }


    @Override
    public void customErrorView(View v) {

    }

    @Override
    public void customLoadingView(View v) {

    }

    @Override
    public void customEmptyView(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(android.R.id.home==item.getItemId()){
            if(!MainActivity.isOpend()){
                startActivity(new Intent(this,MainActivity.class));
            }
            finish();
        } if(item.getItemId()==R.id.map_action){

            startActivity(new Intent(this, MapStoresListActivity.class));
            overridePendingTransition(R.anim.lefttoright_enter, R.anim.lefttoright_exit);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if(!MainActivity.isOpend()){
            startActivity(new Intent(this,MainActivity.class));
        }else {

        }

        super.onBackPressed();
    }

}
