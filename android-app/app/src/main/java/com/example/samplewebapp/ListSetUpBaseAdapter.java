package com.example.samplewebapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListSetUpBaseAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    List titles;
    List dates;
    List times;


    public ListSetUpBaseAdapter(Context ctx, List titles, List dates, List times){
        this.context = ctx;
        this.titles = titles;
        this.dates = dates;
        this.times = times;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public Object getItem(int position) {

        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.list_set_up, null);
        TextView titleView = (TextView) convertView.findViewById(R.id.eventListTitle);
        TextView dateView = (TextView) convertView.findViewById(R.id.eventListDate);
        TextView timeView = (TextView) convertView.findViewById(R.id.eventListTime);
        titleView.setText(titles.get(position).toString());
        dateView.setText(dates.get(position).toString());
        timeView.setText(times.get(position).toString());
        return convertView;
    }
}
