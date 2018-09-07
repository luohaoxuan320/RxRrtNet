# RxRrtNet

这是一个基于Rxjava+Retrofit封装的网络请求小框架，使业务层只关心自己的业务数据，而不用关心http返回的外层状态相关的结构字段

移植项目中使用时，主要修改两个类

1. 将HttpResult 中的字段用Gson的注解SerializedName映射为自己的字段
~~~
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

~~~
2. 修改STATE_OK对应的具体值

~~~
public class ApiStateException extends RuntimeException {
  public static final int STATE_OK = 1;
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

~~~

## 为何使用Maybe 而不使用Observable
因为在实际使用中发现一个恶心的问题，比如我提交数据，提交成功后，服务器只返回一个code给我，告诉我提交成功了，而entity为null。

Rxjava2中onNext里的值若为null，会抛出NPE。导致业务流程的处理不一致。

Maybe如果onNext对象为null，会执行onComplete方法，跟我们的预期是一致的。

当然还封装了一个NetMaybeObservable 将onComplete的统一到onSuccess中，简化业务数据的处理。

## 注意compose的顺序，否则会出现Dialog的处理发送在子线程上
