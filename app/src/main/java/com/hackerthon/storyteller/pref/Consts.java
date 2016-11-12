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
        public static String ID = "STORED_ID";
        public static String NAME = "STORED_NAME";
        public static String EMAIL = "STORED_EMAIL";
        public static String SERVER_IP = "STORED_IP";
        public static String GENDER = "STORED_GENDER";
        public static String AGE = "STORED_AGE";
        public static String HEIGHT = "STORED_HEIGHT";
        public static String WEIGHT = "STORED_WEIGHT";
        public static String TOTAL = "STORED_TOTAL";
        public static String AVOIDFOOD = "STORED_AVOID";
        public static String DIET = "STORED_DIET";
        public static String CARBO_REMAIN = "STORED_CARBO_REMAIN";
        public static String PROTEIN_REMAIN = "STORED_PROTEIN_REMAIN";
        public static String FAT_REMAIN = "STORED_FAT_REMAIN";
        public static String KCAL_REMAIN = "STORED_KCAL_REMAIN";
    }

    public static class Msg{
        public static String RequestFeedContent = "RequestFeedContent";
        public static String ResponseFeedContent = "ResponseFeedContent";
        public static String RequestPostContent = "RequestPostContent";
        public static String ResponsePostContent = "ResponsePostContent";

    }

    public static class food{
//        db.execSQL("CREATE TABLE FoodDB(_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "date TEXT, " + "time TEXT"
//                + "group TEXT, " + "name TEXT," + "onetime TEXT , " +"kcal TEXT," + "carbohydrate TEXT, " + "protein TEXT, "
//                + "fat TEXT, " + "sugar TEXT, " + "sodium TEXT, " + "cholesterol TEXT, " + "saturateFat TEXT, " + "transFat TEXT, " + "components TEXT, " + "fat TEXT ); ");
        public static int ID = 0;
        public static int DATE = 1;
        public static int TIME = 2;
        public static int GRUOP = 3;
        public static int NAME = 4;
        public static int ONE_TIME = 5;
        public static int KCAL = 6;
        public static int CARBOHYDRATE = 7;
        public static int PROTEIN = 8;
        public static int FAT = 9;
        public static int SUGAR = 10;
        public static int SODIUM = 11;
        public static int CHOLESTEROL = 12;
        public static int SATURATE_FAT = 13;
        public static int TRANS_FAT = 14;
        public static int COMPONENTS = 15;
    }

    public static class foodFromNum{

        public static int ID = 0;
        public static int DATE = 1;
        public static int TIME = 2;
        public static int GRUOP = 3;
        public static int NAME = 4;
        public static int ONE_TIME = 5;
        public static int KCAL = 6;
        public static int CARBOHYDRATE = 7;
        public static int PROTEIN = 8;
        public static int FAT = 9;
        public static int SUGAR = 10;
        public static int SODIUM = 11;
        public static int CHOLESTEROL = 12;
        public static int SATURATE_FAT = 13;
        public static int TRANS_FAT = 14;
        public static int COMPONENTS = 15;
    }

    public static class exercise{

        public static int ID = 0;
        public static int DATE = 1;
        public static int TIME = 2;
        public static int type = 3;
        public static int name = 4;
        public static int kcal = 5;
        public static int constant = 6;

    }

}
