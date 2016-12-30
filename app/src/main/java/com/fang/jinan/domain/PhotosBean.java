package com.fang.jinan.domain;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/20.
 */
public class PhotosBean {
    public PhotosData data;

    public class PhotosData {
        public ArrayList<PhotoNews> news;
    }

    public class PhotoNews {
        public int id;
        public String listimage;
        public String title;
    }
}
