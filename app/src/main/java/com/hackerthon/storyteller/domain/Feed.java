package com.hackerthon.storyteller.domain;

/**
 * Created by Hynk on 2016-11-12.
 */
public class Feed {
    public int id;
    public String nick;
    public String title;
    public String content;
    public String picture;
    public String time;
    public int cnt;

    public Feed(String nick, String title, String content, String time){
        this.nick = nick;
        this.title = title;
        this.content = content;
        this.time = time;
    }

}
