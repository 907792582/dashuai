package com.example.myapplication.Fragment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.OrderAdapter;
import com.example.myapplication.Admin_MainActivity;
import com.example.myapplication.CheckOrderActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.Adapter.OrderAdapter;
import com.example.myapplication.StatusBarUtil;
import com.example.myapplication.SearchActivity;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.Shop;
import com.example.myapplication.model.User;
import com.google.gson.Gson;
import com.necer.ndialog.NDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfigureOrderAdmin extends Fragment{

    @BindView(R.id.titleView)
    TextView mTvTitle;
    @BindView(R.id.listView_order_admin)
    ListView mListView;
    @BindView(R.id.tv_total_price)
    TextView mTvTotalPrice;
    @BindView(R.id.tv_number_unconfigured)
    TextView tv_number_unconfigured;
    @BindView(R.id.tv_stu_info)
    TextView tv_stu_info;

    private double totalPrice = 0.00;
    private int totalCount = 0;
    private List<HashMap<String, String>> goodsList;
    private List<HashMap<String, String>> goodsList_order;
    private OrderAdapter adapter;
    private Context mcontext;

    private List<User> userList;
    private List<Shop> paidList;
    RequestQueue mQueue;

    private int CURRENTUSER = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_order_form_admin, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mcontext=getActivity();
        goodsList=new ArrayList<>();
        onAttach(mcontext);
        ButterKnife.bind(this,view);
        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, null);
        TextView searchview=view.findViewById(R.id.search_text);
        searchview.setVisibility(View.GONE);
        mTvTitle.setText("订单配置");

        mQueue = Volley.newRequestQueue(mcontext);
        userList = new ArrayList<>();
        paidList = new ArrayList<>();

        getAllUsersWithPaidOrders(view);



    }

    private void getAllUsersWithPaidOrders(final View view) {

        userList.clear();

        String url = "http://193.112.98.224:8080/shopapp/shop/findusernot";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());

                // 操作成功
                if(message.getCode() == 100){

                    JSONArray temp = null;
                    try {
                        temp = jsonObject.getJSONObject("extend").getJSONArray("userlist");

                        for(int i = 0;i<temp.length();i++){
                            User item = new Gson().fromJson(temp.get(i).toString(), User.class);
                            userList.add(item);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 目前没有订单
                    if(userList.size() == 0){
                        CURRENTUSER = -1;
                        setPage(view);
                    }else {
                        CURRENTUSER = 0;
                        getPaidOrdersByUser(view);
                    }



                }else{
                    // 操作失败
                    Toast.makeText(mcontext, message.getExtend().get("va_msg").toString() , Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(mcontext, "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void getPaidOrdersByUser(final View view){

        paidList.clear();

        // 配置完成最后一个用户的订单，刷新用户列表
        if(CURRENTUSER >= userList.size()){
            getAllUsersWithPaidOrders(view);
            return;
        }


        User user = userList.get(CURRENTUSER);

        String url = "http://193.112.98.224:8080/shopapp/bookbuy/getinform/"+user.getUserid();
        Log.e("##订单url:",url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());

                // 操作成功
                if(message.getCode() == 100){
                    try {

                        JSONArray paidShop = jsonObject.getJSONObject("extend").getJSONArray("未配置列表");

                        for(int i = 0;i<paidShop.length();i++){
                            Shop item = new Gson().fromJson(paidShop.get(i).toString(), Shop.class);
                            paidList.add(item);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 显示界面
                    setPage(view);


                }else{
                    // 操作失败
                    Toast.makeText(mcontext,"订单获取失败，请联系管理员" , Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(mcontext, "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);
    }


    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        goodsList=((Admin_MainActivity)activity).getGoodsList_order();
        if(goodsList!=null){
            for(int i=0;i<goodsList.size();i++)
            {
                goodsList.get(i).put("id","0");
            }
        }

    }

    private void setPage(View view){

        //模拟数据
        initDate();

        if(goodsList.isEmpty())
        {
            LinearLayout orderAdminLayout=view.findViewById(R.id.orderAdminLayout);
            orderAdminLayout.setVisibility(View.GONE);
            TextView cartEmpty=view.findViewById(R.id.tv_cart_empty);
            cartEmpty.setText("当前没有新订单哦~");
        }
        else
        {
            TextView cartEmpty=view.findViewById(R.id.tv_cart_empty);
            cartEmpty.setVisibility(View.GONE);
            tv_number_unconfigured.setText("当前还有"+(userList.size()-CURRENTUSER)+"份订单未配置");
            tv_stu_info.setText("from: "+userList.get(CURRENTUSER).getUsername());
        }


        initView();
        priceContro();

    }


    private void initDate() {
        goodsList = new ArrayList<>();
        goodsList.clear();
        for (int i = 0; i < paidList.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", paidList.get(i).getShopid());
            map.put("name", paidList.get(i).getBookname());
            map.put("number", paidList.get(i).getBookid());
            // map.put("inventory", paidList.get(i).get);
            map.put("price", String.valueOf(paidList.get(i).getBookprice()));
            map.put("count", paidList.get(i).getBookintroduction());
            goodsList.add(map);
        }
    }

    private void initView() {
        adapter = new OrderAdapter(mcontext, goodsList, R.layout.item_order);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.tv_check_configure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_check_configure:
                NDialog builder  = new NDialog(mcontext);
                builder.setTitle("确认配置" ) ;
                builder.setTitleColor(Color.parseColor("#c1272d"));
                builder.setMessage("是否确认订单已配置？" ) ;
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
                                setOrderToUntook(paidList);
                                CURRENTUSER++;
                                getPaidOrdersByUser(getView());
                                break;
                        }
                    }
                }).create(NDialog.CONFIRM).show();

                break;
        }
    }

    private void setOrderToUntook(List<Shop> shops) {

        for(Shop shop:shops){
            String url = "http://193.112.98.224:8080/shopapp/bookbuy/changestatus1/"+shop.getShopid();
            Log.e("##配置成功url:",url);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

                public void onResponse(org.json.JSONObject jsonObject) {
                    Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                    Log.e("##", jsonObject.toString());

                    // 操作成功
                    if(message.getCode() == 100){

                        Toast.makeText(mcontext,"配置成功" , Toast.LENGTH_SHORT).show();

                    }else{
                        // 操作失败
                        Toast.makeText(mcontext,"配置操作失败，请联系管理员" , Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(mcontext, "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
                }
            });
            mQueue.add(jsonObjectRequest);
        }

    }


    //控制价格展示
    private void priceContro() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < goodsList.size(); i++) {
                totalCount = totalCount + Integer.valueOf(goodsList.get(i).get("count"));
                double goodsPrice = Integer.valueOf(goodsList.get(i).get("count")) * Double.valueOf(goodsList.get(i).get("price"));
                totalPrice = totalPrice + goodsPrice;
        }
        mTvTotalPrice.setText("￥ " + totalPrice);
    }

}
