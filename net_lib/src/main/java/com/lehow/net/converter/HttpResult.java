package com.lehow.net.converter;

import com.google.gson.annotations.SerializedName;

public class HttpResult<T> {
  @SerializedName("obj")
  private T entity;
  @SerializedName("message")
  private String msg;
  private int code;

  public T getEntity() {
    return entity;
  }

  public void setEntity(T entity) {
    this.entity = entity;
  }

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
}
