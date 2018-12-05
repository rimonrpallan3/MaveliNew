package com.mavelinetworks.mavelideals.Services;

/**
 * Created by Droideve on 10/30/2016.
 */

public class BusEventRefresh {

    public static int NETWORK_AVAILABLE=1;
    public static int NETWORK_UNAVAILABLE=0;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
