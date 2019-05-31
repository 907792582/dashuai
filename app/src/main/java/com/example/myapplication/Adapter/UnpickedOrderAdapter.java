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

public class UnpickedOrderAdapter extends CommonAdapter<HashMap<String, String>> {
    private ItemClickListener listener;

    public UnpickedOrderAdapter(Context context, List<HashMap<String, String>> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, HashMap<String, String> map) {

        holder.setText(R.id.tv_order_number,"订单号："+map.get("id"));
        holder.setText(R.id.tv_book_count, "共"+map.get("number")+"本书");
        holder.setText(R.id.tv_book_price,"合计：￥"+map.get("price"));
        holder.setImageUrl(R.id.order_image_user1,map.get("url"));
        holder.setOnClickListener(R.id.tv_confirm_picked, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.ConfirmPicked(view,holder.getPosition());
                    }
                }
        );
    }
    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    public interface ItemClickListener {
        void ConfirmPicked(View view, int position);
    }
}
