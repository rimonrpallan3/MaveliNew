package com.mavelinetworks.mavelideals.RetorHelper;

/**
 * Created by User on 10-Apr-18.
 */

public class OfferList {
    int success;
    Result result;
    Args args;

    public OfferList() {
    }

    public OfferList(int success, Result result, Args args) {
        this.success = success;
        this.result = result;
        this.args = args;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }


    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Args getArgs() {
        return args;
    }

    public void setArgs(Args args) {
        this.args = args;
    }
}
