package com.mavelinetworks.mavelideals.RetorHelper;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 10-Apr-18.
 */

public class Result {

    @SerializedName("0")
    MainOffers zeroOffer;
    @SerializedName("1")
    MainOffers firstOffer;
    @SerializedName("2")
    MainOffers secondOffer;
    @SerializedName("3")
    MainOffers thirdOffer;
    @SerializedName("4")
    MainOffers fourOffer;
    @SerializedName("5")
    MainOffers fiveOffer;
    @SerializedName("6")
    MainOffers sixOffer;
    @SerializedName("7")
    MainOffers sevenOffer;
    @SerializedName("8")
    MainOffers eightOffer;
    @SerializedName("9")
    MainOffers nineOffer;
    @SerializedName("10")
    MainOffers tenOffer;

    List<MainOffers> mainOffersList = new ArrayList<>();

    public Result() {
    }

    public MainOffers getZeroOffer() {
        return zeroOffer;
    }

    public void setZeroOffer(MainOffers zeroOffer) {
        this.zeroOffer = zeroOffer;
    }

    public MainOffers getFirstOffer() {
        return firstOffer;
    }

    public void setFirstOffer(MainOffers firstOffer) {
        this.firstOffer = firstOffer;
    }

    public MainOffers getSecondOffer() {
        return secondOffer;
    }

    public void setSecondOffer(MainOffers secondOffer) {
        this.secondOffer = secondOffer;
    }

    public MainOffers getThirdOffer() {
        return thirdOffer;
    }

    public void setThirdOffer(MainOffers thirdOffer) {
        this.thirdOffer = thirdOffer;
    }

    public MainOffers getFourOffer() {
        return fourOffer;
    }

    public void setFourOffer(MainOffers fourOffer) {
        this.fourOffer = fourOffer;
    }

    public MainOffers getFiveOffer() {
        return fiveOffer;
    }

    public void setFiveOffer(MainOffers fiveOffer) {
        this.fiveOffer = fiveOffer;
    }

    public MainOffers getSixOffer() {
        return sixOffer;
    }

    public void setSixOffer(MainOffers sixOffer) {
        this.sixOffer = sixOffer;
    }

    public MainOffers getSevenOffer() {
        return sevenOffer;
    }

    public void setSevenOffer(MainOffers sevenOffer) {
        this.sevenOffer = sevenOffer;
    }

    public MainOffers getEightOffer() {
        return eightOffer;
    }

    public void setEightOffer(MainOffers eightOffer) {
        this.eightOffer = eightOffer;
    }

    public MainOffers getNineOffer() {
        return nineOffer;
    }

    public void setNineOffer(MainOffers nineOffer) {
        this.nineOffer = nineOffer;
    }

    public MainOffers getTenOffer() {
        return tenOffer;
    }

    public void setTenOffer(MainOffers tenOffer) {
        this.tenOffer = tenOffer;
    }

    public List<MainOffers> getMainOffersList() {
        return mainOffersList;
    }

    public void setMainOffersList(List<MainOffers> mainOffersList) {
        this.mainOffersList = mainOffersList;
    }

    /*HashMap<String, MainOffers> firstOffer;


    public Result() {
    }

    public Result(HashMap<String, MainOffers> firstOffer) {
        this.firstOffer = firstOffer;
    }

    public HashMap<String, MainOffers> getFirstOffer() {
        return firstOffer;
    }

    public void setFirstOffer(HashMap<String, MainOffers> firstOffer) {
        this.firstOffer = firstOffer;
    }*/
}
