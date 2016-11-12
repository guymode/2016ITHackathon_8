package com.hackerthon.storyteller.ui;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hackerthon.storyteller.R;
import com.hackerthon.storyteller.adapter.FeedListAdapter;
import com.hackerthon.storyteller.message.*;
import com.hackerthon.storyteller.pref.Consts;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends TabActivity {

    private String TAG = "MainActivity";
    private SharedPreferences mSharedPreferences;
    private MassgeHandler mMainHandler = null;

    private ListView lv_board, lv_game, lv_trade, lv_hangout, lv_study;

    FeedListAdapter mFeedListAdapter;

    private DataOutputStream os;
    private InputStream is;
    private static final int PORT  = Consts.server.SERVER_PORT;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logo);


        mMainHandler = new MassgeHandler();
        lv_board = (ListView) findViewById(R.id.lv_recom_board);
        lv_game = (ListView) findViewById(R.id.lv_recom_game);
        lv_trade = (ListView) findViewById(R.id.lv_recom_trade);
        lv_hangout = (ListView) findViewById(R.id.lv_recom_hangout);
        lv_study  = (ListView) findViewById(R.id.lv_recom_study);

        _init();
    }

    private void _init(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        TabHost tabHost = getTabHost();
//
//        ImageView gameView = new ImageView(this);
//        gameView.setImageResource(R.drawable.test_img);
//        ImageView boardView = new ImageView(this);
//        boardView.setImageResource(R.drawable.test_img);
//        ImageView studyView = new ImageView(this);
//        studyView.setImageResource(R.drawable.test_img);
//        ImageView tradeView= new ImageView(this);
//        tradeView.setImageResource(R.drawable.test_img);
//        ImageView hangoutView = new ImageView(this);
//        hangoutView.setImageResource(R.drawable.test_img);
        tabHost = getTabHost();                                                      // TabHost

        tabHost.addTab(tabHost.newTabSpec("자유").setIndicator("자유").setContent(R.id.lv_recom_board));             // Tab 생성
        tabHost.addTab(tabHost.newTabSpec("게임").setIndicator("게임").setContent(R.id.lv_recom_game));
        tabHost.addTab(tabHost.newTabSpec("트레이드").setIndicator("트레이드").setContent(R.id.lv_recom_trade));
        tabHost.addTab(tabHost.newTabSpec("행아웃").setIndicator("행아웃").setContent(R.id.lv_recom_hangout));
        tabHost.addTab(tabHost.newTabSpec("스터디").setIndicator("스터디").setContent(R.id.lv_recom_study));

        tabHost.setCurrentTab(4);
        Thread sendThread = new Thread(new SenderThread("스터디"));
        sendThread.start();


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                Log.d(TAG,"TAB ID: "+tabId);

                Thread sendThread = new Thread(new SenderThread(tabId));
                sendThread.start();
            }
        });
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
                    System.out.println("TEST!?!");
                    ResponseFeedContent content = gson.fromJson(receivedContent, ResponseFeedContent.class);
                    if(content.feeds.isEmpty()){
                        android.os.Message msg = mMainHandler.obtainMessage();
                        msg.what = -1;
                        System.out.println("EMPTY!");
                        mMainHandler.sendEmptyMessage(-1);
                    }
                    else {
                        android.os.Message msg = mMainHandler.obtainMessage();

                        switch (keyword){
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
        }

    }
    class MassgeHandler extends Handler {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if(msg.what == -1){
                Toast t = Toast.makeText(getApplicationContext(),
                        "추천 할 음식이 없습니다.",
                        Toast.LENGTH_SHORT);
                t.show();
                return;
            }

            ResponseFeedContent content = (ResponseFeedContent) msg.obj;


            mFeedListAdapter = new FeedListAdapter(MainActivity.this, R.layout.activity_main_feed_cell, content.feeds);

            System.out.println("+++"+content.feeds.toString());

            switch (msg.what) {
                case 0:
//                    mRecommendListAdapter.notifyDataSetChanged();
                    lv_board.setAdapter(mFeedListAdapter);
                    synchronized (lv_board) {
                        lv_board.notify();
                    }
                    break;
                case 1:
//                    mRecommendListAdapter.notifyDataSetChanged();
                    lv_game.setAdapter(mFeedListAdapter);
                    synchronized (lv_game) {
                        lv_game.notify();
                    }
                    break;
                case 2:
//                    mRecommendListAdapter.notifyDataSetChanged();
                    lv_trade.setAdapter(mFeedListAdapter);
                    synchronized (lv_trade) {
                        lv_trade.notify();
                    }
                    break;
                case 3:
//                    mRecommendListAdapter.notifyDataSetChanged();
                    lv_hangout.setAdapter(mFeedListAdapter);
                    synchronized (lv_hangout) {
                        lv_hangout.notify();
                    }
                    break;
                case 4:
//                    mRecommendListAdapter.notifyDataSetChanged();
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
