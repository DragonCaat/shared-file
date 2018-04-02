package com.example.wtuapplication.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wtuapplication.R;

import java.util.ArrayList;

/**
 * Created by 贾焰华 on 2017/11/19.
 */

public class GridViewOpenFileAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> list;

    /**
     * 通过构造方法将数据引入
     */
    public GridViewOpenFileAdapter(Context context, ArrayList<String> arrayList) {
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
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(mContext, R.layout.open_file_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.tv_des_file);
           holder.imageView = (ImageView) convertView.findViewById(R.id.dir);//这里你给布局的imageview设置一个id
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        String fileName = list.get(position);

        if (fileName.contains("."))//有后缀名的
        {

            if (fileName.contains(".jpeg")||fileName.contains(".png")||fileName.contains(".bmp")||fileName.contains(".jpg"))
            {
                holder.imageView.setImageResource(R.mipmap.img);//加入对应的图片id//继续判断
            }

            else if(fileName.contains(".mp4")||fileName.contains(".3gp")||fileName.contains(".rmvb")||fileName.contains(".flash"))//加入对应的图片id)
            {
                holder.imageView.setImageResource(R.mipmap.tv);
            }
            else if(fileName.contains(".mp3")||fileName.contains(".wma")||fileName.contains(".wav")||fileName.contains(".ra"))//加入对应的图片id)
            {
                holder.imageView.setImageResource(R.mipmap.music);
            }
            else if (fileName.contains(".txt"))
            {
                holder.imageView.setImageResource(R.mipmap._txt);
            }
            else
            {
                holder.imageView.setImageResource(R.mipmap._unkonw);
            }
          //加入对应的图片id
        }
        else {//后缀名
            holder.imageView.setImageResource(R.mipmap.file_);//设置对应的图片
        }
        holder.textView.setText(fileName);
        return convertView;
        }
    class Holder{
        TextView textView;
        ImageView imageView;
    }
}