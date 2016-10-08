package com.fang.jinan.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fang.jinan.MainActivity;
import com.fang.jinan.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by Administrator on 2016/10/4.
 */
public class BasePager {
    public Activity mActivity;
    public TextView tvTitle;
    public ImageButton btnMenu;
    public FrameLayout flContent;

    public View mRootView;// 当前页面的布局对象

    public BasePager(Activity activity) {
        mActivity = activity;
        mRootView = initView();
    }

    public View initView() {

        View view = View.inflate(mActivity, R.layout.base_pager,null);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        btnMenu = (ImageButton) view.findViewById(R.id.btn_menu);
        flContent = (FrameLayout) view.findViewById(R.id.fl_content);
        btnMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        return view;
    }

    /**
     * 打开或者关闭侧边栏
     */
    protected void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();// 如果当前状态是开, 调用后就关; 反之亦然
    }

    public void initData(){};

}
