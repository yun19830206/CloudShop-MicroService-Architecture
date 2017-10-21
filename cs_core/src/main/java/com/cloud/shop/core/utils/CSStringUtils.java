package com.cloud.shop.core.utils;

/**
 * 字符串工具类
 * Created by ChengYun on 2017/8/29 Version 1.0
 */
public final class CSStringUtils {

    private CSStringUtils(){}

    /**
     * 判断两个字符串是否相等
     * @param s1 字符串1
     * @param s2 字符串2
     * @return boolean
     */
    public static boolean equals(String s1, String s2){
        return s1 == null ? s2 == null : s1.equals(s2);
    }


}
