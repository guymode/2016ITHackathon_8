package com.hackerthon.storyteller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackerthon.storyteller.R;
import com.hackerthon.storyteller.domain.Feed;

import java.util.List;


/**
 * Created by 대학원생 on 2016-09-26.
 */
public class FeedListAdapter extends ArrayAdapter<Feed> {
//    String backgroundCorlor;
//    LinearLayout lo_status;
    private String TAG = "RecommendListAdater";
    List<Feed> items;
    private final int layout;
    private LayoutInflater inflater = null;
    Context context;


    public FeedListAdapter(Context context, int layout, List<Feed> items){
        super(context, layout, items);
        this.context = context;
        this.items = items;
        this.layout = layout;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    class ViewHolder {
        TextView tv_title, tv_content, tv_date, tv_nick;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //View view = super.getView(position);
        //View view = convertView;
//        Log.d(TAG,"getview");
        ViewHolder holder;

        View row = convertView;
        if(row == null){
            row = inflater.inflate(layout, parent, false);

            holder = new ViewHolder();

            holder.tv_title = (TextView)row.findViewById(R.id.tv_feed_title);
            holder.tv_content = (TextView)row.findViewById(R.id.tv_feed_content);
            holder.tv_date = (TextView)row.findViewById(R.id.tv_feed_date);
            holder.tv_nick = (TextView)row.findViewById(R.id.tv_feed_nick);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder)row.getTag();
        }

        System.out.println(items.get(position).name);

        holder.tv_title.setText(items.get(position).title);
        holder.tv_content.setText(items.get(position).content);
        holder.tv_date.setText(items.get(position).time);
//        holder.tv_nick.setText(items.get(position).nick);


//        String str = items.get(position).toString();
//        //Log.d(TAG, str);
//        holder.tv_Name.setText(items.get(position).name);                                                   //장비 이름


        return row;
    }


}
