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

public class CartAdapter extends CommonAdapter<HashMap<String, String>> {
    private ItemClickListener listener;

    public CartAdapter(Context context, List<HashMap<String, String>> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, HashMap<String, String> map) {
        if (map.get("id").equals("0")) {
            holder.setChecked(R.id.check_box, false);
        } else {
            holder.setChecked(R.id.check_box, true);
        }
        holder.setText(R.id.book_name, map.get("name"));
        holder.setText(R.id.tv_num, map.get("count"));
        holder.setText(R.id.book_number,"编号："+map.get("number"));
        holder.setText(R.id.book_inventory,"库存："+map.get("inventory"));
        holder.setText(R.id.book_price, "￥ " + (Double.valueOf(map.get("price")) * Integer.valueOf(map.get("count"))));

        final EasySwipeMenuLayout easySwipeMenuLayout = holder.getView(R.id.action_bar);
        holder.setOnClickListener(R.id.check_box, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemClickListener(v, holder.getPosition());
            }
        });
        //删除
        holder.setOnClickListener(R.id.right, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //侧滑初始化
                easySwipeMenuLayout.resetStatus();
                listener.ItemDeleteClickListener(v, holder.getPosition());
            }
        });
        //减
        holder.setOnClickListener(R.id.tv_reduce, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemReduceClickListener(v, holder.getPosition());
            }
        });
        //加
        holder.setOnClickListener(R.id.tv_add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.ItemAddClickListener(v, holder.getPosition());
            }
        });
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public interface ItemClickListener {
        void ItemClickListener(View view, int position);

        void ItemDeleteClickListener(View view, int position);

        void ItemAddClickListener(View view, int position);

        void ItemReduceClickListener(View view, int position);
    }
}
