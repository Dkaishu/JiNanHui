package com.fang.jinan.fragment;


import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fang.jinan.MainActivity;
import com.fang.jinan.R;
import com.fang.jinan.base.impl.NewsCenterPager;
import com.fang.jinan.domain.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

/**
 * 侧边栏fragment
 *
 */
public class LeftMenuFragment extends BaseFragment {

	private ListView lvList;

	private ArrayList<NewsMenu.NewsMenuData> mNewsMenuData;// 侧边栏网络数据对象

	private int mCurrentPos;// 当前被选中的item的位置

	private LeftMenuAdapter mAdapter;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_left_menu, null);
		lvList = (ListView) view.findViewById(R.id.lv_list);
		return view;
	}

	@Override
	public void initData() {
	}

	// 给侧边栏设置数据
	public void setMenuData(ArrayList<NewsMenu.NewsMenuData> data) {
		mCurrentPos = 0;//当前选中的位置归零
		
		// 更新页面
		mNewsMenuData = data;

		mAdapter = new LeftMenuAdapter();
		lvList.setAdapter(mAdapter);

		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentPos = position;// 更新当前被选中的位置
				mAdapter.notifyDataSetChanged();// 刷新listview

				// 收起侧边栏
				toggle();

				// 侧边栏点击之后, 要修改新闻中心的FrameLayout中的内容
				setCurrentDetailPager(position);
			}
		});
	}

	/**
	 * 设置当前菜单详情页
	 * 
	 * @param position
	 */
	protected void setCurrentDetailPager(int position) {
		// 获取新闻中心的对象
		MainActivity mainUI = (MainActivity) mActivity;
		// 获取ContentFragment
		ContentFragment fragment = mainUI.getContentFragment();
		// 获取NewsCenterPager
		NewsCenterPager newsCenterPager = fragment.getNewsCenterPager();
		// 修改新闻中心的FrameLayout的布局
		newsCenterPager.setCurrentDetailPager(position);
	}

	/**
	 * 打开或者关闭侧边栏
	 */
	protected void toggle() {
		MainActivity mainUI = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		slidingMenu.toggle();// 如果当前状态是开, 调用后就关; 反之亦然
	}

	class LeftMenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mNewsMenuData.size();
		}

		@Override
		public NewsMenu.NewsMenuData getItem(int position) {
			return mNewsMenuData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(mActivity, R.layout.list_item_left_menu, null);
			TextView tvMenu = (TextView) view.findViewById(R.id.tv_menu);

			NewsMenu.NewsMenuData item = getItem(position);
			tvMenu.setText(item.title);

			if (position == mCurrentPos) {
				// 被选中
				tvMenu.setEnabled(true);// 文字变为红色
			} else {
				// 未选中
				tvMenu.setEnabled(false);// 文字变为白色
			}

			return view;
		}

	}

}
