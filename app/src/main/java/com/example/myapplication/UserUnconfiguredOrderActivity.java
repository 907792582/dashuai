package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Adapter.OrderAdapter;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.Shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserUnconfiguredOrderActivity extends AppCompatActivity  {
    @BindView(R.id.titile)
    TextView mTvTitle;
    @BindView(R.id.listView_order)
    ListView mListView;
    public List<HashMap<String, String>> goodsList_order;
    private OrderAdapter adapter;
    private  List<Shop> paidList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_check_order);
        ButterKnife.bind(this);
        mTvTitle.setText("未配置订单");
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        goodsList_order=new ArrayList<>();
        initDate();
        initView();

    }
    private void initDate() {

        Msg message = (Msg) getIntent().getSerializableExtra("paidList");

        paidList = (List<Shop>) message.getExtend().get("paidList");

        goodsList_order = new ArrayList<>();
        for (Shop shop:paidList) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", shop.getShopid());
            map.put("name", shop.getBookname());
            map.put("number",shop.getBookid());
            map.put("inventory", "50");
            map.put("price", String.valueOf(shop.getBookprice()));
            map.put("count",shop.getBookintroduction());
            map.put("url","http://47.100.226.176:8080/shopapp/upload/"+shop.getShopimage());
            goodsList_order.add(map);
        }
    }

    private void initView() {
        adapter = new OrderAdapter(this, goodsList_order, R.layout.item_order);
        mListView.setAdapter(adapter);
        ImageView ivnoOrder=findViewById(R.id.iv_no_order);
        TextView tvnoOrder=findViewById(R.id.tv_no_order);
        if(goodsList_order.isEmpty())
            mListView.setVisibility(View.GONE);
        else {
            ivnoOrder.setVisibility(View.GONE);
            tvnoOrder.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                this.finish();
                break;

        }
    }

}



