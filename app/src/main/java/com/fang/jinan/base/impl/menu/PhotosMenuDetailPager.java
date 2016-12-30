package com.fang.jinan.base.impl.menu;


import android.app.Activity;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fang.jinan.R;
import com.fang.jinan.base.BaseMenuDetailPager;
import com.fang.jinan.domain.PhotosBean;
import com.fang.jinan.global.GlobalConstants;
import com.fang.jinan.utils.CacheUtils;
import com.fang.jinan.utils.Image.MyBitmapUtils;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 菜单详情页-组图
 *
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager implements
		View.OnClickListener {

	private ListView lvPhoto;
	private GridView gvPhoto;

	private ArrayList<PhotosBean.PhotoNews> mNewsList;

	private ImageButton btnPhoto;

	public PhotosMenuDetailPager(Activity activity, ImageButton btnPhoto) {
		super(activity);
		btnPhoto.setOnClickListener(this);// 组图切换按钮设置点击事件
		this.btnPhoto = btnPhoto;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_photos_menu_detail, null);

		lvPhoto = (ListView) view.findViewById(R.id.lv_photo);
		gvPhoto = (GridView) view.findViewById(R.id.gv_photo);

		return view;
	}

	@Override
	public void initData() {
		String cache = CacheUtils.getCache(GlobalConstants.PHOTOS_URL,
				mActivity);
		if (!TextUtils.isEmpty(cache)) {
			processData(cache);
		}

		getDataFromServer();
	}

	private void getDataFromServer() {

		RequestParams params = new RequestParams(GlobalConstants.PHOTOS_URL);
		x.http().get(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				processData(result);
				CacheUtils.setCache(GlobalConstants.PHOTOS_URL,result,mActivity);
				Toast.makeText(x.app(), "PhotosMenuDetailPager:从服务器获取成功"+result, Toast.LENGTH_LONG).show();

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				Toast.makeText(x.app(), "PhotosMenuDetailPager:"+ex.getMessage(), Toast.LENGTH_LONG).show();

			}

			@Override
			public void onCancelled(CancelledException cex) {
				Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onFinished() {

			}
		});

/*		HttpUtils utils = new HttpUtils();
		utils.send(HttpMethod.GET, GlobalConstants.PHOTOS_URL,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						processData(result);

						CacheUtils.setCache(GlobalConstants.PHOTOS_URL, result,
								mActivity);
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						// 请求失败
						error.printStackTrace();
						Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT)
								.show();
					}
				});*/
	}

	protected void processData(String result) {
		Gson gson = new Gson();
		PhotosBean photosBean = gson.fromJson(result, PhotosBean.class);

		mNewsList = photosBean.data.news;

		lvPhoto.setAdapter(new PhotoAdapter());
		gvPhoto.setAdapter(new PhotoAdapter());// gridview的布局结构和listview完全一致,
		// 所以可以共用一个adapter
	}

	class PhotoAdapter extends BaseAdapter {

		private MyBitmapUtils mBitmapUtils;

		public PhotoAdapter() {
			mBitmapUtils = new MyBitmapUtils();
		}

		@Override
		public int getCount() {
			return mNewsList.size();
		}

		@Override
		public PhotosBean.PhotoNews getItem(int position) {
			return mNewsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mActivity,
						R.layout.list_item_photos, null);
				holder = new ViewHolder();
				holder.ivPic = (ImageView) convertView
						.findViewById(R.id.iv_pic);
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			PhotosBean.PhotoNews item = getItem(position);

			holder.tvTitle.setText(item.title);
			mBitmapUtils.display(holder.ivPic, item.listimage);
			Log.d("111","dispaly:"+position+item.title+item.listimage);

//			x.image().bind(holder.ivPic, String.valueOf(R.drawable.pic_item_list_default), new ImageOptions.Builder().build());

			return convertView;
		}

	}

	static class ViewHolder {
		public ImageView ivPic;
		public TextView tvTitle;
	}

	private boolean isListView = true;// 标记当前是否是listview展示

	@Override
	public void onClick(View v) {
		if (isListView) {
			// 切成gridview
			lvPhoto.setVisibility(View.GONE);
			gvPhoto.setVisibility(View.VISIBLE);
			btnPhoto.setImageResource(R.drawable.icon_pic_list_type);

			isListView = false;
		} else {
			// 切成listview
			lvPhoto.setVisibility(View.VISIBLE);
			gvPhoto.setVisibility(View.GONE);
			btnPhoto.setImageResource(R.drawable.icon_pic_grid_type);

			isListView = true;
		}
	}

}

