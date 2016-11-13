package com.hackerthon.storyteller.adapter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hackerthon.storyteller.R;
import com.hackerthon.storyteller.domain.Feed;
import com.hackerthon.storyteller.domain.comment;
import com.hackerthon.storyteller.message.Message;
import com.hackerthon.storyteller.message.RequestCommentContent;
import com.hackerthon.storyteller.message.RequestWriteCommentContent;
import com.hackerthon.storyteller.message.ResponseCommentContent;
import com.hackerthon.storyteller.message.ResponseFeedContent;
import com.hackerthon.storyteller.pref.Consts;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;


/**
 * Created by 대학원생 on 2016-09-26.
 */
public class FeedListAdapter extends ArrayAdapter<Feed> {
    private String TAG = "RecommendListAdater";
    List<Feed> items;
    private final int layout;
    private LayoutInflater inflater = null;
    Context context;
    private MassgeHandler mMainHandler = null;
    private DataOutputStream os;
    private InputStream is;
    private static final int PORT = Consts.server.SERVER_PORT;
    private Socket socket;
    private CommentListAdapter mCommentListAdapter;
    private String mNick;


    public FeedListAdapter(Context context, int layout, List<Feed> items){
        super(context, layout, items);
        this.context = context;
        this.items = items;
        this.layout = layout;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    class ViewHolder {
        TextView tv_title, tv_content, tv_date, tv_nick, tv_comment;
        Button btn_comment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        Log.d(TAG,"getview");
        final ViewHolder holder;
        mMainHandler = new MassgeHandler();

        View row = convertView;
        if(row == null){
            row = inflater.inflate(layout, parent, false);

            holder = new ViewHolder();

            holder.tv_title = (TextView)row.findViewById(R.id.tv_feed_title);
            holder.tv_content = (TextView)row.findViewById(R.id.tv_feed_content);
            holder.tv_date = (TextView)row.findViewById(R.id.tv_feed_date);
            holder.tv_nick = (TextView)row.findViewById(R.id.tv_feed_nick);
            holder.tv_comment = (TextView)row.findViewById(R.id.tv_feed_comment);
            holder.btn_comment = (Button)row.findViewById(R.id.btn_feed_comment);

            row.setTag(holder);
        }
        else{
            holder = (ViewHolder)row.getTag();
        }

        holder.tv_title.setText(items.get(position).title);
        holder.tv_content.setText(items.get(position).content);
        holder.tv_date.setText(items.get(position).time);
        holder.tv_nick.setText(items.get(position).nick);
        holder.tv_comment.setText(items.get(position).cnt+"");


        holder.btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread commentThread = new Thread(new CommentThread(items.get(position).id));
                mNick = holder.tv_nick.getText().toString();
                commentThread.start();

            }
        });

        return row;
    }


    class CommentThread extends Thread {
        private int id;

        public CommentThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            super.run();
            try {
                socket = new Socket(InetAddress.getByName(Consts.server.SERVER_IP), PORT);
                os = new DataOutputStream(socket.getOutputStream());
                Message sendMsg;

                sendMsg = new Message(Consts.Msg.RequestCommentContent);
                sendMsg.content = new RequestCommentContent(id);
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

                System.out.println("TAG: Length: " + data.length);
                System.out.println(receivedString);

                String receivedMessage = receivedString.substring(0, receivedString.indexOf(",\"content\":")) + receivedString.substring(receivedString.lastIndexOf("}"));
                String receivedContent = receivedString.substring(receivedString.indexOf(",\"content\":") + 11, receivedString.lastIndexOf("}"));

                System.out.println(receivedMessage);
                System.out.println(receivedContent);

                Message received = gson.fromJson(receivedMessage, Message.class);

                if (received.contentType.equals(Consts.Msg.ResponseCommentContent)) {
                    ResponseCommentContent content = gson.fromJson(receivedContent, ResponseCommentContent.class);
                        android.os.Message msg = mMainHandler.obtainMessage();
                        msg.what = id;
                        msg.obj = content;

                        mMainHandler.sendMessage(msg);
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

    class WriteCommentThread extends Thread{
        int what;
        String nick, content;

        public WriteCommentThread(int what, String nick, String content){
        System.out.println("4####### ID: "+what);
            this.what = what;
            this.nick = nick;
            this.content = content;
        }

        @Override
        public void run() {
            super.run();
            try {
                socket = new Socket(InetAddress.getByName(Consts.server.SERVER_IP), PORT);
                os = new DataOutputStream(socket.getOutputStream());
                Message sendMsg;

                sendMsg = new Message(Consts.Msg.RequestWriteCommentContent);
                sendMsg.content = new RequestWriteCommentContent(what, nick, content);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                String sendString = gson.toJson(sendMsg);
                System.out.println("SendString:" + sendString);

                os.writeUTF(sendString);
                os.flush();

                is = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);

                System.out.println("최적화");
                is.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    class MassgeHandler extends Handler {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(final android.os.Message msg) {
            super.handleMessage(msg);


            ResponseCommentContent content = (ResponseCommentContent) msg.obj;


            mCommentListAdapter = new CommentListAdapter(context, R.layout.dialog_comment_cell, content.comments);

            //TODO 스레드에서 보여주기
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//                LayoutInflater inflater = .getLayoutInflater();
            final View view_enroll = inflater.inflate(R.layout.dialog_comment, null);

            AlertDialog.Builder inBuilder = new AlertDialog.Builder(context);

            final EditText et_content = (EditText) view_enroll.findViewById(R.id.et_comment_content);
            final Button  btn_send = (Button) view_enroll.findViewById(R.id.btn_comment_send);
            final ListView lv_list = (ListView) view_enroll.findViewById(R.id.lv_comment);

//            System.out.println("3####### ID: "+msg.what);
            final int temp = msg.what;

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    System.out.println("3.5####### ID: "+temp);
                   Thread writeCommentThread = new Thread(new WriteCommentThread(temp, Consts.pref.getNick, et_content.getText().toString()));
                    writeCommentThread.start();
                }
            });

            inBuilder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO 디비에 저장
                            dialog.dismiss();
                        }
                    });

            LayoutInflater factory = LayoutInflater.from(context);
            View myView = factory.inflate(R.layout.titlebar_dialog, null);
            TextView tv = (TextView) myView.findViewById(R.id.tv_dialog_title);
            tv.setText(mNick+"님의 스토리");
            myView.getDefaultSize(10,10);

            inBuilder.setCustomTitle(myView);


            lv_list.setAdapter(mCommentListAdapter);
            synchronized (lv_list) {
                lv_list.notify();
            }
            inBuilder.setView(view_enroll);
            inBuilder.show();
        }
    }


}
