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
    private static final ObservableTransformer TRANSFORMER = new ObservableTransformer() {
      @Override public ObservableSource apply(Observable upstream) {
        return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
      }
    };
  }

  public static <T> ObservableTransformer io_main() {
    return TransfIoMain.TRANSFORMER;
  }

  public static <T> ObservableTransformer waitLoading(final ILoadingView iLoadingView) {

    return new ObservableTransformer<T, T>() {

      @Override public ObservableSource<T> apply(final Observable<T> upstream) {
        return Observable.using(new Callable<ILoadingView>() {
          @Override public ILoadingView call() throws Exception {
            Log.i(TAG, "show: "+Thread.currentThread());
            iLoadingView.show();
            return iLoadingView;
          }
        }, new io.reactivex.functions.Function<ILoadingView, ObservableSource<? extends T>>() {
          @Override public ObservableSource<? extends T> apply(ILoadingView iLoadingView)
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
