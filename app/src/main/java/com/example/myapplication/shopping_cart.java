package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.CartAdapter;
import com.example.myapplication.StatusBarUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.io.Serializable;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class shopping_cart extends AppCompatActivity implements com.example.myapplication.CartAdapter.ItemClickListener {
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
    public List<HashMap<String, String>> goodsList_order;
    private com.example.myapplication.CartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_cart);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
        mTvTitle.setText("购物车");
        //模拟一些数据
        initDate();
        initView();
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
        adapter = new CartAdapter(this, goodsList, R.layout.item_cehua);
        adapter.setOnItemClickListener(this);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }



    @OnClick({R.id.all_chekbox, R.id.tv_go_to_pay,R.id.search_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.all_chekbox:
                AllTheSelected(true);
                break;
            case R.id.search_image:
                Intent intent_s=new Intent();
                intent_s.setClass(this,search.class);
                startActivity(intent_s);
            case R.id.tv_go_to_pay:
                if (totalCount <= 0) {
                    Toast.makeText(this, "请选择要付款的商品~", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Bundle bundle=new Bundle();
                    Intent intent=new Intent();
                    goodsList_order=new ArrayList<>();

                    for (int i = 0; i < goodsList.size(); i++)
                        {
                            if (goodsList.get(i).get("id").equals("1"))
                            {
                                     goodsList_order.add(goodsList.get(i));
                            }
                        }

                  intent.setClass(this,check_order.class);
                    bundle.putSerializable("goodsList_order",(Serializable)goodsList_order);
                    System.out.println(goodsList_order.get(1).get("name"));
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
            Toast.makeText(this, "亲，已达库存上限~", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "受不了了，宝贝不能再减少了哦~", Toast.LENGTH_SHORT).show();
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
