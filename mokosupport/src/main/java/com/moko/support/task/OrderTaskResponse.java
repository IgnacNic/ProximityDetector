package com.moko.support.task;

import com.moko.support.entity.OrderType;

import java.io.Serializable;

/**
 * @Date 2018/1/23
 * @Author wenzheng.liu
 * @ClassPath com.moko.support.task.OrderTaskResponse
 */
public class OrderTaskResponse implements Serializable {
    public OrderType orderType;
    public int responseType;
    public byte[] responseValue;
}
