package com.fang.jinan.base.impl.menu;

import android.app.Activity;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.fang.jinan.MainActivity;
import com.fang.jinan.R;
import com.fang.jinan.base.BaseMenuDetailPager;
import com.fang.jinan.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;


import java.util.ArrayList;


/**
 * 菜单详情页-新闻
 *
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener{
	private ArrayList<NewsMenu.NewsTabData> mTabData;// 页签网络数据
	private ArrayList<TabDetailPager> mPagers;// 页签页面集合
	private ViewPager mViewPager;
	private TabPageIndicator mIndicator;
	private ImageButton btn_next;

	public NewsMenuDetailPager(Activity activity,ArrayList<NewsMenu.NewsTabData> children) {
		super(activity);
		mTabData = children;
	}

	@Override
	public View initView() {

		View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
		mViewPager = (ViewPager) view.findViewById(R.id.vp_news_menu_detail);
		mIndicator = (TabPageIndicator) view.findViewById(R.id.indicator);
		btn_next = (ImageButton) view.findViewById(R.id.btn_next);

		return view;
	}

	@Override
	public void initData() {
		// 初始化页签
		mPagers = new ArrayList<TabDetailPager>();
		for (int i = 0; i < mTabData.size(); i++) {
			TabDetailPager pager = new TabDetailPager(mActivity,mTabData.get(i));
			mPagers.add(pager);
		}

		mViewPager.setAdapter(new NewsMenuDetailAdapter());
		mIndicator.setViewPager(mViewPager);// 将viewpager和指示器绑定在一起.注意:必须在viewpager设置完数据之后再绑定

		// 设置页面滑动监听
		// mViewPager.setOnPageChangeListener(this);
		mIndicator.setOnPageChangeListener(this);// 此处必须给指示器设置页面监听,不能设置给viewpager
		btn_next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int current = mViewPager.getCurrentItem();
				current++;
				mViewPager.setCurrentItem(current);
			}
		});
	}

	//ViewPager.OnPageChangeListener
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		if (position == 0) {
			// 开启侧边栏
			setSlidingMenuEnable(true);
		} else {
			// 禁用侧边栏
			setSlidingMenuEnable(false);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	/**
	 * 开启或禁用侧边栏
	 *
	 * @param enable
	 */
	protected void setSlidingMenuEnable(boolean enable) {
		// 获取侧边栏对象
		MainActivity mainUI = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		if (enable) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}


	private class NewsMenuDetailAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return mPagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			NewsMenu.NewsTabData data = mTabData.get(position);
			return data.title;
		}

		//这个函数的实现的功能是创建指定位置的页面视图。
		// 适配器有责任增加即将创建的View视图到这里给定的container中，
		// 这是为了确保在finishUpdate(viewGroup)返回时this is be done!
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TabDetailPager pager = mPagers.get(position);
			View view = pager.mRootView;
			pager.initData();
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}


	}
}
