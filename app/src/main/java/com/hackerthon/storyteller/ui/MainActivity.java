package com.hackerthon.storyteller.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hackerthon.storyteller.R;
import com.hackerthon.storyteller.adapter.FeedListAdapter;
import com.hackerthon.storyteller.domain.Feed;
import com.hackerthon.storyteller.message.*;
import com.hackerthon.storyteller.pref.Consts;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends TabActivity {

    private String TAG = "MainActivity";
    private SharedPreferences mSharedPreferences;
    private MassgeHandler mMainHandler = null;

    public static List<Feed> tempFeed;
    public static int tempMsgWhat;
    public static String currentCategory = "";

    private ListView lv_board, lv_game, lv_trade, lv_hangout, lv_study;
    private Button btn_post;

    FeedListAdapter mFeedListAdapter;

    private ImageButton changeButton;

    private DataOutputStream os;
    private InputStream is;
    private static final int PORT = Consts.server.SERVER_PORT;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logo);

        tempFeed = new ArrayList<Feed>();
        tempMsgWhat = 0;

//        changeButton = (ImageButton) findViewById(R.id.tb_btn_setting);
//        changeButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if(Consts.pref.language == 1) {
//                    Consts.pref.language = 0;
//                }
//                else{
//                    Consts.pref.language = 1;
//                }
//            }
//        });


        mMainHandler = new MassgeHandler();
        lv_board = (ListView) findViewById(R.id.lv_recom_board);
        lv_game = (ListView) findViewById(R.id.lv_recom_game);
        lv_trade = (ListView) findViewById(R.id.lv_recom_trade);
        lv_hangout = (ListView) findViewById(R.id.lv_recom_hangout);
        lv_study = (ListView) findViewById(R.id.lv_recom_study);
        btn_post = (Button) findViewById(R.id.btn_main_post);

        btn_post.setText(Consts.pref.getNick + "님의 스토리를 들려주세요!");
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPostDlg();
            }
        });

        _init();
    }

    private void showPostDlg() {

        LayoutInflater inflater = getLayoutInflater();
        final View view_enroll = inflater.inflate(R.layout.dialog_post, null);

        AlertDialog.Builder inBuilder = new AlertDialog.Builder(MainActivity.this);

        final EditText et_title = (EditText) view_enroll.findViewById(R.id.et_post_title);
        final EditText et_content = (EditText) view_enroll.findViewById(R.id.et_post_content);

        inBuilder.setPositiveButton("저장" ,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO 디비에 저장

                        Thread postThread = new Thread(new PostThread(et_title.getText().toString(), et_content.getText().toString()));
                        postThread.start();
                        dialog.dismiss();
                    }
                });


        inBuilder.setNegativeButton("취소" ,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        inBuilder.setView(view_enroll);
        inBuilder.show();
    }

    private void _init() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ImageView boardView = new ImageView(this);
        boardView.setImageResource(R.drawable.tab_board_xml);
        boardView.setMaxHeight(10);
        ImageView gameView = new ImageView(this);
        gameView.setImageResource(R.drawable.tab_events_xml);
        gameView.setMaxHeight(10);
        ImageView tradeView = new ImageView(this);
        tradeView.setImageResource(R.drawable.tab_trade_xml);
        tradeView.setMaxHeight(10);
        ImageView hangoutView = new ImageView(this);
        hangoutView.setImageResource(R.drawable.tab_hangout_xml);
        hangoutView.setMaxHeight(10);
        ImageView studyView = new ImageView(this);
        studyView.setImageResource(R.drawable.tab_study_xml);
        studyView.setMaxHeight(5);

        TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("자유").setIndicator(boardView).setContent(R.id.lv_recom_board));             // Tab 생성
        tabHost.addTab(tabHost.newTabSpec("게임").setIndicator(gameView).setContent(R.id.lv_recom_game));
        tabHost.addTab(tabHost.newTabSpec("트레이드").setIndicator(tradeView).setContent(R.id.lv_recom_trade));
        tabHost.addTab(tabHost.newTabSpec("행아웃").setIndicator(hangoutView).setContent(R.id.lv_recom_hangout));
        tabHost.addTab(tabHost.newTabSpec("스터디").setIndicator(studyView).setContent(R.id.lv_recom_study));

        tabHost.setCurrentTab(0);


        Thread sendThread = new Thread(new SenderThread("자유"));
        currentCategory = "자유";
        sendThread.start();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                Log.d(TAG, "TAB ID: " + tabId);
                currentCategory = tabId;

                Thread sendThread = new Thread(new SenderThread(tabId));
                sendThread.start();
            }
        });
    }


    class PostThread extends Thread {
        String title, content;

        public PostThread(String title, String content) {
            this.title = title;
            this.content = content;
        }

        @Override
        public void run() {
            super.run();
            try {
                socket = new Socket(InetAddress.getByName(Consts.server.SERVER_IP), PORT);
                os = new DataOutputStream(socket.getOutputStream());
                Message sendMsg;

                sendMsg = new Message(Consts.Msg.RequestPostContent);
//                Feed feed = new Feed(Consts.pref.getNick, title, content, );
                sendMsg.content = new RequestPostContent(currentCategory, Consts.pref.getNick, title, content);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                String sendString = gson.toJson(sendMsg);
                System.out.println("SendString:" + sendString);

                os.writeUTF(sendString);
                os.flush();
//
//                is = socket.getInputStream();
//                ObjectInputStream ois = new ObjectInputStream(is);

//                is.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Thread sendThread = new Thread(new SenderThread(currentCategory));
            sendThread.start();
        }

    }

    public class SenderAsyncTask extends AsyncTask<String, String, String> {

        String keyword = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            for (String string : strings) {
                keyword = string;
            }
            try {

                socket = new Socket(InetAddress.getByName(Consts.server.SERVER_IP), PORT);
                os = new DataOutputStream(socket.getOutputStream());
                Message sendMsg;

                sendMsg = new Message(Consts.Msg.RequestFeedContent);
                sendMsg.content = new RequestFeedContent(keyword);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                String sendString = gson.toJson(sendMsg);
                System.out.println("SendString:" + sendString);

                os.writeUTF(sendString);
                os.flush();

                is = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);

                byte[] data = (byte[]) ois.readObject();

                String receivedString = new String(data, "EUC-KR");

                System.out.println("Length: " + data.length);
                System.out.println(receivedString);

                String receivedMessage = receivedString.substring(0, receivedString.indexOf(",\"content\":")) + receivedString.substring(receivedString.lastIndexOf("}"));
                String receivedContent = receivedString.substring(receivedString.indexOf(",\"content\":") + 11, receivedString.lastIndexOf("}"));

                System.out.println(receivedMessage);
                System.out.println(receivedContent);

                Message received = gson.fromJson(receivedMessage, Message.class);

                if (received.contentType.equals(Consts.Msg.ResponseFeedContent)) {
                    ResponseFeedContent content = gson.fromJson(receivedContent, ResponseFeedContent.class);
                    if (content.feeds.isEmpty()) {
                        android.os.Message msg = mMainHandler.obtainMessage();
                        msg.what = -1;
                        System.out.println("EMPTY!");
                        mMainHandler.sendEmptyMessage(-1);
                    } else {
                        android.os.Message msg = mMainHandler.obtainMessage();

                        switch (keyword) {
                            case "자유":
                                msg.what = 0;
                                break;
                            case "게임":
                                msg.what = 1;
                                break;
                            case "트레이드":
                                msg.what = 2;
                                break;
                            case "행아웃":
                                msg.what = 3;
                                break;
                            case "스터디":
                                msg.what = 4;
                                break;


                        }
//                        msg.what = 0;
                        msg.obj = content;

                        mMainHandler.sendMessage(msg);
                    }
                }

                is.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);



        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

    class SenderThread extends Thread {
        String keyword;

        public SenderThread(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public void run() {
            super.run();
            try {
                socket = new Socket(InetAddress.getByName(Consts.server.SERVER_IP), PORT);
                os = new DataOutputStream(socket.getOutputStream());
                Message sendMsg;

                sendMsg = new Message(Consts.Msg.RequestFeedContent);
                sendMsg.content = new RequestFeedContent(keyword);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                String sendString = gson.toJson(sendMsg);
                System.out.println("SendString:" + sendString);

                os.writeUTF(sendString);
                os.flush();

                is = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);

                byte[] data = (byte[]) ois.readObject();

                String receivedString = new String(data, "EUC-KR");

                System.out.println("Length: " + data.length);
                System.out.println(receivedString);

                String receivedMessage = receivedString.substring(0, receivedString.indexOf(",\"content\":")) + receivedString.substring(receivedString.lastIndexOf("}"));
                String receivedContent = receivedString.substring(receivedString.indexOf(",\"content\":") + 11, receivedString.lastIndexOf("}"));

                System.out.println(receivedMessage);
                System.out.println(receivedContent);

                Message received = gson.fromJson(receivedMessage, Message.class);

                if (received.contentType.equals(Consts.Msg.ResponseFeedContent)) {
                    ResponseFeedContent content = gson.fromJson(receivedContent, ResponseFeedContent.class);
                    if (content.feeds.isEmpty()) {
                        android.os.Message msg = mMainHandler.obtainMessage();
                        msg.what = -1;
                        System.out.println("EMPTY!");
                        mMainHandler.sendEmptyMessage(-1);
                    } else {
                        android.os.Message msg = mMainHandler.obtainMessage();

                        switch (keyword) {
                            case "자유":
                                msg.what = 0;
                                break;
                            case "게임":
                                msg.what = 1;
                                break;
                            case "트레이드":
                                msg.what = 2;
                                break;
                            case "행아웃":
                                msg.what = 3;
                                break;
                            case "스터디":
                                msg.what = 4;
                                break;

                        }

                        msg.obj = content;

                        mMainHandler.sendMessage(msg);
                    }
                }

                is.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    class MassgeHandler extends Handler {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if (msg.what == -1) {
                Toast t = Toast.makeText(getApplicationContext(),
                        "no Feed" ,
                        Toast.LENGTH_SHORT);
                t.show();
                return;
            }

            ResponseFeedContent content = (ResponseFeedContent) msg.obj;

            List<Feed> t_feeds = new ArrayList<Feed>();

            for(Feed f : content.feeds){
                t_feeds.add(0, f);
            }

            mFeedListAdapter = new FeedListAdapter(MainActivity.this, R.layout.activity_main_feed_cell, t_feeds);

            switch (msg.what) {
                case 0:
                    lv_board.setAdapter(mFeedListAdapter);
                    synchronized (lv_board) {
                        lv_board.notify();
                    }
                    break;
                case 1:
                    lv_game.setAdapter(mFeedListAdapter);
                    synchronized (lv_game) {
                        lv_game.notify();
                    }
                    break;
                case 2:
                    lv_trade.setAdapter(mFeedListAdapter);
                    synchronized (lv_trade) {
                        lv_trade.notify();
                    }
                    break;
                case 3:
                    lv_hangout.setAdapter(mFeedListAdapter);
                    synchronized (lv_hangout) {
                        lv_hangout.notify();
                    }
                    break;
                case 4:
                    lv_study.setAdapter(mFeedListAdapter);
                    synchronized (lv_study) {
                        lv_study.notify();
                    }
                    break;

                default:
                    break;
            }
        }
    }

}
