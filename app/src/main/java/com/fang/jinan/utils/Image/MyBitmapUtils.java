package com.fang.jinan.utils.Image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.fang.jinan.R;

/**
 * Created by Administrator on 2016/10/20.
 */
public class MyBitmapUtils {

    private NetCacheUtils mNetCacheUtils;
    private DiskCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public MyBitmapUtils() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new DiskCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    public void display(ImageView imageView, String url) {
        // 设置默认图片
        imageView.setImageResource(R.drawable.pic_item_list_default);

        // 优先从内存中加载图片, 速度最快
        Bitmap bitmap = mMemoryCacheUtils.getMemoryCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            System.out.println("从内存加载图片啦");
            return;
        }

        // 其次从本地(sdcard)加载图片, 速度快
        bitmap = mLocalCacheUtils.getLocalCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            System.out.println("从本地加载图片啦");

            // 写内存缓存
            mMemoryCacheUtils.setMemoryCache(url, bitmap);
            return;
        }

        // 最后从网络下载图片, 速度慢, 浪费流量
        mNetCacheUtils.getBitmapFromNet(imageView, url);
        System.out.println("从网络加载图片啦");

    }
}
