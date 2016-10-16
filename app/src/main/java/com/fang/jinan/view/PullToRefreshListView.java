package com.fang.jinan.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fang.jinan.R;
import com.fang.jinan.utils.CacheUtils;
import com.fang.jinan.utils.PrefUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 下拉刷新对应的ListView
 * Created by Administrator on 2016/10/15.
 */
public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private static final int STATE_PULL_TO_REFRESH = 1;
    private static final int STATE_RELEASE_TO_REFRESH = 2;
    private static final int STATE_REFRESHING = 3;

    private int mCurrentState = STATE_PULL_TO_REFRESH;// 当前刷新状态

    private View mHeaderView;
    private int mHeaderViewHeight;
    private int startY = -1;

    private TextView tvTitle;
    private TextView tvTime;
    private ImageView ivArrow;

    private RotateAnimation animUp;
    private RotateAnimation animDown;
    private ProgressBar pbProgress;

    private OnRefreshListener mListener;// 3. 定义成员变量,接收监听对象
    private View mFooterView;
    private int mFooterViewHeight;

    private boolean isLoadMore;// 标记是否正在加载更多

    public PullToRefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();

    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();
    }

    private void initHeaderView() {
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh,null);
        this.addHeaderView(mHeaderView);

        tvTitle = (TextView) mHeaderView.findViewById(R.id.tv_title);
        tvTime = (TextView) mHeaderView.findViewById(R.id.tv_time);
        ivArrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        pbProgress = (ProgressBar) mHeaderView.findViewById(R.id.pb_loading);

        mHeaderView.measure(0,0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);

        initAnim();
        //设置上次刷新时间
        tvTime.setText(PrefUtils.getString(getContext(),"lastRefreshTime","距离上次刷新太久了"));
    }

    /**
     * 初始化脚布局
     */
    private void initFooterView() {
        mFooterView = View.inflate(getContext(),
                R.layout.pull_to_refresh_footer, null);
        this.addFooterView(mFooterView);

        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();

        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);

        this.setOnScrollListener(this);// 滑动监听
    }

    private void initAnim() {
        animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);

        animDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);
    }

    // 设置刷新时间
    private void setCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        //本地保存上次刷新时间
        PrefUtils.setString(getContext(),"lastRefreshTime",time.toString());

        tvTime.setText(time);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                //ACTION_DOWN会被viewpager消耗掉
                if (startY == -1){
                    startY = (int) ev.getY();
                }
                // 如果是正在刷新, 跳出循环
                if (mCurrentState == STATE_REFRESHING) {
                    break;
                }

                int endY = (int) ev.getY();
                int dy = endY - startY;
                int firstVisiblePosition = getFirstVisiblePosition();// 当前显示的第一个item的位置

                //刷新条件：下拉,并且当前显示的是第一个item
                if (dy>0 && firstVisiblePosition == 0){
                    int padding = dy - mHeaderViewHeight;// 计算当前下拉控件的padding值
                    mHeaderView.setPadding(0, padding, 0, 0);

                    if (padding > 0 && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        // 改为松开刷新
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    } else if (padding < 0
                            && mCurrentState != STATE_PULL_TO_REFRESH) {
                        // 改为下拉刷新
                        mCurrentState = STATE_PULL_TO_REFRESH;
                        refreshState();
                    }
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:
                startY = -1;

                if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    refreshState();

                    // 完整展示头布局
                    mHeaderView.setPadding(0, 0, 0, 0);

                    // 4. 进行回调
                    if (mListener != null) {
                        mListener.onRefresh();
                    }

                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    // 隐藏头布局
                    mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    // 滑动状态发生变化(处理脚布局)
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {// 空闲状态
            int lastVisiblePosition = getLastVisiblePosition();

            if (lastVisiblePosition == getCount() - 1 && !isLoadMore) {// 当前显示的是最后一个item并且没有正在加载更多
                // 到底了
                //System.out.println("加载更多...");

                isLoadMore = true;

                mFooterView.setPadding(0, 0, 0, 0);// 显示加载更多的布局

                setSelection(getCount() - 1);// 将listview显示在最后一个item上,
                // 从而加载更多会直接展示出来, 无需手动滑动

                //通知主界面加载下一页数据
                if(mListener!=null) {
                    mListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    /**
     * 根据当前状态刷新界面
     */
    private void refreshState() {
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                tvTitle.setText("下拉刷新");
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.startAnimation(animDown);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tvTitle.setText("松开刷新");
                pbProgress.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.startAnimation(animUp);
                break;
            case STATE_REFRESHING:
                tvTitle.setText("正在刷新...");

                ivArrow.clearAnimation();// 清除箭头动画,否则无法隐藏

                pbProgress.setVisibility(View.VISIBLE);
                ivArrow.setVisibility(View.INVISIBLE);
                break;

            default:
                break;
        }
    }
    /**
     * 刷新结束,收起控件
     */
    public void onRefreshComplete(boolean success) {
        if (isLoadMore){
            mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

            mCurrentState = STATE_PULL_TO_REFRESH;
            tvTitle.setText("下拉刷新");
            pbProgress.setVisibility(View.INVISIBLE);
            ivArrow.setVisibility(View.VISIBLE);

            if (success) {// 只有刷新成功之后才更新时间
                setCurrentTime();
            }
        }else {
            //加载更多
            mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);//隐藏布局
            isLoadMore = false;
        }


    }



     //2. 暴露接口,设置监听
    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    //1. 下拉刷新的回调接口
    public interface OnRefreshListener {
        public void onRefresh();
        public void onLoadMore();
    }

    //4.回调  mListener.onRefresh();
}










