package com.nandakishor.cmpe275.lab2.utils;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "Response")
public class Response {

    int code;
    String msg;

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
