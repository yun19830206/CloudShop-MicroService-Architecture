package com.cloud.shop.core.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 获得高并发随机数的工具类
 * Created by ChengYun on 2017/7/18 Vesion 1.0
 */
public class CSRandomNumber {

    /** 工具类禁止实例化 */
    private CSRandomNumber(){}

    /**
     * 获得maxNum以内的随机数
     * @param maxNum
     * @return
     */
    public static int getRandomIntNumber(int maxNum){
        return ThreadLocalRandom.current().nextInt(maxNum);
    }
}
