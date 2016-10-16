package com.fang.jinan.base.impl.menu;

import android.app.Activity;
import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fang.jinan.R;
import com.fang.jinan.domain.NewsMenu;
import com.fang.jinan.domain.NewsTabBean;
import com.fang.jinan.global.GlobalConstants;
import com.fang.jinan.utils.CacheUtils;
import com.fang.jinan.view.PullToRefreshListView;
import com.fang.jinan.view.TopNewsViewPager;
import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/9.
 */
public class TabDetailPager {
    public Activity mActivity;
    public View mRootView;

    private NewsMenu.NewsTabData mTabData;
    private String mUrl;
    private String mMoreUrl;
    private TopNewsViewPager mViewPager;

    private CirclePageIndicator mIndicator;

    private TextView tvTitle;
    private PullToRefreshListView lvList;

    private ArrayList<NewsTabBean.TopNews> mTopNews;
    private ArrayList<NewsTabBean.NewsData> mNewsList;

    private NewsAdapter mNewsAdapter;


    /**
     *
     * @param activity
     * @param newsTabData Tab数据
     */
    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        mActivity = activity;
        mRootView = initView();
        mTabData = newsTabData;
        mUrl = GlobalConstants.SERVER_URL + mTabData.url;
    }

    // 初始化布局,必须子类实现
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
        lvList = (PullToRefreshListView) view.findViewById(R.id.lv_list);

        View mHeaderView = View.inflate(mActivity, R.layout.list_item_header, null);
        mViewPager = (TopNewsViewPager) mHeaderView.findViewById(R.id.vp_top_news);
        mIndicator = (CirclePageIndicator) mHeaderView.findViewById(R.id.indicator);
        tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);

        lvList.addHeaderView(mHeaderView);

        // 5. 前端界面设置回调
        lvList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override// 刷新数据
            public void onRefresh() {

                getDataFromServer();
            }
            @Override
            public void onLoadMore() {
                // 判断是否有下一页数据
                if (mMoreUrl != null) {
                    // 有下一页
                    getMoreDataFromServer();
                } else {
                    // 没有下一页
                    Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT)
                            .show();
                    // 没有数据时也要收起控件
                    lvList.onRefreshComplete(true);
                }
            }
        });



        return view;
    }

    // 初始化数据
    public void initData() {
        String cache = CacheUtils.getCache(mUrl,mActivity);
        if (!TextUtils.isEmpty(cache)){
            processData(cache,false);
        }else {
// Todo 自动刷新？           getDataFromServer();
        }

    }

    //解析json数据
    private void processData(String json,boolean hasMore) {
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(json,NewsTabBean.class);

        String moreUrl = newsTabBean.data.more;
        if (!TextUtils.isEmpty(moreUrl)) {
            mMoreUrl = GlobalConstants.SERVER_URL + moreUrl;
        } else {
            mMoreUrl = null;
        }

        if (!hasMore){
            //分别给头条新闻mTopNews和新闻列表mNewsList填充数据
            mTopNews = newsTabBean.data.topnews;
            if (mTopNews != null){
                mViewPager.setAdapter(new TopNewsAdapter());
                mIndicator.setViewPager(mViewPager);
                mIndicator.setSnap(true);//快照方式显示
                // 事件要设置给Indicator
                mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        NewsTabBean.TopNews topNews = mTopNews.get(position);
                        tvTitle.setText(topNews.title);// 更新头条新闻标题
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
//Todo 默认让第一个选中(解决页面销毁后重新初始化时,Indicator仍然保留上次圆点位置的bug)

            }else {}

            mNewsList = newsTabBean.data.news;
            if (mNewsList != null){
                mNewsAdapter = new NewsAdapter();
                lvList.setAdapter(mNewsAdapter);
            }

            //Todo 轮播图轮播
        }else {
            // 加载更多数据
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
            mNewsList.addAll(moreNews);// 将数据追加在原来的集合中
            // 刷新listview
            mNewsAdapter.notifyDataSetChanged();
        }



    }

    private void getDataFromServer(){
        RequestParams params = new RequestParams(mUrl);
        //Toast.makeText(x.app(), "TabDetailPager:"+mUrl, Toast.LENGTH_LONG).show();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result,false);
                CacheUtils.setCache(mUrl,result,mActivity);
                Toast.makeText(x.app(), "TabDetailPager:"+result, Toast.LENGTH_LONG).show();
                // 收起下拉刷新控件
                lvList.onRefreshComplete(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), "TabDetailPager:"+ex.getMessage(), Toast.LENGTH_LONG).show();
                // 收起下拉刷新控件
                lvList.onRefreshComplete(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取下一页数据
     */
    protected void getMoreDataFromServer() {
        RequestParams params = new RequestParams(mMoreUrl);
        //Toast.makeText(x.app(), "TabDetailPager:"+mUrl, Toast.LENGTH_LONG).show();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result,true);
                CacheUtils.setCache(mMoreUrl,result,mActivity);
                Toast.makeText(x.app(), "TabDetailPager(more):"+result, Toast.LENGTH_LONG).show();
                // 收起下拉刷新控件
                lvList.onRefreshComplete(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(x.app(), "TabDetailPager(more):"+ex.getMessage(), Toast.LENGTH_LONG).show();
                // 收起下拉刷新控件
                lvList.onRefreshComplete(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinished() {

            }
        });
 /*       HttpUtils utils = new HttpUtils();
        utils.send(HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result, true);

                // 收起下拉刷新控件
                lvList.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                // 请求失败
                error.printStackTrace();
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();

                // 收起下拉刷新控件
                lvList.onRefreshComplete(false);
            }
        });*/
    }

    private class NewsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold viewHold;

            if (convertView == null){
                convertView = View.inflate(mActivity,R.layout.list_item_news,null);
                viewHold = new ViewHold();
                viewHold.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHold.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                viewHold.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
                convertView.setTag(viewHold);
            }else {
                viewHold = (ViewHold) convertView.getTag();
            }

            NewsTabBean.NewsData newsData = (NewsTabBean.NewsData) getItem(position);
            viewHold.tvTitle.setText(newsData.title);
            viewHold.tvDate.setText(newsData.pubdate);

            //利用BitmapUtils加载图片
            x.image().bind(viewHold.ivIcon,
                    newsData.listimage,
                    new ImageOptions.Builder().build());

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }
    }

    private static class ViewHold {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvDate;
    }

    private class TopNewsAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mTopNews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(mActivity);
            // view.setImageResource(R.drawable.topnews_item_default);
            view.setScaleType(ImageView.ScaleType.FIT_XY);// 设置图片缩放方式, 宽高填充父控件

            String imageUrl = mTopNews.get(position).topimage;// 图片下载链接

            // 下载图片-将图片设置给imageview-避免内存溢出-缓存
            // BitmapUtils-XUtils3
            ImageOptions options = new ImageOptions.Builder()
                    //设置加载过程中的图片
                    .setLoadingDrawableId(R.drawable.news_pic_default)
                    //设置加载失败后的图片
                    .setFailureDrawableId(R.drawable.news_pic_default)
                    //设置使用缓存
                    .setUseMemCache(true)
                    //设置显示圆形图片
                    .setCircular(false)
                    //设置支持gif
                    .setIgnoreGif(false)
                    .build();
            x.image().bind(view,imageUrl,options);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);

        }
    }
}










