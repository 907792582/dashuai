package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.OrderAdapter;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.Shop;
import com.example.myapplication.tool.TokenHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckOrderActivity extends AppCompatActivity  {
    @BindView(R.id.titile)
    TextView mTvTitle;
    @BindView(R.id.listView_order)
    ListView mListView;
    //@BindView(R.id.stu_num)
    //TextView stu_num;
    //@BindView(R.id.stu_name)
    //TextView stu_name;
    @BindView(R.id.tv_total_price)
    TextView mTvTotalPrice;
    @BindView(R.id.tv_go_to_pay_wechat)
    ImageButton mTvGoToPaywc;
    @BindView(R.id.tv_go_to_pay_ali)
    ImageButton mTvGoToPayali;
    private double totalPrice = 0.00;
    private int totalCount = 0;
    public List<HashMap<String, String>> goodsList_order;
    private OrderAdapter adapter;

    // volley
    RequestQueue mQueue;
    // token
    TokenHelper tokenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_order);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        goodsList_order=new ArrayList<>();
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        goodsList_order=(List<HashMap<String, String>>) bundle.getSerializable("goodsList_order");
        // stu_num.setText("学号 ：" + "161310611");
        // stu_name.setText("姓名 ：" + "DHU");
        initView();
        priceContro();
    }


    private void initView() {
        adapter = new OrderAdapter(this, goodsList_order, R.layout.item_order);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mQueue = Volley.newRequestQueue(CheckOrderActivity.this);
        tokenHelper = new TokenHelper();
    }
    private void priceContro() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < goodsList_order.size(); i++) {
            double goodsPrice = Integer.valueOf(goodsList_order.get(i).get("count")) * Double.valueOf(goodsList_order.get(i).get("price"));
            totalPrice = totalPrice + goodsPrice;
        }
        mTvTotalPrice.setText("￥ " + totalPrice);
}
    @OnClick({R.id.tv_go_to_pay_wechat, R.id.tv_go_to_pay_ali, R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_go_to_pay_wechat:
                for(int i = 0;i<goodsList_order.size();i++){
                    Shop shop = new Shop();
                    shop.setBookname(goodsList_order.get(i).get("name"));
                    shop.setShopid(goodsList_order.get(i).get("id"));
                    sendToServer(shop);
                }
                break;
            case R.id.tv_go_to_pay_ali:
                for(int i = 0;i<goodsList_order.size();i++){
                    Shop shop = new Shop();
                    shop.setBookname(goodsList_order.get(i).get("name"));
                    shop.setShopid(goodsList_order.get(i).get("id"));
                    sendToServer(shop);
                }
                break;
            case R.id.tv_back:
               finish();
                break;
        }
    }

    private void sendToServer(final Shop shop){
        String url = "http://47.100.226.176:8080/shopapp/bookbuy/buybook/"+shop.getShopid();
        Log.e("##买书url:",url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());

                // 操作成功
                if(message.getCode() == 100){


                    Toast.makeText(getApplicationContext(),shop.getBookname()+"购买成功" , Toast.LENGTH_SHORT).show();

                }else{
                    // 操作失败
                    Toast.makeText(getApplicationContext(),shop.getBookname()+"购买失败，请联系管理员" , Toast.LENGTH_SHORT).show();
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



