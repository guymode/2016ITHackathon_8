package com.hackerthon.storyteller.message;

/**
 * Created by Hynk on 2016-11-13.
 */
public class RequestWriteCommentContent extends Content {
    public String nick;
    public String content;
    public int id;

    public RequestWriteCommentContent(int id, String nick, String content){
        this.id = id;
        this.nick = nick;
        this.content = content;
    }
}
