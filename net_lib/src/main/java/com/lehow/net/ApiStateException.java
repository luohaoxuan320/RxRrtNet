package com.lehow.net;

public class ApiStateException extends RuntimeException {
  public static final int STATE_OK = 1;
  public static int ERR_ACCESS_TOKEN = 20014;
  public static final int ERR_REFRESH_TOKEN = 20015;
  private int code;
  private String msg;

  public ApiStateException(int code, String msg) {
    this.msg = msg;
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}
