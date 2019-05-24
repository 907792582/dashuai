package com.example.myapplication.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.CheckOrderActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.SearchActivity;
import com.example.myapplication.UserUnconfiguredOrderActivity;
import com.example.myapplication.model.User;
import com.example.myapplication.tool.NetImage;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfo_Fragment extends Fragment {

    ImageButton head_image_button;

    RequestQueue mQueue;
    User user;
    int ALBUM_REQUEST_CODE = 1;
    int CROP_REQUEST_CODE = 3;

    // 环境
    private Context mcontext;

    public UserInfo_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_user_info, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState) {
        //页面初始化
        super.onViewCreated(view,savedInstanceState);
        mcontext=getActivity();
        ButterKnife.bind(this,view);
        TextView tvsearch=view.findViewById(R.id.search_text);
        tvsearch.setVisibility(View.GONE);
        TextView title=view.findViewById(R.id.titleView);
        title.setText("用户中心");
        init(view);
        getUser();
        setPage();
        setClickFunction();
    }

    @OnClick({R.id.more_unconfigured_button,R.id.more_picked_button,R.id.more_unpicked_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.more_unconfigured_button:
                Intent intent_unconfigured=new Intent();
                intent_unconfigured.setClass(mcontext, UserUnconfiguredOrderActivity.class);
                startActivity(intent_unconfigured);
                break;

            case R.id.more_picked_button:
                Intent intent_picked=new Intent();
                intent_picked.setClass(mcontext, SearchActivity.class);
                startActivity(intent_picked);
                break;
            case R.id.more_unpicked_button:
                Intent intent_unpicked=new Intent();
                intent_unpicked.setClass(mcontext, SearchActivity.class);
                startActivity(intent_unpicked);
                break;
        }
    }

    private void init(View view) {
        user = new User();
        head_image_button = view.findViewById(R.id.head_image_button);
        mQueue = Volley.newRequestQueue(mcontext);
    }

    private void getUser() {
        user.setUsername("wsc");
    }

    private void setPage() {
        setUserInfoBlock();
    }

    private void setUserInfoBlock() {
        NetImage image = new NetImage();
        String url = "http://47.100.226.176:8080/XueBaJun/head_image/"+user.getUsername()+".jpg";
        image.setHeadImage(mQueue,head_image_button,url);
    }

    // 设置点击事件
    private void setClickFunction(){

        // 头像从手机相册上传，剪裁为300*300的大小
        head_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
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
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
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
                    head.uploadImage(image,user.getUsername(),head_image_button,mQueue);
                }
                break;
        }
    }

}
