package com.shun.liu.shunzhitianxia.bean;

import java.io.Serializable;

/**
 * Created by liushun on 16/12/10.
 */
public class ServerResponse implements Serializable{
    /**
     * 消息
     */
    private String msg;
    /**
     * 编码
     */
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                '}';
    }
}
