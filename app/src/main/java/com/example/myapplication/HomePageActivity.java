package com.example.myapplication;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.User;
import com.example.myapplication.tool.TokenHelper;
import com.google.gson.Gson;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity implements OnBannerListener {
    private Banner mBanner;
    private MyImageLoader mMyImageLoader;
    private ArrayList<Integer> imagePath;
    private ArrayList<String> imageTitle;

    // 上方搜索控件
    private ImageButton search_image;
    private TextView search_text;

    // 专业类书籍
    private LinearLayout professional_book_top1_linearLayout;
    private ImageView professional_book_top1_cover_image;
    private TextView professional_book_top1_name_text;
    private TextView professional_book_top1_num_text;
    private TextView professional_book_top1_inventory_text;
    private TextView professional_book_top1_price_text;

    // 登陆用户相关信息
    private User user;

    RequestQueue mQueue;
    TokenHelper tokenHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        TextView title=findViewById(R.id.titleView);
        title.setText("书城");
        init();
        // 设置搜索跳转
        setSearchJump();
        // 向服务器请求用户信息
        getUser();
        // 专业书籍显示
        showProfessionalBooks();
        // 设置专业书籍跳转
        setProfessionalBooksBlockJump();
    }



    private void init() {
        initData();
        initBanner();
        user = new User();
        tokenHelper = new TokenHelper();
        mQueue = Volley.newRequestQueue(HomePageActivity.this);
        search_text = findViewById(R.id.search_text);
        professional_book_top1_cover_image = findViewById(R.id.professional_book_top1_cover_image);
        professional_book_top1_inventory_text = findViewById(R.id.professional_book_top1_inventory_text);
        professional_book_top1_linearLayout = findViewById(R.id.professional_book_top1_linearLayout);
        professional_book_top1_name_text = findViewById(R.id.professional_book_top1_name_text);
        professional_book_top1_num_text = findViewById(R.id.professional_book_top1_num_text);
        professional_book_top1_price_text = findViewById(R.id.professional_book_top1_price_text);
    }

    private void initData() {
        imagePath = new ArrayList<>();
        imageTitle = new ArrayList<>();
        imagePath.add(R.drawable.patrik);
        imagePath.add(R.drawable.patrik);
        imagePath.add(R.drawable.patrik);
        imageTitle.add("我是海鸟一号");
        imageTitle.add("我是海鸟二号");
        imageTitle.add("我是海鸟3号");
    }

    private void initBanner() {
        mMyImageLoader = new MyImageLoader();
        mBanner = findViewById(R.id.banner);
        //设置样式，里面有很多种样式可以自己都看看效果
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(mMyImageLoader);
        //设置轮播的动画效果,里面有很多种特效,可以都看看效果。
        mBanner.setBannerAnimation(Transformer.ZoomOutSlide);
        //轮播图片的文字
        mBanner.setBannerTitles(imageTitle);
        //设置轮播间隔时间
        mBanner.setDelayTime(3000);
        //设置是否为自动轮播，默认是true
        mBanner.isAutoPlay(true);
        //设置指示器的位置，小点点，居中显示
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //设置图片加载地址
        mBanner.setImages(imagePath)
                //轮播图的监听
                .setOnBannerListener(this)
                //开始调用的方法，启动轮播图。
                .start();

    }

    /**
     * 轮播图的监听
     *
     * @param position
     */
    @Override
    public void OnBannerClick(int position) {
        Toast.makeText(this, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();
    }


    /**
     * 图片加载类
     */
    private class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load(path)
                    .into(imageView);
        }
    }

    private void setSearchJump() {
        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, SearchActivity.class);
                //intent.putExtra("user",(Serializable) user);
                startActivity(intent);
            }
        });
        search_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomePageActivity.this, SearchActivity.class);
                //intent.putExtra("user",(Serializable) user);
                startActivity(intent);
            }
        });
    }

    private void getUser() {
        user.setUsername("蓝菇");
        user.setUsermajor("public");
    }

    private void showProfessionalBooks() {
        // 服务器请求专业书籍
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("major", user.getUsermajor());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String url = "http://193.112.98.224:8080/shopapp/book/getAllByMajor/"+user.getUsermajor();
        String url = "http://193.112.98.224:8080/shopapp/book/getAll";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, jsonObject, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());
                Log.e("##", message.getExtend().get("booklist").toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("##", "HomePage获取专业书籍出错");
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void setProfessionalBooksBlockJump() {
        professional_book_top1_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断概述及是否在购物车中
                // 如果购物车中已存在，报错
                // 如果购物车中没有该书，加入购物车
                Book book = new Book();
                addBookToCart(book);
            }
        });
    }

    private void addBookToCart(Book book){
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("bookid", book.getBookid());
            jsonObject.put("token", tokenHelper.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String url = "http://193.112.98.224:8080/shopapp/book/getAllByMajor/"+user.getUsermajor();
        String url = "http://193.112.98.224:8080/shopapp/book/getAll";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, jsonObject, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        mQueue.add(jsonObjectRequest);
    }

}
