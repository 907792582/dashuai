package com.example.myapplication;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.Fragment.ShoppingCart_Fragment;
import com.example.myapplication.Fragment.UserInfo_Fragment;
import com.example.myapplication.Fragment.HomePage_Fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //底部菜单栏3个TextView
    private TextView mTextBook;
    private TextView mTextCart;
    private TextView mTextUser;

    //3个Fragment
    private Fragment mHomePage_Fragment;
    private Fragment mShopping_Fragment;
    private Fragment mUserInfo_Fragment;
 private List<HashMap<String, String>> goodsList_order;
    //标记当前显示的Fragment
    private int fragmentId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goodsList_order = new ArrayList<>();
        Intent intent=getIntent();
        if(intent!=null) {
            Bundle bundle = intent.getExtras();
            if (bundle!=null)
                goodsList_order = (List<HashMap<String, String>>) bundle.getSerializable("goodsList_order");
        }
        //初始化
        init();
        //根据传入的Bundle对象判断Activity是正常启动还是销毁重建
        if(savedInstanceState == null){
            //设置第一个Fragment默认选中
            if(goodsList_order.isEmpty())
            setFragment(0);
            else
                {
                setFragment(1);
            }
        }
    }
public List<HashMap<String, String>> getGoodsList_order()
{
    return goodsList_order;
}
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //通过onSaveInstanceState方法保存当前显示的fragment
        outState.putInt("fragment_id",fragmentId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        //通过FragmentManager获取保存在FragmentTransaction中的Fragment实例
       mHomePage_Fragment = (HomePage_Fragment)mFragmentManager
                .findFragmentByTag("homepage_fragment");
        mShopping_Fragment = (ShoppingCart_Fragment)mFragmentManager
                .findFragmentByTag("cart_fragment");
        mUserInfo_Fragment = (UserInfo_Fragment)mFragmentManager
                .findFragmentByTag("user_fragment");
        //恢复销毁前显示的Fragment
        setFragment(savedInstanceState.getInt("fragment_id"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
            case R.id.book_image_button:
                setFragment(0);
                break;
            case R.id.cart_image_button:
                setFragment(1);
                break;
            case R.id.user_image_button:
                setFragment(2);
                break;
        }
    }

    private void init(){
        //初始化控件
        mTextBook = (TextView)findViewById(R.id.book_image_button);
        mTextCart = (TextView)findViewById(R.id.cart_image_button);
        mTextUser = (TextView)findViewById(R.id.user_image_button);

        //设置监听
        mTextBook.setOnClickListener(this);
        mTextCart.setOnClickListener(this);
        mTextUser.setOnClickListener(this);
    }

    private void setFragment(int index){
        //获取Fragment管理器
        FragmentManager mFragmentManager = getSupportFragmentManager();
        //开启事务
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        //隐藏所有Fragment
        hideFragments(mTransaction);
        switch (index){
            default:
                break;
            case 0:
                fragmentId = 0;
                //设置菜单栏为选中状态（修改文字和图片颜色）

                mTextBook.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.ic_book_pressed,0,0);
                //显示对应Fragment
                if(mHomePage_Fragment == null){
                    mHomePage_Fragment = new HomePage_Fragment();
                    mTransaction.add(R.id.container,mHomePage_Fragment,
                            "homepage_fragment");
                }else {
                    mTransaction.show(mHomePage_Fragment);
                }
                break;
            case 1:
                fragmentId = 1;
                mTextCart.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.ic_cart_pressed,0,0);
                if(mShopping_Fragment == null){
                    mShopping_Fragment = new ShoppingCart_Fragment();
                    mTransaction.add(R.id.container, mShopping_Fragment,
                            "cart_fragment");
                }else {
                    mTransaction.show(mShopping_Fragment);
                }
                break;
            case 2:
                fragmentId = 2;
                mTextUser.setCompoundDrawablesWithIntrinsicBounds(0,
                        R.drawable.ic_user_pressed,0,0);
                if(mUserInfo_Fragment == null){
                    mUserInfo_Fragment = new UserInfo_Fragment();
                    mTransaction.add(R.id.container, mUserInfo_Fragment,
                            "user_fragment");
                }else {
                    mTransaction.show(mUserInfo_Fragment);
                }
                break;
        }
        //提交事务
        mTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction){
        if(mHomePage_Fragment != null){
            //隐藏Fragment
            transaction.hide(mHomePage_Fragment);
            //将对应菜单栏设置为默认状态
            mTextBook.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.ic_book,0,0);
        }
        if(mShopping_Fragment != null){
            transaction.hide(mShopping_Fragment);
            mTextCart.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.ic_cart,0,0);
        }
        if(mUserInfo_Fragment != null){
            transaction.hide(mUserInfo_Fragment);
            mTextUser.setCompoundDrawablesWithIntrinsicBounds(0,
                    R.drawable.ic_user,0,0);
        }
    }
}
