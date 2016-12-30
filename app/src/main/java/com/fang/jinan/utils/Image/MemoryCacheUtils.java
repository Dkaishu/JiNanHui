package com.fang.jinan.utils.Image;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Administrator on 2016/10/19.
 */
public class MemoryCacheUtils {
    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCacheUtils() {
        long maxMemory = (int) (Runtime.getRuntime().maxMemory());// 获取分配给app的内存大小，KB

        mMemoryCache = new LruCache<String, Bitmap> ((int) (maxMemory / 8)) {
            // 返回每个对象的大小，byte
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() ;
            }
        };
    }

    /**
     * 写缓存
     */
    public void setMemoryCache(String url, Bitmap bitmap) {
        // mMemoryCache.put(url, bitmap);
        // SoftReference<Bitmap> soft = new SoftReference<Bitmap>(bitmap);//
        // 使用软引用将bitmap包装起来
        // mMemoryCache.put(url, soft);
        mMemoryCache.put(url, bitmap);
    }

    /**
     * 读缓存
     */
    public Bitmap getMemoryCache(String url) {
        // SoftReference<Bitmap> softReference = mMemoryCache.get(url);
        //
        // if (softReference != null) {
        // Bitmap bitmap = softReference.get();
        // return bitmap;
        // }

        return mMemoryCache.get(url);
    }
}
