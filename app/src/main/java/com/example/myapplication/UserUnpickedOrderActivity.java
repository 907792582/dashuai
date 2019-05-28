package com.example.myapplication;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.AdminInfoAdapter;
import com.example.myapplication.Adapter.OrderAdapter;
import com.example.myapplication.Adapter.UnpickedOrderAdapter;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.Shop;
import com.google.gson.Gson;
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

    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_check_order);
        ButterKnife.bind(this);
        mTvTitle.setText("待提取订单");
        mQueue = Volley.newRequestQueue(UserUnpickedOrderActivity.this);
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
            map.put("url",shop.getShopimage());
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
    public void ConfirmPicked(final View view, final int position) {
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
                        setOrderToToken(untookList.get(position));
                        view.setBackgroundColor(Color.parseColor("#999999"));
                        break;
                }
            }
        }).create(NDialog.CONFIRM).show();
    }

    private void setOrderToToken(Shop shop) {


            String url = "http://47.100.226.176:8080/shopapp/bookbuy/changestatus2/"+shop.getShopid();
            Log.e("##配置成功url:",url);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

                public void onResponse(org.json.JSONObject jsonObject) {
                    Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                    Log.e("##", jsonObject.toString());

                    // 操作成功
                    if(message.getCode() == 100){

                        Toast.makeText(getApplicationContext(),"领取成功" , Toast.LENGTH_SHORT).show();

                    }else{
                        // 操作失败
                        Toast.makeText(getApplicationContext(),"配置操作失败，请联系管理员" , Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
                }
            });
            mQueue.add(jsonObjectRequest);


    }

}



