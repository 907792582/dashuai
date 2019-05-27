package com.example.myapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.viewholder.CommonAdapter;
import com.example.myapplication.viewholder.ViewHolder;
import com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout;

import java.util.HashMap;
import java.util.List;

/**
 * File descripition:
 *
 * @author lp
 * @date 2018/9/28
 */

public class AdminInfoAdapter extends CommonAdapter<HashMap<String, String>> {


    public AdminInfoAdapter(Context context, List<HashMap<String, String>> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, HashMap<String, String> map) {
        holder.setText(R.id.tv_stu_info_admin,"单号："+map.get("stu_number")+"   书名："+map.get("stu_name"));
        if(map.get("order_status").equals("1")) {
            holder.setText(R.id.tv_order_status, "已提取");
            holder.setBackgroundColor(R.id.tv_order_status, Color.parseColor("#ffe4e4"));
        }
        else
        {
            holder.setText(R.id.tv_order_status, "未提取");
            holder.setBackgroundColor(R.id.tv_order_status, Color.parseColor("#c6c6c6"));
        }
        holder.setImageUrl(R.id.order_image_admin1,map.get("url"));

    }

}