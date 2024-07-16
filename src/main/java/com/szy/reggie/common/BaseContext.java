package com.szy.reggie.common;

/**
 * 基于ThreadLocal封装的工具类，用于保存和获取当前登录用户的id
 * 作用域是在一个线程之内
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    // 当一个HTTP请求到达Tomcat服务器时，Tomcat会从其工作线程池中获取一个线程来处理这个请求
    // threadLocal是一个局部变量
    // 当用户发起多个并发请求时，Web容器会为每个请求分配一个线程。如果这些请求属于同一个用户会话，
    // 它们将共享同一个session对象
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
