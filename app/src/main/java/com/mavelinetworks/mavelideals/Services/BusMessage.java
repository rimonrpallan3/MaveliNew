package com.mavelinetworks.mavelideals.Services;

/**
 * Created by Droideve on 8/19/2016.
 */

public class BusMessage {

    public static int OPEN_PANEL=1;
    public static int CLOSE_PANEL=0;
    public static int SEARCH_ON_PLACES = 2;
    public static int SEARCH_ON_HASHTAGS = 3;
    public static int SEARCH_ON_EVENTS = 4;
    public static int SEARCH_ON_PEOPLE = 5;
    public static int SEARCH_ON_PICTURES = 6;
    public static int ON_MOVE_TO = 7;
    public static int REFRESH = 88;



    public static int NOTIFY_NOTIFS_CHANGED = 766;
    public static int NOTIFY_INBOX_CHANGED = 767;
    private int intValue;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public static int SEARCH_ON_SUBMIT = 56;
    public static int SEARCH_ON_TEXT_CHANGED = 66;


    public static int GET_NBR_NEW_NOTIFS = 6635;

    public static int USER_FOLLOWED = 60;


    private int order=CLOSE_PANEL;
    private String terms="";
    private int type=SEARCH_ON_SUBMIT;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }


    public BusMessage() {
        this.order = CLOSE_PANEL;
    }

    public BusMessage(int order) {
        this.order = order;
    }



    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    @Override
    public String toString() {
        return "BusMessage{" +
                "order=" + order +
                ", terms='" + terms + '\'' +
                ", type=" + type +
                '}';
    }
}
