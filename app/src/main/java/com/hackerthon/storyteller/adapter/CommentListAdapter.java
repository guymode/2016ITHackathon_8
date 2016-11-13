package com.hackerthon.storyteller.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.hackerthon.storyteller.message.RequestFeedContent;
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
 * Created by Hynk on 2016-11-13.
 */
public class CommentListAdapter extends ArrayAdapter<comment> {
    private String TAG = "CommentListAdapter";
    List<comment> items;
    private final int layout;
    private LayoutInflater inflater = null;
    Context context;


    public CommentListAdapter(Context context, int layout, List<comment> items) {
        super(context, layout, items);
        this.context = context;
        this.items = items;
        this.layout = layout;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    class ViewHolder {
        TextView tv_content, tv_nick;
    }
    ViewHolder holder;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
//        Log.d(TAG,"getview");
        final ViewHolder holder;

        View row = convertView;
        if (row == null) {
            row = inflater.inflate(layout, parent, false);

            holder = new ViewHolder();

            holder.tv_nick = (TextView) row.findViewById(R.id.tv_comment_nick);
            holder.tv_content = (TextView) row.findViewById(R.id.tv_comment_content);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.tv_nick.setText(items.get(position).nick);
        holder.tv_content.setText(items.get(position).content);

        return row;
    }



}
