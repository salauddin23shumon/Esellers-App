package com.wstcon.gov.bd.esellers.utility;

public class EventBusMessenger {

    private boolean isFailed;
    private String msg;
    private int complete1=0;
    private int complete2 =0;


    public EventBusMessenger(boolean isFailed) {
        this.isFailed = isFailed;
    }

    public EventBusMessenger(String msg) {
        this.msg = msg;
    }

    public EventBusMessenger(int complete1, int complete2) {
        this.complete1 = complete1;
        this.complete2 = complete2;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getComplete1() {
        return complete1;
    }

    public void setComplete1(int complete1) {
        this.complete1 = complete1;
    }

    public int getComplete2() {
        return complete2;
    }

    public void setComplete2(int complete2) {
        this.complete2 = complete2;
    }
}
