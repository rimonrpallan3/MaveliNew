package com.mavelinetworks.mavelideals;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.AppContext;
import com.mavelinetworks.mavelideals.appconfig.Constances;
import com.mavelinetworks.mavelideals.classes.Category;
import com.mavelinetworks.mavelideals.helper.MyPreferenceManager;
import com.mavelinetworks.mavelideals.network.ServiceHandler;
import com.mavelinetworks.mavelideals.security.Security;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.FirebaseApp;
import com.norbsoft.typefacehelper.TypefaceCollection;
import com.norbsoft.typefacehelper.TypefaceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.mavelinetworks.mavelideals.appconfig.AppConfig.APP_DEBUG;

/**
 * Created by Droideve on 6/2/2016.
 */
public class AppController extends MultiDexApplication {

    private static String fcmToken="";

    public static String setFcmToken(String str){
        fcmToken = str;
        return fcmToken;
    }

    public static String getFcmToken(){
        return fcmToken;
    }

    private static HashMap<String,String> tokens=null;

    public synchronized static boolean isTokenFound(){
        SharedPreferences sharedPref = getInstance().getSharedPreferences("tokens", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token-0","");

        if(token.equals(""))
            return false;

        return true;
    }

    public synchronized static HashMap<String,String> getTokens(){

            tokens = new HashMap<>();
            SharedPreferences sharedPref = getInstance().getSharedPreferences("tokens", Context.MODE_PRIVATE);

        tokens.put("apiKey","00-1");
            tokens.put("macadr", sharedPref.getString("macadr", ServiceHandler.getMacAddr() ));
            tokens.put("token-0", sharedPref.getString("token-0",""));
            tokens.put("token-1",  sharedPref.getString("token-1",""));
            tokens.put("ipAddress","value");

        if(AppContext.DEBUG)
            Log.e(TAG,"getTokens");

        return tokens;
    }

    public synchronized static HashMap<String,String> setTokens(String macadr, String token0, String token1){
            SharedPreferences sharedPref = getInstance().getSharedPreferences("tokens", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("apiKey","00-1");
            editor.putString("macadr",macadr);
            editor.putString("token-0",token0);
            editor.putString("token-1",token1);
            editor.putString("uid",token1);
            editor.putString("ipAddress","value");
            editor.commit();

            tokens = new HashMap<>();
            tokens.put("apiKey","00-1");
            tokens.put("macadr", macadr);
            tokens.put("token-0", token0);
            tokens.put("token-1", token1);
            tokens.put("ipAddress","value");

        if(AppContext.DEBUG)
            Log.e(TAG,"setTokens");

        return tokens;
    }

    public synchronized static HashMap<String,String> setTokens(String macadr, String token0, String token1, String uid){

        SharedPreferences sharedPref = getInstance().getSharedPreferences("tokens", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("apiKey","00-1");
        editor.putString("macadr",macadr);
        editor.putString("token-0",token0);
        editor.putString("token-1",token1);
        editor.putString("uid",uid);
        editor.putString("ipAddress","value");
        editor.commit();

        tokens = new HashMap<>();
        tokens.put("apiKey","00-1");
        tokens.put("macadr", macadr);
        tokens.put("token-0", token0);
        tokens.put("token-1", token1);
        tokens.put("uid", uid);
        tokens.put("ipAddress","value");

        if(AppContext.DEBUG)
            Log.e(TAG,"setTokens");

        return tokens;
    }

    public synchronized static HashMap<String,String> refreshToken(String key, String value){

        SharedPreferences sharedPref = getInstance().getSharedPreferences("tokens", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key,value);
        editor.commit();

        if(AppContext.DEBUG)
            Log.e(TAG,"refreshToken");

        return AppController.getTokens();
    }


    private static ArrayList<String> listLangsIndex=null;

    public static synchronized ArrayList<String> getLangsIndex(ArrayList<String> listLangs){

        if(listLangsIndex==null && listLangs!=null){
            listLangsIndex = listLangs;
        }

        if(listLangs!=null){
            listLangsIndex = listLangs;
        }

        if(listLangsIndex==null && listLangs==null){
            listLangsIndex=new ArrayList<>();
        }

        if(AppContext.DEBUG)
            Log.e(TAG,"getLangsIndex");

        return  listLangsIndex;
    }



    public static final String TAG = AppController.class
            .getSimpleName();
    private static AppController mInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }
    private MyPreferenceManager pref;
    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }
        return pref;
    }


    /*
        DCMESSENGER Init
     */

    private void appInit(){
        mInstance=this;
        parseAppConfig();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(getString(R.string.analytics));
        }
        return mTracker;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        appInit();

        Security.init(this);

        TypefaceCollection typeface = new TypefaceCollection.Builder()
                .set(Typeface.NORMAL, Typeface.createFromAsset(getAssets(), Constances.Fonts.REGULAR))
                .create();
        TypefaceHelper.init(typeface);

