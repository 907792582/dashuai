package com.example.myapplication.Fragment;
import android.os.Bundle;
import android.os.Message;
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
import com.example.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.myapplication.SearchActivity;
import com.example.myapplication.SearchResultActivity;
import com.example.myapplication.StatusBarUtil;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.User;
import com.example.myapplication.tool.NetImage;
import com.example.myapplication.tool.TokenHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomePage_Fragment extends Fragment implements OnBannerListener{

    public HomePage_Fragment() {
        // Required empty public constructor
    }
    private Banner mBanner;
    private MyImageLoader mMyImageLoader;
    private ArrayList<Integer> imagePath;
    private ArrayList<String> imageTitle;

    // 上方搜索控件
    private ImageButton search_image;
    private TextView search_text;
    private Context mcontext;

    // 专业类书籍
    private LinearLayout professional_book_top1_linearLayout;
    private ImageView professional_book_top1_cover_image;
    private TextView professional_book_top1_name_text;
    private TextView professional_book_top1_num_text;
    private TextView professional_book_top1_inventory_text;
    private TextView professional_book_top1_price_text;

    private ImageView professional_book_top2_cover_image;
    private ImageView professional_book_top3_cover_image;
    private ImageView professional_book_top4_cover_image;

    private Button more_pro_button;


    // 公共类书籍
    private LinearLayout public_book_top1_linearLayout;
    private ImageView public_book_top1_cover_image;
    private TextView public_book_top1_name_text;
    private TextView public_book_top1_num_text;
    private TextView public_book_top1_inventory_text;
    private TextView public_book_top1_price_text;

    private ImageView public_book_top2_cover_image;
    private ImageView public_book_top3_cover_image;
    private ImageView public_book_top4_cover_image;

    private Button more_pub_button;

    // 存放书籍
    List<Book> professional_book_list;
    List<Book> public_book_list;

    // 当前登陆用户
    User user;
    // volley
    RequestQueue mQueue;
    // token
    TokenHelper tokenHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home, container, false);
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        TextView title=(TextView)view.findViewById(R.id.titleView);
        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, null);
        title.setText("书城");
        mcontext=getActivity();


        init(view);

        // 设置搜索跳转
        setSearchJump();

        // 向服务器请求用户信息。信息返回后显示用户对应的专业书籍
        getUser();

        // 公共书籍获取并显示
        getPublicBooks();


    }



    private void init(View view) {
        initData();
        initBanner();
        // search_image = view.findViewById(R.id.searchView);
        search_text = view.findViewById(R.id.search_text);
        professional_book_top1_cover_image = view.findViewById(R.id.professional_book_top1_cover_image);
        professional_book_top1_inventory_text = view.findViewById(R.id.professional_book_top1_inventory_text);
        professional_book_top1_linearLayout = view.findViewById(R.id.professional_book_top1_linearLayout);
        professional_book_top1_name_text = view.findViewById(R.id.professional_book_top1_name_text);
        professional_book_top1_num_text = view.findViewById(R.id.professional_book_top1_num_text);
        professional_book_top1_price_text = view.findViewById(R.id.professional_book_top1_price_text);
        professional_book_top2_cover_image = view.findViewById(R.id.professional_book_top2_cover_image);
        professional_book_top3_cover_image = view.findViewById(R.id.professional_book_top3_cover_image);
        professional_book_top4_cover_image = view.findViewById(R.id.professional_book_top4_cover_image);
        public_book_top1_cover_image = view.findViewById(R.id.public_book_top1_cover_image);
        public_book_top1_inventory_text = view.findViewById(R.id.public_book_top1_inventory_text);
        public_book_top1_linearLayout = view.findViewById(R.id.public_book_top1_linearLayout);
        public_book_top1_name_text = view.findViewById(R.id.public_book_top1_name_text);
        public_book_top1_num_text = view.findViewById(R.id.public_book_top1_num_text);
        public_book_top1_price_text = view.findViewById(R.id.public_book_top1_price_text);
        public_book_top2_cover_image = view.findViewById(R.id.public_book_top2_cover_image);
        public_book_top3_cover_image = view.findViewById(R.id.public_book_top3_cover_image);
        public_book_top4_cover_image = view.findViewById(R.id.public_book_top4_cover_image);
        more_pro_button = view.findViewById(R.id.more_pro_button);
        more_pub_button = view.findViewById(R.id.more_pub_button);


        professional_book_list = new ArrayList<>();
        public_book_list = new ArrayList<>();
        mQueue = Volley.newRequestQueue(mcontext);
        tokenHelper = new TokenHelper();
        user = new User();
    }


    private void initData() {
        imagePath = new ArrayList<>();
        imageTitle = new ArrayList<>();
        imagePath.add(R.drawable.patrik);
        imagePath.add(R.drawable.patrik);
        imagePath.add(R.drawable.patrik);
        imageTitle.add("   ");
        imageTitle.add("   ");
        imageTitle.add("   ");
    }

    private void initBanner() {
        mMyImageLoader = new MyImageLoader();
        mBanner = getView().findViewById(R.id.banner);
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
        Toast.makeText(mcontext, "你点了第" + (position + 1) + "张轮播图", Toast.LENGTH_SHORT).show();
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
        /*search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, SearchActivity.class);
                //intent.putExtra("user",(Serializable) user);
                startActivity(intent);
            }
        });*/
        search_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, SearchActivity.class);
                //intent.putExtra("user",(Serializable) user);
                startActivity(intent);
            }
        });
    }

    private void getUser() {

        String url = "http://193.112.98.224:8080/shopapp/User/finduser/"+tokenHelper.getToken();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", "User信息返回"+jsonObject.toString());
                // 操作成功
                if(message.getCode() == 100){

                    try {
                        Log.e("##", "User"+jsonObject.getJSONObject("extend").getJSONObject("user").toString());
                        user = new Gson().fromJson(jsonObject.getJSONObject("extend").getJSONObject("user").toString(), User.class);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // user = new Gson().fromJson(jsonObject.getJSONObject("extend").getJSONObject("user").toString(), User.class);

                    // 专业书籍显示
                    getProfessionalBooks();


                }else{
                    // 操作失败
                    Toast.makeText(mcontext, "用户信息获取失败，请联系管理员" , Toast.LENGTH_SHORT).show();
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

    private void getProfessionalBooks() {
        // 服务器请求专业书籍
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("major", user.getUsermajor());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://193.112.98.224:8080/shopapp/book/getAllByMajor/"+user.getUsermajor();
        // String url = "http://193.112.98.224:8080/shopapp/book/getAll";
        Log.e("##HomePage获取专业书籍url", url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##booklistMsg", jsonObject.toString());
                Log.e("##booklist", message.getExtend().get("booklist").toString());
                try {

                    JSONArray ob = jsonObject.getJSONObject("extend").getJSONArray("booklist");

                    // List<Book> bookList = new ArrayList<>();
                    for(int i = 0;i<ob.length();i++){
                        JSONObject temp = ob.getJSONObject(i);
                        professional_book_list.add(new Gson().fromJson(temp.toString(), Book.class));
                        Log.e("##", "HOME获得专业书籍："+temp.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                showProfessionalBooks();
                // 设置专业书籍跳转
                setProfessionalBooksBlockJump();
                setMoreButtonFun(more_pro_button,professional_book_list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("##", "HomePage获取专业书籍出错");
                Toast.makeText(mcontext, "专业书籍信息获取失败，请联系管理员" , Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void getPublicBooks() {
        // 服务器请求公共书籍

        String url = "http://193.112.98.224:8080/shopapp/book/getAllByMajor/public";
        // String url = "http://193.112.98.224:8080/shopapp/book/getAll";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());
                Log.e("##", message.getExtend().get("booklist").toString());
                try {

                    JSONArray ob = jsonObject.getJSONObject("extend").getJSONArray("booklist");

                    // List<Book> bookList = new ArrayList<>();
                    for(int i = 0;i<ob.length();i++){
                        JSONObject temp = ob.getJSONObject(i);
                        public_book_list.add(new Gson().fromJson(temp.toString(), Book.class));
                        Log.e("##", "HOME获得专业书籍："+temp.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(public_book_list.size()>0){
                    showPublicBooks();
                    setPublicBooksBlockJump();
                    setMoreButtonFun(more_pub_button,public_book_list);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("##", "HomePage获取专业书籍出错");
                Toast.makeText(mcontext, "专业书籍信息获取失败，请联系管理员" , Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void showProfessionalBooks() {

        NetImage netImage = new NetImage();
        Book topBook = professional_book_list.get(0);
        netImage.setCoverImage(mQueue,professional_book_top1_cover_image,"http://193.112.98.224:8080/shopapp/BookImage/"+topBook.getBookimage());
        professional_book_top1_name_text.setText(topBook.getBookname());
        professional_book_top1_num_text.setText("编号："+String.valueOf(topBook.getBookid()));
        professional_book_top1_inventory_text.setText("库存： "+topBook.getBookstock());
        professional_book_top1_price_text.setText("￥ "+String.valueOf(topBook.getBookprice()));

        //netImage.setCoverImage(mQueue,professional_book_top1_cover_image,"http://193.112.98.224:8080/shopapp/BookImage/"+professional_book_list.get(1).getBookimage());
        //netImage.setCoverImage(mQueue,professional_book_top1_cover_image,"http://193.112.98.224:8080/shopapp/BookImage/"+professional_book_list.get(2).getBookimage());
        //netImage.setCoverImage(mQueue,professional_book_top1_cover_image,"http://193.112.98.224:8080/shopapp/BookImage/"+professional_book_list.get(3).getBookimage());

    }

    private void showPublicBooks() {

        NetImage netImage = new NetImage();
        Book topBook = public_book_list.get(0);
        netImage.setCoverImage(mQueue,public_book_top1_cover_image,"http://193.112.98.224:8080/shopapp/BookImage/"+topBook.getBookimage());
        public_book_top1_name_text.setText(topBook.getBookname());
        public_book_top1_num_text.setText("编号："+String.valueOf(topBook.getBookid()));
        public_book_top1_inventory_text.setText("库存： "+topBook.getBookstock());
        public_book_top1_price_text.setText("￥ "+String.valueOf(topBook.getBookprice()));

        //netImage.setCoverImage(mQueue, public_book_top2_cover_image,"http://193.112.98.224:8080/shopapp/BookImage/"+professional_book_list.get(1).getBookimage());
        //netImage.setCoverImage(mQueue, public_book_top3_cover_image,"http://193.112.98.224:8080/shopapp/BookImage/"+professional_book_list.get(2).getBookimage());
        //netImage.setCoverImage(mQueue, public_book_top4_cover_image,"http://193.112.98.224:8080/shopapp/BookImage/"+professional_book_list.get(3).getBookimage());

    }

    private void setProfessionalBooksBlockJump() {
        professional_book_top1_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断概述及是否在购物车中
                // 如果购物车中已存在，报错
                // 如果购物车中没有该书，加入购物车
                Book top_1_Book = professional_book_list.get(0);
                addBookToCart(top_1_Book);
            }
        });
    }

    private void setPublicBooksBlockJump() {
        public_book_top1_linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 判断概述及是否在购物车中
                // 如果购物车中已存在，报错
                // 如果购物车中没有该书，加入购物车
                Book top_1_Book = public_book_list.get(0);
                addBookToCart(top_1_Book);
            }
        });

        public_book_top2_cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book top_2_Book = public_book_list.get(1);
                addBookToCart(top_2_Book);
            }
        });
        public_book_top3_cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book top_3_Book = public_book_list.get(2);
                addBookToCart(top_3_Book);
            }
        });
        public_book_top4_cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book top_4_Book = public_book_list.get(3);
                addBookToCart(top_4_Book);
            }
        });
    }

    private void setMoreButtonFun(Button more_button, final List<Book> bookList){
        more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, SearchResultActivity.class);
                Msg message = new Msg();
                message.getExtend().put("bookList",bookList);
                intent.putExtra("bookList", message);
                startActivity(intent);
            }
        });
    }

    private void addBookToCart(final Book book){

        //String url = "http://193.112.98.224:8080/shopapp/book/getAllByMajor/"+user.getUsermajor();
        String url = "http://193.112.98.224:8080/shopapp/bookbuy/addtobuy/"+String.valueOf(user.getUserid())+"/"+book.getBookid()+"/1";
        Log.e("##", "加入购物车url:"+url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                if(message.getCode() == 100){

                    Toast.makeText(mcontext, book.getBookname()+"已加入购物车" , Toast.LENGTH_SHORT).show();

                }else{
                    // 操作失败
                    Toast.makeText(mcontext, "加入购物车失败，请联系管理员" , Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(mcontext, "服务器返回出错，请联系管理员" , Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);
    }


}
