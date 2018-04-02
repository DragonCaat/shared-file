package com.example.wtuapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wtuapplication.R;

import java.util.ArrayList;

/**
 * Created by 梁 on 2017/9/21.
 */

/**
 * gridView的适配器
 * */
public class MyGridViewAdapter  extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> list;

    /**
     * 通过构造方法将数据引入
     * */
    public MyGridViewAdapter(Context context,ArrayList<String>arrayList){
        this.mContext = context;
        this.list = arrayList;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Holder holder = null;
        if (convertView == null){
            holder= new Holder();
            convertView = View.inflate(mContext, R.layout.show_computer_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_des_ip);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        String ipName = list.get(position);
        holder.textView.setText(ipName);
        return convertView;
    }

    class Holder{
        TextView textView;
    }
}
