package com.lehow.net;

import android.util.Log;
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
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.Callable;

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


}
