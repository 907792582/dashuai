package com.example.myapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

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

public class SearchResultAdapter extends CommonAdapter<HashMap<String, String>> {
private ItemClickListener listener;

    public SearchResultAdapter(Context context, List<HashMap<String, String>> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, HashMap<String, String> map) {
        if(map.get("id").equals("1"))
            holder.setBackgroundColor(R.id.tv_add_to_cart, Color.parseColor("#b5b5b5"));
        else
            holder.setBackgroundColor(R.id.tv_add_to_cart, Color.parseColor("#E24146"));
        holder.setText(R.id.book_name,map.get("name"));
        holder.setText(R.id.book_number, "编号："+map.get("number"));
        holder.setText(R.id.book_inventory,"库存："+map.get("inventory"));
        holder.setText(R.id.book_price, "￥ " + (Double.valueOf(map.get("price")) * Integer.valueOf(map.get("count"))));
        holder.setOnClickListener(R.id.tv_add_to_cart, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.AddToCart(view,holder.getPosition());
            }
        }
        );
    }
    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public interface ItemClickListener {
      void AddToCart(View view, int position);
    }
}
