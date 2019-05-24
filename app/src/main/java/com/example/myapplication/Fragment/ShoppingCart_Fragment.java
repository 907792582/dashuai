package com.example.myapplication.Fragment;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.myapplication.Adapter.CartAdapter;
import com.example.myapplication.StatusBarUtil;
import com.example.myapplication.SearchActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShoppingCart_Fragment extends Fragment implements CartAdapter.ItemClickListener{

    @BindView(R.id.titleView)
    TextView mTvTitle;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.all_chekbox)
    CheckBox mAllChekbox;
    @BindView(R.id.tv_total_price)
    TextView mTvTotalPrice;
    @BindView(R.id.tv_go_to_pay)
    TextView mTvGoToPay;
    private double totalPrice = 0.00;
    private int totalCount = 0;
    private List<HashMap<String, String>> goodsList;
    private List<HashMap<String, String>> goodsList_order;
    private CartAdapter adapter;
    private Context mcontext;
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
        onAttach(mcontext);
        ButterKnife.bind(this,view);
        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, null);
        mTvTitle.setText("购物车");
        if(goodsList.isEmpty())
        {
            LinearLayout cartLayout=view.findViewById(R.id.cart_layout);
            LinearLayout cartbottomBar=view.findViewById(R.id.cartbottomBar);
            cartbottomBar.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            TextView cartEmpty=view.findViewById(R.id.tv_cart_empty);
            cartEmpty.setText("购物车是空的诶~");
        }
        else
        {
            TextView cartEmpty=view.findViewById(R.id.tv_cart_empty);
            cartEmpty.setVisibility(View.GONE);
        }

        initView();
    }
@Override
public void onAttach(Activity activity)
{
    super.onAttach(activity);
    goodsList=((MainActivity)activity).getGoodsList_order();
    if(goodsList!=null){
        for(int i=0;i<goodsList.size();i++)
        {
            goodsList.get(i).put("id","0");
        }
    }

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
                        if (goodsList.get(i).get("id").equals("1"))
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
            hashMap.put("id", "1");
        } else {
            hashMap.put("id", "0");
        }
        goodsList.set(position, hashMap);
        AllTheSelected(false);
    }

    //删除
    @Override
    public void ItemDeleteClickListener(View view, int position) {
        goodsList.remove(position);
        AllTheSelected(false);
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
            if (goodsList.get(i).get("id").equals("1")) {
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
            if (goodsList.get(j).get("id").equals("1")) {
                number++;
            }
        }
        if (aBoolean) {
            //全部选择  反选
            if (number == goodsList.size()) {
                for (int i = 0; i < goodsList.size(); i++) {
                    goodsList.get(i).put("id", "0");
                }
                mAllChekbox.setChecked(false);
                //全部未选
            } else if (number == 0) {
                for (int i = 0; i < goodsList.size(); i++) {
                    goodsList.get(i).put("id", "1");
                }
                mAllChekbox.setChecked(true);
                //部分选择
            } else {
                for (int i = 0; i < goodsList.size(); i++) {
                    goodsList.get(i).put("id", "1");
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
