package com.lehow.net.net;

import com.lehow.net.converter.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * desc:
 * author: luoh17
 * time: 2018/7/14 16:56
 */
public class NetHelper {
  private static final String HOST = "http://175132y7b9.imwork.net:56557/api/";
  public static NetHelper instance = new NetHelper();
  private Retrofit retrofit;

  private NetHelper() {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    retrofit = new Retrofit.Builder().baseUrl(HOST)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(new OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .addInterceptor(new MockDataInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build())
        .build();
  }

  public <T> T create(final Class<T> service) {
    return retrofit.create(service);
  }
}
