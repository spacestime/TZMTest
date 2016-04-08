package com.example.MyBaseAdapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Space on 2016/3/22.
 */
public  abstract class MyBaseAdapter<T> extends BaseAdapter {

    public List<T> lists;
    public Context context;

    public MyBaseAdapter(List<T> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    public MyBaseAdapter() {
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
