package com.cloud.shop.core.test.pojo;

/**
 * 记录每个线程的执行结果：最小请求时间; 最大请求时间; 平均耗时
 * Created by ChengYun on 2017/10/15 Version 1.0
 */
public class HttpRequestResultModel {

    /** 线程名 */
    private String threadName ;
    /** 请求最小花费时间 */
    private int minRequestUsedTime ;
    /** 请求最大花费时间 */
    private int maxRequestUsedTime ;
    /** 请求平均花费时间 */
    private int requestAverageUsedTume ;

    public HttpRequestResultModel(){}

    public HttpRequestResultModel(String threadName, int minRequestUsedTime, int maxRequestUsedTime, int requestAverageUsedTume) {
        this.threadName = threadName;
        this.minRequestUsedTime = minRequestUsedTime;
        this.maxRequestUsedTime = maxRequestUsedTime;
        this.requestAverageUsedTume = requestAverageUsedTume;
    }

    public HttpRequestResultModel setThreadName(String threadName) {
        this.threadName = threadName;
        return this;
    }
    public HttpRequestResultModel setMinRequestUsedTime(int minRequestUsedTime) {
        this.minRequestUsedTime = minRequestUsedTime;
        return this;
    }
    public HttpRequestResultModel setMaxRequestUsedTime(int maxRequestUsedTime) {
        this.maxRequestUsedTime = maxRequestUsedTime;
        return this;
    }
    public HttpRequestResultModel setRequestAverageUsedTime(int requestAverageUsedTume) {
        this.requestAverageUsedTume = requestAverageUsedTume;
        return this;
    }

    @Override
    public String toString() {
        return "HttpRequestResultModel{" +
                "threadName='" + threadName + '\'' +
                ", minRequestUsedTime=" + minRequestUsedTime +
                ", maxRequestUsedTime=" + maxRequestUsedTime +
                ", requestAverageUsedTume=" + requestAverageUsedTume +
                '}';
    }

    public String getThreadName() {
        return threadName;
    }

    public int getMinRequestUsedTime() {
        return minRequestUsedTime;
    }

    public int getMaxRequestUsedTime() {
        return maxRequestUsedTime;
    }

    public int getRequestAverageUsedTime() {
        return requestAverageUsedTume;
    }
}
