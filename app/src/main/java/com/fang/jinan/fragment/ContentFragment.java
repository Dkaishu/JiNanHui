package com.fang.jinan.fragment;


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


import com.fang.jinan.MainActivity;
import com.fang.jinan.R;
import com.fang.jinan.base.BasePager;
import com.fang.jinan.base.impl.HomePager;
import com.fang.jinan.base.impl.NewsCenterPager;
import com.fang.jinan.base.impl.SettingPager;
import com.fang.jinan.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

/**
 * 主页面fragment
 *
 */
public class ContentFragment extends BaseFragment {

	private NoScrollViewPager mViewPager;
	private RadioGroup rgGroup;
	private ArrayList<BasePager> mPagers;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_content, null);
		mViewPager = (NoScrollViewPager) view.findViewById(R.id.vp_content);
		rgGroup = (RadioGroup) view.findViewById(R.id.rg_group);
		return view;
	}

	@Override
	public void initData() {
		mPagers = new ArrayList<BasePager>();

		// 添加标签页
		mPagers.add(new HomePager(mActivity));
		mPagers.add(new NewsCenterPager(mActivity));
		mPagers.add(new SettingPager(mActivity));

		mViewPager.setAdapter(new ContentAdapter());

		// 底栏标签切换监听
		rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_home:
					// 首页
					// mViewPager.setCurrentItem(0);
					mViewPager.setCurrentItem(0, true);// 参2:表示是否具有滑动动画
					break;
				case R.id.rb_news:
					// 新闻中心
					mViewPager.setCurrentItem(1, true);
					break;

				case R.id.rb_setting:
					// 设置
					mViewPager.setCurrentItem(2, true);
					break;

				default:
					break;
				}
			}
		});

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				BasePager pager = mPagers.get(position);
				pager.initData();

				if (position == 0 || position == mPagers.size() - 1) {
					// 首页和设置页要禁用侧边栏
					setSlidingMenuEnable(false);
				} else {
					// 其他页面开启侧边栏
					setSlidingMenuEnable(true);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		// 手动加载第一页数据
		mPagers.get(0).initData();
		// 首页禁用侧边栏
		setSlidingMenuEnable(false);
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

	class ContentAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			BasePager pager = mPagers.get(position);
			View view = pager.mRootView;// 获取当前页面对象的布局

			// pager.initData();// 初始化数据, viewpager会默认加载下一个页面,
			// 为了节省流量和性能,不要在此处调用初始化数据的方法

			container.addView(view);

			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	// 获取新闻中心页面
	public NewsCenterPager getNewsCenterPager() {
		NewsCenterPager pager = (NewsCenterPager) mPagers.get(1);
		return pager;
	}

}
