package com.kdpark.sickdan.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class OAuthUtil {
    public static final String CLIENT_NAME = "sickdan";
    public static final String NAVER_CLIENT_ID = "e9tjfZdoFfLEK7U7Z6zL";
    public static final String NAVER_CLIENT_SECRET = "igPilI1AYO";

    public static int getExpireSec(Date date) {
        long diff = date.getTime() - new Date().getTime();
        return (int) TimeUnit.MILLISECONDS.toSeconds(diff);
    }
}
