package com.hackerthon.storyteller.message;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Message {

    public String name;
    public String email;
    public String nick;
    public String type;
    public String Time;
    public String contentType;
    public Content content;

    public Message(String contentType){
        name = 0+"";
        this.contentType = contentType;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(("yyyy-MM-dd hh:mm:ss"));
        Time = simpleDateFormat.format(cal.getTime());
    }
    
    public Message()
    {
    }
    
    @Override
    public String toString() {
        return "content: " + content.toString();
    }

}
