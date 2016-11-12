package com.hackerthon.storyteller.ui;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;

import com.hackerthon.storyteller.R;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends TabActivity {

    private String TAG = "MainActivity";
    private SharedPreferences mSharedPreferences;

    private DataOutputStream os;
    private InputStream is;
    private static final int PORT  = 8889;//= Consts.server.SERVER_PORT;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logo);

        _init();
    }

    private void _init(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        TabHost tabHost = getTabHost();

        ImageView allView = new ImageView(this);
        allView.setImageResource(R.drawable.test_img);
        ImageView gameView = new ImageView(this);
        gameView.setImageResource(R.drawable.test_img);
        ImageView boardView = new ImageView(this);
        boardView.setImageResource(R.drawable.test_img);
        ImageView studyView = new ImageView(this);
        studyView.setImageResource(R.drawable.test_img);
        ImageView tradeView= new ImageView(this);
        tradeView.setImageResource(R.drawable.test_img);
        ImageView hangoutView = new ImageView(this);
        hangoutView.setImageResource(R.drawable.test_img);


        tabHost = getTabHost();                                                      // TabHost

        tabHost.addTab(tabHost.newTabSpec("모두").setIndicator("모두").setContent(R.id.lv_recom_all));             // Tab 생성
        tabHost.addTab(tabHost.newTabSpec("자유").setIndicator("자유").setContent(R.id.lv_recom_board));             // Tab 생성
        tabHost.addTab(tabHost.newTabSpec("게임").setIndicator("게임").setContent(R.id.lv_recom_game));
        tabHost.addTab(tabHost.newTabSpec("트레이드").setIndicator("트레이드").setContent(R.id.lv_recom_trade));
        tabHost.addTab(tabHost.newTabSpec("행아웃").setIndicator("행아웃").setContent(R.id.lv_recom_hangout));
        tabHost.addTab(tabHost.newTabSpec("스터디").setIndicator("스터디").setContent(R.id.lv_recom_study));

        tabHost.setCurrentTab(0);
        Thread sendThread = new Thread(new SenderThread("한식"));
        sendThread.start();
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
                //is = new DataInputStream(socket.getInputStream());
                os = new DataOutputStream(socket.getOutputStream());
                Message sendMsg = new Message();


//                RequestRecommendContent requestRecommendContent
                sendMsg = new Message(Consts.Msg.RequestRecommendContent);
                sendMsg.content  = new RequestRecommendContent(keyword,
                        mSharedPreferences.getInt(Consts.pref.CARBO_REMAIN, 0),
                        mSharedPreferences.getInt(Consts.pref.PROTEIN_REMAIN, 0),
                        mSharedPreferences.getInt(Consts.pref.FAT_REMAIN, 0),
                        mSharedPreferences.getString(Consts.pref.AVOIDFOOD, ""));;
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                String sendString = gson.toJson(sendMsg);
                System.out.println("SendString:" + sendString);

                os.writeUTF(sendString);
                os.flush();

//                String received = is.readUTF();

                is = socket.getInputStream();
                //등록한 InputStream을 ObjectInputStream방식으로 사용합니다.
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

                if (received.contentType.equals(Consts.Msg.ResponseRecommendContent)) {
                    ResponseRecommendContent content = gson.fromJson(receivedContent, ResponseRecommendContent.class);

                    for(Food food: content.foods){
                        System.out.println(food.toString());
                    }

//                    System.out.println("ISEMPTY?" + content.foods.isEmpty());
                    if(content.foods.isEmpty()){
                        android.os.Message msg = mMainHandler.obtainMessage();
                        msg.what = -1;
                        mMainHandler.sendMessage(msg);
                    }
                    else {
                        android.os.Message msg = mMainHandler.obtainMessage();

                        switch (keyword){
                            case "한식":
                                msg.what = 0;
                                break;
                            case "중식":
                                msg.what = 1;
                                break;
                            case "양식":
                                msg.what = 2;
                                break;
                            case "일식":
                                msg.what = 3;
                                break;
                            case "편의점":
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


}
