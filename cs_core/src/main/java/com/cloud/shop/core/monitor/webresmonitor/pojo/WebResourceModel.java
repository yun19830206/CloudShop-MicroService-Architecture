package com.cloud.shop.core.monitor.webresmonitor.pojo;

/**
 * 实体类，代码服务器实例的属性信息。  构建者模式创建。
 * Created by ChengYun on 2017/9/16 Version 1.0
 */
public class WebResourceModel {

    /** 堆内存总数 */
    private final String heapMax;

    /** 堆内存空闲 */
    private final String heapFree;

    /** 线程数 */
    private final String threadCount;

    /** 守护线程数 */
    private final String threadDemoCount;

    /** monitor GC 次数 */
    private final String monitorGCCount;

    /** monitor GC 时间 */
    private final String monitorTimes;

    /** full GC 次数 */
    private final String fullGCCount;

    /** full GC 时间 */
    private final String fullGCTimes;

    private WebResourceModel(WebResourceModelBuilder webResourceModelBuilder) {
        this.heapMax = webResourceModelBuilder.heapMax;
        this.heapFree = webResourceModelBuilder.heapFree;
        this.threadCount = webResourceModelBuilder.threadCount;
        this.threadDemoCount = webResourceModelBuilder.threadDemoCount;
        this.monitorGCCount = webResourceModelBuilder.monitorGCCount;
        this.monitorTimes = webResourceModelBuilder.monitorTimes;
        this.fullGCCount = webResourceModelBuilder.fullGCCount;
        this.fullGCTimes = webResourceModelBuilder.fullGCTimes;
    }

    /** 必须要有此无参构造函数，不能反序列化不成功 */
    public WebResourceModel(){
        this.heapMax = null ;
        this.heapFree = null;
        this.threadCount = null;
        this.threadDemoCount = null;
        this.monitorGCCount = null;
        this.monitorTimes = null;
        this.fullGCCount = null;
        this.fullGCTimes = null;
    }

    /** WebResourceModel构造器 */
    public static class WebResourceModelBuilder {
        /** 堆内存总数 */
        private String heapMax;

        /** 堆内存空闲 */
        private String heapFree;

        /** 线程数 */
        private String threadCount;

        /** 守护线程数 */
        private String threadDemoCount;

        /** monitor GC 次数 */
        private String monitorGCCount;

        /** monitor GC 时间 */
        private String monitorTimes;

        /** full GC 次数 */
        private String fullGCCount;

        /** full GC 时间 */
        private String fullGCTimes;

        public WebResourceModelBuilder heapMax(String heapMax) {
            this.heapMax=heapMax;
            return this ;
        }
        public WebResourceModelBuilder heapFree(String heapFree) {
            this.heapFree=heapFree;
            return this ;
        }
        public WebResourceModelBuilder threadCount(String threadCount) {
            this.threadCount=threadCount;
            return this ;
        }
        public WebResourceModelBuilder threadDemoCount(String threadDemoCount) {
            this.threadDemoCount=threadDemoCount;
            return this ;
        }
        public WebResourceModelBuilder monitorGCCount(String monitorGCCount) {
            this.monitorGCCount=monitorGCCount;
            return this ;
        }
        public WebResourceModelBuilder monitorTimes(String monitorTimes) {
            this.monitorTimes=monitorTimes;
            return this ;
        }
        public WebResourceModelBuilder fullGCCount(String fullGCCount) {
            this.fullGCCount=fullGCCount;
            return this ;
        }
        public WebResourceModelBuilder fullGCTimes(String fullGCTimes) {
            this.fullGCTimes=fullGCTimes;
            return this ;
        }
        public WebResourceModel builder(){
            return new WebResourceModel(this);
        }
    }

    public String getHeapMax() {
        return heapMax;
    }
    public String getHeapFree() {
        return heapFree;
    }
    public String getThreadCount() {
        return threadCount;
    }
    public String getThreadDemoCount() {
        return threadDemoCount;
    }
    public String getMonitorGCCount() {
        return monitorGCCount;
    }
    public String getMonitorTimes() {
        return monitorTimes;
    }
    public String getFullGCCount() {
        return fullGCCount;
    }
    public String getFullGCTimes() {
        return fullGCTimes;
    }

    @Override
    public String toString() {
        return "WebResourceModel{" +
                "heapMax='" + heapMax + '\'' +
                ", heapFree='" + heapFree + '\'' +
                ", threadCount='" + threadCount + '\'' +
                ", threadDemoCount='" + threadDemoCount + '\'' +
                ", monitorGCCount='" + monitorGCCount + '\'' +
                ", monitorTimes='" + monitorTimes + '\'' +
                ", fullGCCount='" + fullGCCount + '\'' +
                ", fullGCTimes='" + fullGCTimes + '\'' +
                '}';
    }
}
