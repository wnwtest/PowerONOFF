package com.example.poweronoff.customer;

import android.content.Context;
import android.graphics.Color;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.util.Calendar;

public class MyTimePicker {
    public MyTimePicker() {
    }

    public TimePickerView getPicker(Context mContext, OnTimeSelectListener listener) {
        Calendar cale =Calendar.getInstance();
        int year = cale.get(Calendar.YEAR);
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(year, 0, 1);
        Calendar endDate = Calendar.getInstance();
        endDate.set(year+1, 11, 31);
        return new TimePickerBuilder(mContext, listener)
                .setType(new boolean[]{true, true, true, true, true, true})//分别对应年月日时分秒，默认全部显示
                .setCancelText("0000")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(14)//标题文字大小
                .setTitleText("选择时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setLineSpacingMultiplier(2f)
                .setTextColorCenter(Color.parseColor("#5CACEE"))
                .setDividerColor(Color.parseColor("#5CACEE"))
                .setTitleColor(Color.parseColor("#515151"))//标题文字颜色
                .setSubmitColor(Color.parseColor("#5CACEE"))//确定按钮文字颜色
                .setCancelColor(Color.BLUE)//取消按钮文字颜色
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isDialog(false)//是否显示为对话框样式
                .build();
    }
}
