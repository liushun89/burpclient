package com.shun.liu.shunzhitianxia.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by liushun on 16/12/10.
 */
public class ClientRequest implements Serializable{
    /**
     * 请求地址
     */
    private String requestURL;
    /**
     * 请求内容
     */
    private byte[] requestMsg;
    /**
     * 是否重复
     */
    private boolean repeat;
    /**
     * 重复间隔
     */
    private long repeatInterval;

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public byte[] getRequestMsg() {
        return requestMsg;
    }

    public void setRequestMsg(byte[] requestMsg) {
        this.requestMsg = requestMsg;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public long getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(long repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    @Override
    public String toString() {
        return "ClientRequest{" +
                "requestURL='" + requestURL + '\'' +
                ", requestMsg=" + Arrays.toString(requestMsg) +
                ", repeat=" + repeat +
                ", repeatInterval=" + repeatInterval +
                '}';
    }
}
