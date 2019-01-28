package com.sri.dominospizza.Model;

/**
 * Created by Scarecrow on 2/14/2018.
 */

public class News {

    private String newsTitle;
    private String newsDetail;
    private String newsImage;
    private String time;

    public News() {
    }

    public News(String newsTitle, String newsDetail, String newsImage, String time) {
        this.newsTitle = newsTitle;
        this.newsDetail = newsDetail;
        this.newsImage = newsImage;
        this.time = time;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsDetail() {
        return newsDetail;
    }

    public void setNewsDetail(String newsDetail) {
        this.newsDetail = newsDetail;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
