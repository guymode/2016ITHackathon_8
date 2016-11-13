package com.hackerthon.storyteller.message;

import com.hackerthon.storyteller.domain.Feed;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Hynk on 2016-11-12.
 */
public class RequestPostContent  extends Content{

//    public Feed feed;
    public String category;
    public String nick;
    public String title;
    public String content;
    public String time;

    public RequestPostContent(){    }
    public RequestPostContent(String category, String nick, String title, String content){
//        this.feed = feed;
        this.category = category;
        this.nick = nick;
        this.title = title;
        this.content = content;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(("yyyy-MM-dd hh:mm:ss"));
        time = simpleDateFormat.format(cal.getTime());
    }
}
