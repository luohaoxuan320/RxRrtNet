package com.lehow.net;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lehow.net.net.LoadingDialogFragment;
import com.lehow.net.net.NetApi;
import com.lehow.net.net.NetHelper;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * desc:
 * author: luoh17
 * time: 2018/7/6 15:29
 */
public abstract class BaseActivity extends AppCompatActivity implements ILoadingView {
  FrameLayout content;
  protected NetApi netApi;
  private LoadingDialogFragment dialogFragment;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    Log.i("TAG", "onCreate: ");
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.activity_base);
    content = findViewById(R.id.content);
    final NetApi realNetApi = NetHelper.instance.create(NetApi.class);
    netApi = realNetApi;
    //动态代理，给请求处理 线程调度和 弹框
    netApi = (NetApi) Proxy.newProxyInstance(realNetApi.getClass().getClassLoader(),
        realNetApi.getClass().getInterfaces(), new InvocationHandler() {

          @Override public Object invoke(Object proxy, final Method method, final Object[] args)
              throws Throwable {
            return Maybe.just(true).flatMap(new Function<Boolean, MaybeSource<?>>() {
              @Override public MaybeSource<?> apply(Boolean aBoolean) throws Exception {
                Maybe<?> invoke = (Maybe<?>) method.invoke(realNetApi, args);//执行原来的方法
                    return invoke;
                  }
                })
                .compose(RxTransformer.waitLoading(BaseActivity.this))
                .compose(RxTransformer.io_main())
                ;
          }
        });
  }

  @Override public void setTitle(CharSequence title) {
    Log.i("TAG", "setTitle: "+title);
  }

  @Override public void setContentView(int layoutResID) {
    setContentView(getLayoutInflater().inflate(layoutResID, null, false));
  }

  @Override public void setContentView(View view) {
    content.addView(view);
    setTitle(getTitle());
  }

  @Override public void dismiss() {
    if (dialogFragment != null) dialogFragment.dismissAllowingStateLoss();
  }

  @Override public void show() {
    if (dialogFragment == null) dialogFragment = new LoadingDialogFragment();
    dialogFragment.show(getSupportFragmentManager(), "loading");
  }
}
