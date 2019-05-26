package com.example.myapplication.Fragment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.necer.ndialog.NDialog;

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
    private double totalPrice = 0.00;
    private int totalCount = 0;
    private List<HashMap<String, String>> goodsList;
    private List<HashMap<String, String>> goodsList_order;
    private OrderAdapter adapter;
    private Context mcontext;
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
        }
        initView();
        priceContro();

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
    private void initDate() {
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
                                break;
                        }
                    }
                }).create(NDialog.CONFIRM).show();

                break;
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
