package com.cloud.shop.core.monitor.restcallmonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 统计所有接口调用数据。
 */
@Component
public class InterfaceCallMonitor {
    private final static Logger logger = LoggerFactory.getLogger(InterfaceCallMonitor.class);

    /** 缓存最新10次服务器资源状态信息 */
    private AtomicLong interfaceCallCount = new AtomicLong();

    @Scheduled(cron="0/10 * *  * * ? ")   //每10秒执行一次,用于打印目前系统接口被调用总次数
    /**  CRON表达式    含义
     * "0 0 12 * * ?"    每天中午十二点触发
     "0 15 10 ? * *"    每天早上10：15触发
     "0 15 10 * * ?"    每天早上10：15触发
     "0 15 10 * * ? *"    每天早上10：15触发
     "0 15 10 * * ? 2005"    2005年的每天早上10：15触发
     "0 * 14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发
     "0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发
     "0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发
     "0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发
     "0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发
     "0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发
     */
    public void run(){
        logger.error("====系统当前时间["+System.currentTimeMillis()+"]接口被调用次数:"+this.interfaceCallCount);
    }

    /** 增加一次接口被调用次数 */
    public void interfaceCallAddOne(){
        interfaceCallCount.incrementAndGet();
    }



}
