package com.example.myapplication.Adapter;

import android.content.Context;
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

public class OrderAdapter extends CommonAdapter<HashMap<String, String>> {


    public OrderAdapter(Context context, List<HashMap<String, String>> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, HashMap<String, String> map) {
        holder.setText(R.id.book_name,map.get("name"));
        holder.setText(R.id.tv_num,"x"+map.get("count"));
        holder.setText(R.id.book_number, "编号："+map.get("number"));
        holder.setText(R.id.book_inventory,"库存："+map.get("inventory"));
        holder.setText(R.id.book_price, "￥ " + (Double.valueOf(map.get("price")) * Integer.valueOf(map.get("count"))));
    }

}