package com.lehow.net.net;

import com.google.gson.Gson;
import com.lehow.net.converter.HttpResult;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * desc:
 * author: luoh17
 * time: 2018/9/7 9:48
 */
public class MockDataInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    HttpResult<String> httpResult = new HttpResult<>();
    httpResult.setCode(0);
    httpResult.setMsg("请求成功");
    httpResult.setEntity("AAA");
    return new Response.Builder().request(chain.request())
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .body(ResponseBody.create(MediaType.parse("application/x-www-form-urlencoded"),
            new Gson().toJson(httpResult)))
        .message("Test Message")
        .build();
  }
}
