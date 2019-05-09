package com.dhcc.crashlib.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 单线程单任务，后提交的任务会挤掉前面的任务
 *@package com.dhcc.commonutils.thread
 *@author jasoncool
 *@createDate 2018/11/22 14:48
 *@description 
 **/
public class SingleTaskPool {
    private static ThreadPoolExecutor pool = null;

    /*初始化线程池*/
    public static void init() {
        if (pool == null) {
            // 创建线程池。线程池的"最大池大小"和"核心池大小"都为1(THREADS_SIZE)，"线程池"的阻塞队列容量为1(CAPACITY)。
            pool = new ThreadPoolExecutor(1, 1, 20L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(1));
            // 设置线程池的拒绝策略为"DiscardOldestPolicy",当有任务添加到线程池被拒绝时，线程池会丢弃阻塞队列中末尾的任务，然后将被拒绝的任务添加到末尾
            // 因为阻塞队列中只能有一个任务存在，所以后提交的任务会直接替换掉当前正在等待的队列
            pool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        }
    }

    /*提交任务执行*/
    public static void execute(Runnable r) {
        init();
        pool.execute(r);
    }

    /* 关闭线程池*/
    public static void unInit() {
        if (pool == null || pool.isShutdown()){
            return;
        }
        pool.shutdownNow();
        pool = null;
    }
}
