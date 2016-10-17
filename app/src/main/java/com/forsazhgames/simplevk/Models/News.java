package com.forsazhgames.simplevk.Models;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Forsazhrus on 20.09.2016.
 */
public class News {

    private String photoURL;
    private String date;
    private String text;
    private Long like;
    private ArrayList<String> attachmentURLs;

    public News(@NonNull String photoURL, @NonNull String date, @NonNull String text,
                @NonNull Long like, ArrayList<String> attachmentURLs) {
        this.photoURL = photoURL;
        this.date = date;
        this.text = text;
        this.like = like;
        this.attachmentURLs = attachmentURLs;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public Long getLike() {
        return like;
    }

    public ArrayList<String> getAttachmentURLs() {
        return attachmentURLs;
    }
}