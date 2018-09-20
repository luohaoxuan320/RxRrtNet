package com.lehow.net;

import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.atomic.AtomicBoolean;

public class TokenRefresh {
  private AtomicBoolean isTokenRefreshing = new AtomicBoolean(false);
  private static final String TAG = "TokenRefresh";
  private PublishSubject publishSubject;
  final int MaxRetry = 3;
  int retryCount = 0;

  private TokenRefresh() {

  }

  public static TokenRefresh getInstance() {
    return LazyHolder.INSTANCE;
  }

  public Observable<Boolean> refresh(Observable tokenObservable) {
    Log.i(TAG, "refresh: ");

    if (isTokenRefreshing.compareAndSet(false, true)) {//如果正在刷新Token，新的Token刷新请求会直接返回，让业务数据一起等待
      Log.i(TAG, "refresh: 开始刷新Token");
      publishSubject = PublishSubject.create();
      tokenObservable.subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {
        @Override public void accept(Disposable disposable) throws Exception {
          Log.i(TAG, "accept: doOnSubscribe");
        }
      }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
        @Override public ObservableSource<?> apply(Observable<Throwable> throwableObservable)
            throws Exception {
          retryCount = 0;
          return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override public ObservableSource<?> apply(Throwable throwable) throws Exception {
              if (throwable instanceof ApiStateException) {
                if (((ApiStateException) throwable).getCode()
                    == ApiStateException.ERR_REFRESH_TOKEN) {//退回登录
                  return Observable.error(throwable);
                }
              }
              retryCount++;
              Log.i(TAG, "apply: refresh retryCount=" + retryCount);
              return retryCount < MaxRetry ? Observable.just(true)
                  : Observable.error(throwable);//重试刷新token,超过三次直接向上抛出错误
            }
          });
        }
      }).subscribe(publishSubject);
    } else {
      Log.i(TAG, "refresh: 请直接等待吧");
    }

    return publishSubject;
  }

  private static class LazyHolder {
    private static final TokenRefresh INSTANCE = new TokenRefresh();
  }
}
