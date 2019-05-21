package com.example.myapplication.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.myapplication.R;

/**
 * A simple {@link Fragment} subclass.
 */

import android.content.Context;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.myapplication.SearchActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

public class HomePage_Fragment extends Fragment implements OnBannerListener{

    public HomePage_Fragment() {
        // Required empty public constructor
    }
    private Banner mBanner;
    private MyImageLoader mMyImageLoader;
    private ArrayList<Integer> imagePath;
    private ArrayList<String> imageTitle;
    // 下方导航栏控件

    // 上方搜索控件
    private ImageButton search_image;
    private TextView search_text;
    private Context mcontext;
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
        title.setText("书城");
        mcontext=getActivity();
        init();
        // 设置搜索跳转
        setSearchJump();
    }


    private void init() {
        initData();
        initBanner();
        search_image = getView().findViewById(R.id.search_image);
        search_text = getView().findViewById(R.id.search_text);
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
        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, SearchActivity.class);
                //intent.putExtra("user",(Serializable) user);
                startActivity(intent);
            }
        });
        search_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, SearchActivity.class);
                //intent.putExtra("user",(Serializable) user);
                startActivity(intent);
            }
        });
    }



}
