package com.lehow.net;

import android.util.Log;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.Callable;
import org.reactivestreams.Publisher;

public class RxTransformer {

  private static final String TAG = "RxTransformer";

  private static class TransfIoMain {
    private static final MaybeTransformer TRANSFORMER = new MaybeTransformer() {
      @Override public MaybeSource apply(Maybe upstream) {
        return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
      }
    };
  }

  public static <T> MaybeTransformer io_main() {
    return TransfIoMain.TRANSFORMER;
  }

  public static <T> MaybeTransformer waitLoading(final ILoadingView iLoadingView) {

    return new MaybeTransformer<T, T>() {

      @Override public MaybeSource<T> apply(final Maybe<T> upstream) {
        return Maybe.using(new Callable<ILoadingView>() {
          @Override public ILoadingView call() throws Exception {
            Log.i(TAG, "show: "+Thread.currentThread());
            iLoadingView.show();
            return iLoadingView;
          }
        }, new io.reactivex.functions.Function<ILoadingView, MaybeSource<? extends T>>() {
          @Override public MaybeSource<? extends T> apply(ILoadingView iLoadingView)
              throws Exception {
            Log.i(TAG, "dowork: "+Thread.currentThread());
            return upstream;
          }
        }, new Consumer<ILoadingView>() {
          @Override public void accept(ILoadingView iLoadingView) throws Exception {
            Log.i(TAG, "dismiss: "+Thread.currentThread());
            iLoadingView.dismiss();
          }
        });
      }
    };
  }

  public static <T> MaybeTransformer retryToken(final Maybe tokenObservable) {
    return new MaybeTransformer<T, T>() {
      @Override public MaybeSource<T> apply(Maybe<T> upstream) {

        return upstream.retryWhen(new Function<Flowable<Throwable>, Publisher<?>>() {
          @Override public Publisher<?> apply(Flowable<Throwable> throwableFlowable)
              throws Exception {
            return throwableFlowable.flatMap(new Function<Throwable, Publisher<?>>() {
              @Override public Publisher<?> apply(Throwable throwable) throws Exception {
                if (throwable instanceof ApiStateException) {
                  int errCode = ((ApiStateException) throwable).getCode();
                  if (errCode == ApiStateException.ERR_ACCESS_TOKEN) {
                    Log.i(TAG, "retryToken 等待Token刷新: " + Thread.currentThread());
                    return TokenRefresh.getInstance()
                        .refresh(tokenObservable.toObservable())
                        .toFlowable(BackpressureStrategy.DROP);//等待刷新token
                  } else if (errCode == ApiStateException.ERR_REFRESH_TOKEN) {
                    //退回登录
                  }
                }
                return Maybe.error(throwable).toFlowable();//直接返回错误
              }
            });
          }
        });
      }
    };
  }

  public static <T> ObservableTransformer retryToken(final Observable tokenObservable) {
    return new ObservableTransformer<T, T>() {

      @Override public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
          @Override public ObservableSource<?> apply(Observable<Throwable> throwableObservable)
              throws Exception {
            Log.i(TAG, "retryToken  retryWhen: " + Thread.currentThread());
            return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
              @Override public ObservableSource<?> apply(final Throwable throwable)
                  throws Exception {
                if (throwable instanceof ApiStateException) {
                  int errCode = ((ApiStateException) throwable).getCode();
                  if (errCode == ApiStateException.ERR_ACCESS_TOKEN) {
                    Log.i(TAG, "retryToken 等待Token刷新: " + Thread.currentThread());
                    return TokenRefresh.getInstance().refresh(tokenObservable);//等待刷新token
                  } else if (errCode == ApiStateException.ERR_REFRESH_TOKEN) {
                    //退回登录

                  }
                }
                return Observable.error(throwable);//直接返回错误
              }
            });
          }
        });
      }
    };
  }

}
