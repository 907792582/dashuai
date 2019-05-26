package com.example.myapplication;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Adapter.AdminInfoAdapter;
import com.example.myapplication.Adapter.OrderAdapter;
import com.example.myapplication.Adapter.UnpickedOrderAdapter;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.Shop;
import com.necer.ndialog.NDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserUnpickedOrderActivity extends AppCompatActivity implements UnpickedOrderAdapter.ItemClickListener {
    @BindView(R.id.titile)
    TextView mTvTitle;
    @BindView(R.id.listView_order)
    ListView mListView;
    public List<HashMap<String, String>> goodsList_order;
    private UnpickedOrderAdapter adapter;
    private List<Shop> untookList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_check_order);
        ButterKnife.bind(this);
        mTvTitle.setText("待提取订单");
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        goodsList_order=new ArrayList<>();
        initDate();
        initView();

    }
    private void initDate() {
        Msg message = (Msg) getIntent().getSerializableExtra("untookList");

        untookList = (List<Shop>) message.getExtend().get("untookList");

        goodsList_order = new ArrayList<>();
        for (Shop shop:untookList) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", shop.getShopid());
            map.put("name", shop.getBookname());
            map.put("number",shop.getBookid());
            map.put("inventory", "50");
            map.put("price", String.valueOf(shop.getBookprice()));
            map.put("count",shop.getBookintroduction());
            goodsList_order.add(map);
        }
    }

    private void initView() {
            adapter = new UnpickedOrderAdapter(this, goodsList_order, R.layout.item_unpicked_order_user);
        mListView.setAdapter(adapter);
        ImageView ivnoOrder=findViewById(R.id.iv_no_order);
        TextView tvnoOrder=findViewById(R.id.tv_no_order);
        if(goodsList_order.isEmpty())
            mListView.setVisibility(View.GONE);
        else {
            ivnoOrder.setVisibility(View.GONE);
            tvnoOrder.setVisibility(View.GONE);
        }
        adapter.setOnItemClickListener(this);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void ConfirmPicked(View view, int position) {
        NDialog builder  = new NDialog(this);
        builder.setTitle("确认提取" ) ;
        builder.setTitleColor(Color.parseColor("#c1272d"));
        builder.setMessage("是否确认订单已提取？" ) ;
        builder.setPositiveTextColor(Color.parseColor("#c1272d"));
        builder.setNegativeTextColor(Color.parseColor("#999999"));
        builder.setCancleable(true);
        builder.setOnConfirmListener(new NDialog.OnConfirmListener() {
            @Override
            public void onClick(int which) {
                //0代表否，1代表是
                switch (which){
                    case 0:
                        break;
                    case 1:
                        break;
                }
            }
        }).create(NDialog.CONFIRM).show();
    }
}



