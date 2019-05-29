package com.example.myapplication.Fragment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.CheckOrderActivity;
import com.example.myapplication.LoginActivity;
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
import com.example.myapplication.Adapter.CartAdapter;
import com.example.myapplication.StatusBarUtil;
import com.example.myapplication.SearchActivity;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.Shop;
import com.example.myapplication.tool.TokenHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShoppingCart_Fragment extends Fragment implements CartAdapter.ItemClickListener{

    // @BindView(R.id.titleView)
    TextView mTvTitle;
    // @BindView(R.id.listView)
    ListView mListView;
   // @BindView(R.id.all_chekbox)
    CheckBox mAllChekbox;
   //  @BindView(R.id.tv_total_price)
    TextView mTvTotalPrice;
   //  @BindView(R.id.tv_go_to_pay)
    TextView mTvGoToPay;
    private double totalPrice = 0.00;
    private int totalCount = 0;
    private List<HashMap<String, String>> goodsList;
    private List<HashMap<String, String>> goodsList_order;
    private CartAdapter adapter;
    private Context mcontext;

    // fragment的view,为了数据返回后设置UI方便
    View mView;
    // volley
    RequestQueue mQueue;
    // token
    TokenHelper tokenHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.shopping_cart, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        mcontext=getActivity();

        goodsList=new ArrayList<>();

        mQueue = Volley.newRequestQueue(mcontext);
        tokenHelper = new TokenHelper();
        mView = view;
        ButterKnife.bind(this,mView);
        // 得到购物车内容后设置UI
        getCartContent();


        //mQueue = Volley.newRequestQueue(mcontext);
        //tokenHelper = new TokenHelper();

        // 得到购物车内容后设置UI
        //getCartContent();


    }

    private void getCartContent() {
        String url = "http://47.100.226.176:8080/shopapp/bookbuy/getinform/"+tokenHelper.getToken();
        Log.e("##购物车信息获取url:",url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());

                // 操作成功
                if(message.getCode() == 100){
                    try {
                        JSONArray shops = jsonObject.getJSONObject("extend").getJSONArray("购物车列表");

                        for(int i = 0;i<shops.length();i++){
                            Shop item = new Gson().fromJson(shops.get(i).toString(), Shop.class);

                            Log.e("##购物车条目：", shops.get(i).toString());
                            HashMap<String, String> map = new HashMap<>();
                            map.put("selected","0");
                            map.put("name", item.getBookname());
                            map.put("number", item.getBookid());
                            map.put("inventory", "50");
                            map.put("price", String.valueOf(item.getBookprice()));
                            // map.put("count",item.getBookintroduction());// 书籍简介中存放数量
                            map.put("count","1");
                            map.put("id",item.getShopid());
                            map.put("url","http://47.100.226.176:8080/shopapp/BookImage/"+item.getShopimage());
                            goodsList.add(map);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 显示界面
                   setPage();


                }else{
                    // 操作失败
                    Toast.makeText(mcontext,"购物车列表获取失败，请联系管理员" , Toast.LENGTH_SHORT).show();
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

    private void setPage(){

        mListView = mView.findViewById(R.id.listView);
        mTvTitle = mView.findViewById(R.id.titleView);
        mAllChekbox = mView.findViewById(R.id.all_chekbox);
        mTvTotalPrice = mView.findViewById(R.id.tv_total_price);
        mTvGoToPay = mView.findViewById(R.id.tv_go_to_pay);

        if(goodsList.isEmpty())
        {
            LinearLayout cartLayout=mView.findViewById(R.id.cart_layout);
            LinearLayout cartbottomBar=mView.findViewById(R.id.cartbottomBar);
            cartbottomBar.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            TextView cartEmpty=mView.findViewById(R.id.tv_cart_empty);
            cartEmpty.setText("购物车是空的诶~");
        }
        else
        {
            TextView cartEmpty=mView.findViewById(R.id.tv_cart_empty);
            cartEmpty.setVisibility(mView.GONE);
        }
        mTvTitle.setText("购物车");
        initView();

        // onAttach(mcontext);


        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, null);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        //goodsList=((MainActivity)activity).getGoodsList_order();

        /*if(goodsList!=null){
            for(int i=0;i<goodsList.size();i++)
            {
                goodsList.get(i).put("selected","0");
            }
        }*/

    }

    /*private void initDate() {
        goodsList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", "0");
            map.put("name", "购物车里的第" + (i + 1) + "件商品");
            map.put("number", "666-"+i%3+i%4+i%5);
            map.put("inventory", (new Random().nextInt(10))+"");
            map.put("price", (new Random().nextInt(100) % (100 - 29 + 29) + 29) + "");
            map.put("count", (new Random().nextInt(10) % (10 - 1 + 1) + 1) + "");
            goodsList.add(map);
        }
    }*/

    private void initView() {
        adapter = new CartAdapter(mcontext, goodsList, R.layout.item_cehua);
        adapter.setOnItemClickListener(this);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    @OnClick({R.id.all_chekbox,R.id.search_text,R.id.tv_go_to_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.all_chekbox:
                AllTheSelected(true);
                break;

            case R.id.search_text:
                Intent intent_search_text=new Intent();
                intent_search_text.setClass(mcontext, SearchActivity.class);
                startActivity(intent_search_text);
                break;
            case R.id.tv_go_to_pay:
                if (totalCount <= 0) {
                    Toast.makeText(mcontext, "请选择要付款的商品~", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Bundle bundle=new Bundle();
                    Intent intent=new Intent(mcontext, CheckOrderActivity.class);
                    String name="name";
                   goodsList_order=new ArrayList<>();
                    for (int i = 0; i < goodsList.size(); i++)
                    {
                        if (goodsList.get(i).get("selected").equals("1"))
                        {
                            goodsList_order.add(goodsList.get(i));
                        }
                    }
                    intent.setClass(mcontext, CheckOrderActivity.class);
                    bundle.putSerializable("goodsList_order",(Serializable)goodsList_order);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void ItemClickListener(View view, int position) {
        HashMap<String, String> hashMap = goodsList.get(position);
        if (((CheckBox) view).isChecked()) {
            hashMap.put("selected", "1");
        } else {
            hashMap.put("selected", "0");
        }
        goodsList.set(position, hashMap);
        AllTheSelected(false);
    }

    //删除
    @Override
    public void ItemDeleteClickListener(View view, final int position) {

        // 向服务器发送删除指令

        Log.e("##购物车信息删除:","position:"+position);
        String url = "http://47.100.226.176:8080/shopapp/shop/delete/"+goodsList.get(position).get("id");
        Log.e("##购物车信息删除url:",url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Toast.makeText(mcontext, "删除成功" , Toast.LENGTH_SHORT).show();

                goodsList.remove(position);
                AllTheSelected(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(mcontext, "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);

    }

    //增加
    @Override
    public void ItemAddClickListener(View view, int position) {
        //当低于目标值是  可以做删除操作
        HashMap<String, String> hashMap = goodsList.get(position);
        int currentCount = Integer.valueOf(hashMap.get("count"));
        int currentInventory=Integer.valueOf(hashMap.get("inventory"));
        if (currentCount + 1 > currentInventory) {
            Toast.makeText(mcontext, "亲，已达库存上限~", Toast.LENGTH_SHORT).show();
        } else {
            hashMap.put("count", String.valueOf(currentCount + 1));
            goodsList.set(position, hashMap);
        }
        AllTheSelected(false);
    }

    //减
    @Override
    public void ItemReduceClickListener(View view, int position) {
        HashMap<String, String> hashMap = goodsList.get(position);
        int currentCount = Integer.valueOf(hashMap.get("count"));
        if (currentCount - 1 < 1) {
            Toast.makeText(mcontext, "受不了了，宝贝不能再减少了哦~", Toast.LENGTH_SHORT).show();
        } else {
            hashMap.put("count", String.valueOf(currentCount - 1));
            goodsList.set(position, hashMap);
        }
        AllTheSelected(false);
    }

    //控制价格展示
    private void priceContro() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < goodsList.size(); i++) {
            if (goodsList.get(i).get("selected").equals("1")) {
                totalCount = totalCount + Integer.valueOf(goodsList.get(i).get("count"));
                double goodsPrice = Integer.valueOf(goodsList.get(i).get("count")) * Double.valueOf(goodsList.get(i).get("price"));
                totalPrice = totalPrice + goodsPrice;
            }
        }
        mTvTotalPrice.setText("￥ " + totalPrice);
        mTvGoToPay.setText("付款(" + totalCount + ")");
    }

    /**
     * 部分选取 做全选  全部选择做反选
     */
    private void AllTheSelected(Boolean aBoolean) {
        int number = 0;
        for (int j = 0; j < goodsList.size(); j++) {
            if (goodsList.get(j).get("selected").equals("1")) {
                number++;
            }
        }
        if (aBoolean) {
            //全部选择  反选
            if (number == goodsList.size()) {
                for (int i = 0; i < goodsList.size(); i++) {
                    goodsList.get(i).put("selected", "0");
                }
                mAllChekbox.setChecked(false);
                //全部未选
            } else if (number == 0) {
                for (int i = 0; i < goodsList.size(); i++) {
                    goodsList.get(i).put("selected", "1");
                }
                mAllChekbox.setChecked(true);
                //部分选择
            } else {
                for (int i = 0; i < goodsList.size(); i++) {
                    goodsList.get(i).put("selected", "1");
                }
                mAllChekbox.setChecked(true);
            }
        } else {
            if (number == goodsList.size()) {
                mAllChekbox.setChecked(true);
            } else {
                mAllChekbox.setChecked(false);
            }
        }

        adapter.notifyDataSetChanged();
        priceContro();

    }


}
