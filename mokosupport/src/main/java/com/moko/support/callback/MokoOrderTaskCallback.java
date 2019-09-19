package com.moko.support.callback;

import com.moko.support.task.OrderTaskResponse;

/**
 * @Date 2017/5/10
 * @Author wenzheng.liu
 * @Description Return data callback class
 * @ClassPath com.moko.support.callback.OrderCallback
 */
public interface MokoOrderTaskCallback {

    void onOrderResult(OrderTaskResponse response);

    void onOrderTimeout(OrderTaskResponse response);

    void onOrderFinish();
}
