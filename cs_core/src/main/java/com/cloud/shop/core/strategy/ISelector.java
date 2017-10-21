package com.cloud.shop.core.strategy;

import java.util.List;

/**
 * 挑选算法接口
 * Created by ChengYun on 2017/8/29 Version 1.0
 */
public interface ISelector {

    /**
     * 在一个集合中，通过某个挑选算法，获得一个合适数据
     * @param all T的结合
     * @param <T> T泛型
     * @return T
     */
    <T> T getInstance(List<T> all);
}
