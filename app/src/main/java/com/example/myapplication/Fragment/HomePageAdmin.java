package com.example.myapplication.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
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
import com.example.myapplication.StatusBarUtil;
import com.example.myapplication.tool.NetImage;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class HomePageAdmin extends Fragment implements OnBannerListener{

    public HomePageAdmin() {
        // Required empty public constructor
    }
    private Banner mBanner;
    private MyImageLoader mMyImageLoader;
    private ArrayList<String> imagePath;
    private ArrayList<String> imageTitle;
    // 下方导航栏控件

    // 上方搜索控件
    private ImageButton search_image;
    private TextView search_text;
    private Context mcontext;

    RequestQueue mQueue;
    int ALBUM_REQUEST_CODE = 1;
    int CROP_REQUEST_CODE = 3;
    int position = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_home_admin, container, false);
    }
    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        TextView title=(TextView)view.findViewById(R.id.titleView);
        StatusBarUtil.setTranslucentForImageViewInFragment(getActivity(), 0, null);
        title.setText("书城");
        mcontext=getActivity();
        init();
        // 设置搜索跳转
        setSearchJump();
    }


    private void init() {
        initData();
        initBanner();
        search_text = getView().findViewById(R.id.search_text);
    }

    private void initData() {
        imagePath = new ArrayList<>();
        imageTitle = new ArrayList<>();

        imagePath.add("http://47.100.226.176:8080/XueBaJun/head_image/cover_for_book_0.jpg");
        imagePath.add("http://47.100.226.176:8080/XueBaJun/head_image/cover_for_book_1.jpg");
        imagePath.add("http://47.100.226.176:8080/XueBaJun/head_image/cover_for_book_2.jpg");

    }

    private void initBanner() {
        mMyImageLoader = new MyImageLoader();
        mBanner = getView().findViewById(R.id.banner);
        //设置样式，里面有很多种样式可以自己都看看效果
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
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
        this.position = position;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);

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

        search_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, SearchActivity.class);
                //intent.putExtra("user",(Serializable) user);
                startActivity(intent);
            }
        });
    }


    // 裁剪图片
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 128);
        intent.putExtra("aspectY", 60);
        intent.putExtra("outputX", 1280);
        intent.putExtra("outputY", 600);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        switch (requestCode){
            case 1:    //调用相册后返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    cropPhoto(uri);
                }
                break;
            case 3:     //调用剪裁后返回
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    //在这里获得了剪裁后的Bitmap对象，可以用于上传
                    Bitmap image = bundle.getParcelable("data");
                    // 将image上传到服务器中，并变更UI
                    NetImage head = new NetImage();
                    head.upImage(image,"cover_for_book_"+position,mQueue);
                }
                break;
        }
    }


}
