package com.cloud.shop.core.monitor.webresmonitor.pojo;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 实现一个固定大小的队列，超过队列大小的先进的对象将自动移出队列.
 * 考虑到可能存在的线程不安全使用方法，内部用
 * Created by ChengYun on 2017/9/10 Version 1.0
 */
public class FixedSizeQueue<E> implements Queue<E> {

    /** 元素存储器 */
    private ArrayBlockingQueue<E> queue ;
    /** 元素总数量 */
    int maxNum = 0 ;
    /** 当前元素索引未知 */
    int index = 0 ;
    /** 控制并发的锁 */
    final ReentrantLock lock;


    public FixedSizeQueue(int maxNum){
        if(maxNum <1){
            queue = new ArrayBlockingQueue<E>(30);
            this.maxNum = 30;
        }else{
            queue = new ArrayBlockingQueue<E>(maxNum);
            this.maxNum = maxNum;
        }
        lock = new ReentrantLock();
    }

    /**
     * 获得固定大小队列的实际元素数量
     * @return
     */
    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /** 以数组返回数据 */
    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    /**
     * 如果
     * @param e
     * @return
     */
    @Override
    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if(index < maxNum){
                queue.add(e);
                index++ ;
                return true;
            }else{
                queue.poll();
                queue.add(e);
                return true;
            }
        } finally {
            lock.unlock();
            return false;
        }
    }

    @Override
    public E remove() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if(index < 1){
                return null;
            }else{
                index-- ;
                return queue.remove();
            }
        } finally {
            lock.unlock();
            return null;
        }
    }

    @Override
    public void clear() {
        queue.clear();
    }

    /** 暂时不实现 */
    @Override
    public boolean contains(Object o) {
        return false;
    }
    /** 暂时不实现 */
    @Override
    public Iterator<E> iterator() {
        return null;
    }
    /** 暂时不实现 */
    @Override
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }
    /** 暂时不实现 */
    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }
    /** 暂时不实现 */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }
    /** 暂时不实现 */
    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }
    /** 暂时不实现 */
    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }
    /** 暂时不实现 */
    @Override
    public boolean offer(E e) {
        return false;
    }
    /** 暂时不实现 */
    @Override
    public boolean remove(Object o) {
        return false;
    }
    /** 暂时不实现 */
    @Override
    public E poll() {
        return null;
    }
    /** 暂时不实现 */
    @Override
    public E element() {
        return null;
    }
    /** 暂时不实现 */
    @Override
    public E peek() {
        return null;
    }
}
