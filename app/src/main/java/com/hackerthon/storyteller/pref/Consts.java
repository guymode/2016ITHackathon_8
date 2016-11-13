package com.hackerthon.storyteller.pref;

/**
 * Created by admin on 2016-08-21.
 */
public class Consts {

    public static class naver{
        public static String OAUTH_CLIENT_ID = "EYT768Uo3L2EZwgE3SkG";
        public static String OAUTH_CLIENT_SECRET = "Fgf4ja3Por";
        public static String OAUTH_CLIENT_NAME = "StoryTeller";
        public static String NAME = "STORED_ID";
        public static String NICKNAME = "STORED_NAME";
        public static String EMAIL = "STORED_EMAIL";
    }

    public static class server{
        public static String SERVER_IP = "192.168.43.106";
        public static int SERVER_PORT = 8889;
    }

    public static class pref{
        public static int language = 0;
        public static String getName = "";
        public static String getNick = "";
        public static String getEmail = "";
    }

    public static class Msg{
        public static String RequestFeedContent = "RequestFeedContent";
        public static String ResponseFeedContent = "ResponseFeedContent";
        public static String RequestPostContent = "RequestPostContent";
        public static String ResponsePostContent = "ResponsePostContent";
        public static String RequestCommentContent = "RequestCommentContent";
        public static String ResponseCommentContent = "ResponseCommentContent";
        public static String RequestWriteCommentContent = "RequestWriteCommentContent";

    }


}
