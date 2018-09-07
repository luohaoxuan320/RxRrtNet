package com.lehow.net.net;

import io.reactivex.Maybe;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetApi {

  /**
   * 用户登录
   * @param username 用户名
   * @param password 密码
   * @return
   */
  @GET("login") Maybe<String> login(@Query("username") String username,
      @Query("password") String password);

}
