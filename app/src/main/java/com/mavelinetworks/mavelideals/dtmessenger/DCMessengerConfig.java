package com.mavelinetworks.mavelideals.dtmessenger;

/**
 * Created by Droideve on 7/21/2016.
 */

public class  DCMessengerConfig {

    public static final String APP_ID ="1";
    public static final String SENT_TOKEN_TO_SERVER = "tokenid";
    public static final String SENT_APP_ID_TO_SERVER = "appid";

    public static class Params{

        public static String SOCKET_USER_GET_CONNECT = "get connect";
        public static String SOCKET_USER_GET_DISCONNECT = "get disconnect";
        public static String SOCKET_USER_GET_MESSAGE = "get message";//get message from server
        public static String SOCKET_USER_SEND_MESSAGE = "send message";//send message from server
        public static String SOCKET_MESSAGE_RECEIVED = "message received";
        public static String SOCKET_SET_CONNECT = "set connection";
        public static String SOCKET_DEVICE_JOIN = "join device";
        public static String SOCKET_MESSAGE_SENT = "message sent";



        public static String SOCKET_USER_ONLINE = "user online";


        public static class ResultStatus{
            public static String SUCCESS = "1";
            public static String FIELD = "0";
        }

        public static String JSON_SENDER_TAG = "sender";
        public static String JSON_RECEIVER_TAG = "receiver";

        public static String JSON_TOKENID = "tokenid";
        public static String JSON_SENDERID = "senderid";
        public static String JSON_SENDER_UID = "senderuserid";
        public static String JSON_STATUS= "status";
        public static String JSON_MESSAGE = "message";

    }


    public static class Config{

        public static String SERVER_ADDRESS = "http://52.178.163.121:5000";
        public static String SPDATA = "wsdroidevetemp";
        public static String REGISTRATION_COMPLETE = "REGISTRATION_COMPLETE";


    }







}
