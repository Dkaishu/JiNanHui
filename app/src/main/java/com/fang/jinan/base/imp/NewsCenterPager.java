package com.fang.jinan.base.imp;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fang.jinan.MainActivity;
import com.fang.jinan.base.BasePager;
import com.fang.jinan.base.imp.menu.InteractMenuDetailPager;
import com.fang.jinan.base.imp.menu.NewsMenuDetailPager;
import com.fang.jinan.base.imp.menu.PhotosMenuDetailPager;
import com.fang.jinan.base.imp.menu.TopicMenuDetailPager;
import com.fang.jinan.domain.NewsMenu;
import com.fang.jinan.fragment.LeftMenuFragment;
import com.fang.jinan.global.GlobalConstants;
import com.fang.jinan.utils.CacheUtils;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;


/**
 * 设置
 *
 */
public class NewsCenterPager extends BasePager {
	public NewsMenu mNewsData;
	private ArrayList<com.fang.jinan.base.imp.BaseMenuDetailPager> mMenuDetailPagers;// 菜单详情页集合

	public NewsCenterPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {

		// 修改页面标题
		tvTitle.setText("新闻");

		// 显示菜单按钮
		btnMenu.setVisibility(View.VISIBLE);

		// 先判断有没有缓存,如果有的话,就加载缓存
		String cache = CacheUtils.getCache(GlobalConstants.CATEGORY_URL,
				mActivity);
		if (!TextUtils.isEmpty(cache)) {
			processData(cache);
		}

		// 请求服务器,获取数据
		// 开源框架: XUtils3
		getDataFromServer();
	}
	private void getDataFromServer() {

		RequestParams params = new RequestParams(GlobalConstants.CATEGORY_URL);
		x.http().get(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
				// 解析数据
				processData(result);
				//写入缓存
				CacheUtils.setCache(GlobalConstants.CATEGORY_URL,result,mActivity);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onCancelled(Callback.CancelledException cex) {
				Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFinished() {

			}
		});
	}

	/**
	 * 解析数据
	 */
	protected void processData(String json) {
		// Gson: Google Json
		Gson gson = new Gson();
		mNewsData = gson.fromJson(json, NewsMenu.class);//Todo
		//System.out.println("解析结果:" + mNewsData);

		// 获取侧边栏对象
		MainActivity mainUI = (MainActivity) mActivity;
		LeftMenuFragment fragment = mainUI.getLeftMenuFragment();

		// 给侧边栏设置数据
		fragment.setMenuData(mNewsData.data);

		// 初始化4个菜单详情页
		mMenuDetailPagers = new ArrayList<com.fang.jinan.base.imp.BaseMenuDetailPager>();
		mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity));
		mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));
		mMenuDetailPagers.add(new PhotosMenuDetailPager(mActivity));
		mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));

		// 将新闻菜单详情页设置为默认页面
		setCurrentDetailPager(0);
	}

	// 设置菜单详情页
	public void setCurrentDetailPager(int position) {
		// 重新给frameLayout添加内容
		com.fang.jinan.base.imp.BaseMenuDetailPager pager = mMenuDetailPagers.get(position);// 获取当前应该显示的页面
		View view = pager.mRootView;// 当前页面的布局

		// 清除之前旧的布局
		flContent.removeAllViews();

		flContent.addView(view);// 给帧布局添加布局

		// 初始化页面数据
		pager.initData();

		// 更新标题
		tvTitle.setText(mNewsData.data.get(position).title);
	}
}
