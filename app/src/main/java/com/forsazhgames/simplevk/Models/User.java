package com.forsazhgames.simplevk.Models;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

/**
 * Created by Forsazhrus on 12.10.2016.
 */
public class User implements Comparable<User> {

    private String photoURL;
    private DateTime bDate;
    private String dateFormat;
    private String name;

    public User(@NonNull String name, @NonNull DateTime bDate,
                @NonNull String dateFormat, @NonNull String photoURL) {
        this.photoURL = photoURL;
        this.bDate = bDate;
        this.dateFormat = dateFormat;
        this.name = name;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public DateTime getBDate() {
        return bDate;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(User user) {
        return getBDate().minusYears(getBDate().getYear())
                .compareTo(user.getBDate().minusYears(user.getBDate().getYear()));
    }
}
