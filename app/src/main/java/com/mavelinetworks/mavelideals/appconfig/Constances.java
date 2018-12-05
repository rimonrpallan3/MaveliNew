package com.mavelinetworks.mavelideals.appconfig;

import com.mavelinetworks.mavelideals.BuildConfig;
import com.mavelinetworks.mavelideals.classes.Category;

import java.util.List;

public class Constances {


    public static int DISTANCE_CONST = 1024;
    //Change the url depending on the name of your web hosting
   // public static String BASE_URL = "https://droideve.com/smartgeostore";
    public static String BASE_URL = AppConfig.BASE_URL;
    public static String BASE_URL_API = AppConfig.BASE_URL;
    public static String TERMS_OF_USE_URL = AppConfig.BASE_URL;
    public static String PRIVACY_POLICY_URL = AppConfig.BASE_URL;

    //CHAT CONFIG SERVER
    public static String SERVER_ADDRESS_IP;
    public static String SOCKET_SERVER_VERSION;


    //Set the link to the app store account
    public static String PLAY_STORE_URL = AppConfig.PLAY_STORE_URL;
    //Your AdMob Banner Unit ID
    //public static final String BANNER_UNIT_ID = Config.BANNER_UNIT_ID;
    //public static final String ADS_UNIT_ID = Config.ADS_UNIT_ID;
    //Your maps api key
    //public static final String MAPS_API_ID = Config.MAPS_API_ID;
    //You Can download other fonts , place it in the app and change the variables
    public static class Fonts{
        public final static String BOLD = "fonts/OpenSans-Bold.ttf";
        public final static String REGULAR = "fonts/OpenSans-Regular.ttf";
    }

    //Set SQLLite database  information
    public static final  String DB_NAME= BuildConfig.APPLICATION_ID+".db";
    public static final int DB_VERISON = 2;

    //WARNING :  DO NOT EDIT THIS
    public static class API{

        private static String API_VERSION = "1.0";
        public final static String BASE_IMAGES_URL = BASE_URL+"/uploads/images/";
        //store API's
        public static String API_USER_GET_STORES = BASE_URL_API+"/"+API_VERSION+"/store/getStores";
        public static String API_USER_GET_REVIEWS = BASE_URL_API+"/"+API_VERSION+"/store/getComments";
        public static String API_USER_CREATE_STORE = BASE_URL_API+"/"+API_VERSION+"/store/createStore";
        public static String API_USER_UPDATE_STORE = BASE_URL_API+"/"+API_VERSION+"/webservice/updateStore";
        public static  String API_RATING_STORE = BASE_URL_API+"/"+API_VERSION+"/store/rate";
        public static String API_SAVE_STORE= BASE_URL_API+"/"+API_VERSION+"/store/saveStore";
        public static String API_REMOVE_STORE= BASE_URL_API+"/"+API_VERSION+"/store/removeStore";
        //event API's
        public static String API_USER_GET_EVENTS = BASE_URL_API+"/"+API_VERSION+"/event/getEvents";
        //category API's
        public static String API_USER_GET_CATEGORY = BASE_URL_API+"/"+API_VERSION+"/category/getCategories";
        //uploader API's
        public static String API_USER_UPLOAD64 = BASE_URL_API+"/"+API_VERSION+"/uploader/uploadImage64";
        //user API's
        public static String API_USER_LOGIN = BASE_URL_API+"/"+API_VERSION+"/user/signIn";
        public static String API_USER_SIGNUP = BASE_URL_API+"/"+API_VERSION+"/user/signUp";
        public static String API_USER_OTP = BASE_URL_API+"/"+API_VERSION+"/user/verifyPhone";
        public static String API_USER_VERIFY_OTP = BASE_URL_API+"/"+API_VERSION+"/user/verifyOTP";
        public static String API_USER_CHECK_CONNECTION = BASE_URL_API+"/"+API_VERSION+"/user/checkUserConnection";
        public static String API_BLOCK_USER= BASE_URL_API+"/"+API_VERSION+"/user/blockUser";
        public static String API_GET_USERS= BASE_URL_API+"/"+API_VERSION+"/user/getUsers";
        public static String API_UPDATE_ACCOUNT= BASE_URL_API+"/"+API_VERSION+"/user/updateAccount";
        public static String API_USER_REGISTER_TOKEN = BASE_URL_API+"/"+API_VERSION+"/user/registerToken";
        public static String API_REFRESH_POSITION= BASE_URL_API+"/"+API_VERSION+"/user/refreshPosition";
        //setting API's
        public static String API_APP_INIT = BASE_URL_API+"/"+API_VERSION+"/setting/app_initialization";
        //messenger API's
        public static String API_LOAD_MESSAGES= BASE_URL_API+"/"+API_VERSION+"/messenger/loadMessages";
        public static String API_LOAD_DISCUSSION= BASE_URL_API+"/"+API_VERSION+"/messenger/loadDiscussion";
        public static String API_INBOX_MARK_AS_SEEN= BASE_URL_API+"/"+API_VERSION+"/messenger/markMessagesAsSeen";
        public static String API_INBOX_MARK_AS_LOADED= BASE_URL_API+"/"+API_VERSION+"/messenger/markMessagesAsLoaded";
        public static String API_SEND_MESSAGE= BASE_URL_API+"/"+API_VERSION+"/messenger/sendMessage";
        //offer API's
        public static String API_GET_OFFERS= BASE_URL_API+"/"+API_VERSION+"/offer/getOffers";
        public static String API_GET_OFFERS_TYPES= BASE_URL_API+"/"+API_VERSION+"/offertypes/getOffertypes";
        //campaign API's
        public static String API_MARK_VIEW= BASE_URL_API+"/"+API_VERSION+"/campaign/markView";
        public static String API_MARK_RECEIVE= BASE_URL_API+"/"+API_VERSION+"/campaign/markReceive";


    }


    public static  class initConfig{

        public static class fonts {
            public static final String  FONTSTYLE = "";
        }

        //WARNING :  DO NOT EDIT THIS
        public static class Tabs{

            public static final int HOME = 0;
            public static final int NEARBY = 0;
            public static final int BOOKMAKRS = -1;
            public static final int MOST_RATED = -2;
            public static final int MOST_RECENT = -3;
            public static final int EVENTS = -4;
            public static final int CHAT = -5;
            public static final int NEARBY_OFFERS = -6;
        }


        public static class AppInfos{

                // set the description
                public static String ABOUT_CONTENT = AppConfig.ABOUT_CONTENT;

                // Your email that you wish that users on your app will contact you.
                public static String ADDRESS_CONTACT= AppConfig.ADDRESS_CONTACT;
                public static String PHONE= AppConfig.PHONE;

        }

        //WARNING :  DO NOT EDIT THIS
        public static List<Category> ListCats;
        public static int Numboftabs; 
    }


}