        MobileAds.initialize(this,getString(R.string.ad_app_id));
        //initialization typeface (fonts)

        Realm.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name(BuildConfig.APPLICATION_ID+".realm")
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);

        //get langs

        FirebaseApp.initializeApp(this);

    }

    private RequestQueue mRequestQueue;


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(com.android.volley.Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(com.android.volley.Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
        if(APP_DEBUG) { Log.e("Application","Memory  cleaned !!"); }
    }


    private void parseAppConfig(){

        //others
        AppConfig.ABOUT_CONTENT=getResources().getString(R.string.ABOUT_CONTENT);
        AppConfig.ADDRESS_CONTACT=getResources().getString(R.string.ADDRESS_CONTACT);
        AppConfig.PHONE=getResources().getString(R.string.PHONE);

        AppConfig.SHOW_ADS = Boolean.parseBoolean(getResources().getString(R.string.SHOW_ADS));
        AppConfig.SHOW_ADS_IN_EVENT = Boolean.parseBoolean(getResources().getString(R.string.SHOW_ADS_IN_EVENT));
        AppConfig.SHOW_ADS_IN_HOME = Boolean.parseBoolean(getResources().getString(R.string.SHOW_ADS_IN_HOME));
        AppConfig.SHOW_INTERSTITIAL_ADS_IN_STARTUP = Boolean.parseBoolean(getResources().getString(R.string.SHOW_INTERSTITIAL_ADS_IN_STARTUP));
        AppConfig.SHOW_ADS_IN_STORE = Boolean.parseBoolean(getResources().getString(R.string.SHOW_ADS_IN_STORE));
        AppConfig.SHOW_ADS_IN_OFFER = Boolean.parseBoolean(getResources().getString(R.string.SHOW_ADS_IN_OFFER));

        AppConfig.BASE_URL=getResources().getString(R.string.BASE_URL);

        AppConfig.ENABLE_CHAT=  Boolean.parseBoolean(getResources().getString(R.string.ENABLE_CHAT));

        AppConfig.ENABLE_WEB_DASHBOARD=  Boolean.parseBoolean(getResources().getString(R.string.ENABLE_WEB_DASHBOARD));
        AppConfig.RATE_US_FORCE=Boolean.parseBoolean(getResources().getString(R.string.RATE_US_ON_PLAY_STORE_FORCE));
        AppConfig.ANDROID_API_KEY =getResources().getString(R.string.ANDROID_API_KEY);

        //tabs
        parseAppTabsConfig();



        //chat config
        Constances.BASE_URL=getResources().getString(R.string.BASE_URL);
        Constances.BASE_URL_API=getResources().getString(R.string.BASE_URL_API);
        Constances.PRIVACY_POLICY_URL=getResources().getString(R.string.PRIVACY_POLICY_URL);
        Constances.TERMS_OF_USE_URL=getResources().getString(R.string.TERMS_OF_USE_URL);
        Constances.SERVER_ADDRESS_IP=getResources().getString(R.string.SERVER_ADDRESS_IP);
        Constances.SOCKET_SERVER_VERSION=getResources().getString(R.string.SOCKET_SERVER_VERSION);

    }

    private  List<Category> appTabs;
    public List<Category> parseAppTabsConfig(){
        if(appTabs==null){
            parseConfigToJava();
        }

        if(AppConfig.TabsConfig==null)
            AppConfig.TabsConfig = appTabs;

        Constances.initConfig.ListCats = AppConfig.TabsConfig;
        Constances.initConfig.Numboftabs = AppConfig.TabsConfig.size();

        return appTabs;
    }

    private  void parseConfigToJava(){

        appTabs = new ArrayList<>();

        String[] tab_names = getResources().getStringArray(R.array.tab_names);
        int[] tab_indexes = getResources().getIntArray(R.array.tab_indexes);

        TypedArray ar =getResources().obtainTypedArray(R.array.tab_icons);
        int len = ar.length();
        int[] picArray = new int[len];
        for (int i = 0; i < len; i++)
            picArray[i] = ar.getResourceId(i, 0);

        ar.recycle();

        for (int i=0;i<tab_names.length;i++){

            if(tab_indexes[i]==-5 && AppConfig.ENABLE_CHAT==false){

            }else {
                Category c = new Category(tab_indexes[i],
                        tab_names[i],0,picArray[i]);
                appTabs.add(c);
            }

        }

        /*
         *   SUPPORT RTL ////////////////////////////////
         */

        if(isRTL()){

            List<Category> tempList = new ArrayList<>();
            for (int i=(appTabs.size()-1);i>=0;i--){
                tempList.add(appTabs.get(i));
                if(APP_DEBUG)
                    Log.e("rtl_",appTabs.get(i).getNameCat());
            }
            appTabs = tempList;

        }



    }


    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }


    public void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }


}
