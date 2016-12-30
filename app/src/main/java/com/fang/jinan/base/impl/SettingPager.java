package com.fang.jinan.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fang.jinan.base.BasePager;
import com.fang.jinan.utils.PrefUtils;


/**
 * 设置
 *
 */
public class SettingPager extends BasePager {

	public SettingPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {

		// 要给帧布局填充布局对象
		TextView view = new TextView(mActivity);
		view.setText("设置");
		view.setTextColor(Color.RED);
		view.setTextSize(22);
		view.setGravity(Gravity.CENTER);

		Button bt = new Button(mActivity);
		bt.setText("清除数据");
		bt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PrefUtils.clearAll(mActivity);
			}
		});

		flContent.addView(view);
		flContent.addView(bt);

		// 修改页面标题
		tvTitle.setText("设置");

		// 隐藏菜单按钮
		btnMenu.setVisibility(View.GONE);
	}

}
