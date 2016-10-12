package com.fang.jinan.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * 头条新闻自定义viewpager
 * Created by Administrator on 2016/10/10.
 */
public class TopNewsViewPager extends ViewPager {
    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 1. 上下滑动需要拦截 2. 向右滑动并且当前是第一个页面,需要拦截 3. 向左滑动并且当前是最后一个页面,需要拦截
     */
    //Todo
}
