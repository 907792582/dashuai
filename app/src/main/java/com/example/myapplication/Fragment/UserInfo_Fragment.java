package com.example.myapplication.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.CheckOrderActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.SearchActivity;
import com.example.myapplication.UserPickedOrderActivity;
import com.example.myapplication.UserUnconfiguredOrderActivity;
import com.example.myapplication.UserUnpickedOrderActivity;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.Shop;
import com.example.myapplication.model.User;
import com.example.myapplication.tool.NetImage;
import com.example.myapplication.tool.TokenHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfo_Fragment extends Fragment {

    // 用户信息块
    private ImageButton head_image_button;
    private TextView user_name_text,user_major_text;
    private Button log_off_button,question_button;


    // 未配置列表、待提取列表、已提取列表
    private List<Shop> paidList,untookList,tokenList;
    private ImageView paid_shop_1_image,paid_shop_2_image,paid_shop_3_image;
    private ImageView untook_shop_1_image,untook_shop_2_image,untook_shop_3_image;
    private ImageView token_shop_1_image,token_shop_2_image,token_shop_3_image;


    RequestQueue mQueue;
    User user;
    TokenHelper tokenHelper = new TokenHelper();
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
        getOrder();
        // setPage();
        setClickFunction();
    }



    @OnClick({R.id.more_unconfigured_button,R.id.more_picked_button,R.id.more_unpicked_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.more_unconfigured_button:

                Log.e("##更多已支付列表：", paidList.toString());

                Intent intent_unconfigured=new Intent();
                Msg message_1 = new Msg();
                message_1.getExtend().put("paidList",paidList);
                intent_unconfigured.putExtra("paidList", message_1);
                intent_unconfigured.setClass(mcontext, UserUnconfiguredOrderActivity.class);
                startActivity(intent_unconfigured);
                break;

            case R.id.more_picked_button:
                Intent intent_picked=new Intent();
                Msg message_2 = new Msg();
                message_2.getExtend().put("tokenList",tokenList);
                intent_picked.putExtra("tokenList", message_2);
                intent_picked.setClass(mcontext, UserPickedOrderActivity.class);
                startActivity(intent_picked);
                break;
            case R.id.more_unpicked_button:
                Intent intent_unpicked=new Intent();
                Msg message_3 = new Msg();
                message_3.getExtend().put("untookList",untookList);
                intent_unpicked.putExtra("untookList", message_3);
                intent_unpicked.setClass(mcontext, UserUnpickedOrderActivity.class);
                startActivity(intent_unpicked);
                break;
        }
    }

    private void init(View view) {
        user = new User();
        head_image_button = view.findViewById(R.id.head_image_button);
        user_name_text = view.findViewById(R.id.user_name_text);
        user_major_text = view.findViewById(R.id.user_major_text);
        log_off_button = view.findViewById(R.id.log_off_button);
        question_button = view.findViewById(R.id.question_button);

        paidList = new ArrayList<>();
        tokenList = new ArrayList<>();
        untookList = new ArrayList<>();
        paid_shop_1_image = view.findViewById(R.id.paid_shop_1_image);
        paid_shop_2_image = view.findViewById(R.id.paid_shop_2_image);
        paid_shop_3_image = view.findViewById(R.id.paid_shop_3_image);
        untook_shop_1_image = view.findViewById(R.id.untook_shop_1_image);
        untook_shop_2_image = view.findViewById(R.id.untook_shop_2_image);
        untook_shop_3_image = view.findViewById(R.id.untook_shop_3_image);
        token_shop_1_image = view.findViewById(R.id.token_shop_1_image);
        token_shop_2_image = view.findViewById(R.id.token_shop_2_image);
        token_shop_3_image = view.findViewById(R.id.token_shop_3_image);

        mQueue = Volley.newRequestQueue(mcontext);
        tokenHelper = new TokenHelper();
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

                    // 用户信息显示
                    setUserInfoBlock();


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

    private void getOrder() {
        String url = "http://193.112.98.224:8080/shopapp/bookbuy/getinform/"+tokenHelper.getToken();
        Log.e("##订单url:",url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());

                // 操作成功
                if(message.getCode() == 100){
                    try {
                        JSONArray tokenShop = jsonObject.getJSONObject("extend").getJSONArray("已支付列表");

                        for(int i = 0;i<tokenShop.length();i++){
                            Shop item = new Gson().fromJson(tokenShop.get(i).toString(), Shop.class);
                            tokenList.add(item);

                        }

                        JSONArray paidShop = jsonObject.getJSONObject("extend").getJSONArray("已支付列表");

                        for(int i = 0;i<paidShop.length();i++){
                            Shop item = new Gson().fromJson(paidShop.get(i).toString(), Shop.class);
                            paidList.add(item);

                        }
                        Log.e("##更多已支付列表：", paidList.toString());

                        JSONArray untookShop = jsonObject.getJSONObject("extend").getJSONArray("已支付列表");

                        for(int i = 0;i<untookShop.length();i++){
                            Shop item = new Gson().fromJson(untookShop.get(i).toString(), Shop.class);
                            untookList.add(item);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // 显示界面
                    setOrderBlock();


                }else{
                    // 操作失败
                    Toast.makeText(mcontext,"订单获取失败，请联系管理员" , Toast.LENGTH_SHORT).show();
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

    private void setOrderBlock() {
        NetImage netImage = new NetImage();
        String baseUrl = "http://193.112.98.224:8080/shopapp/BookImage/";

        if(paidList.size()>0)
            netImage.setCoverImage(mQueue,paid_shop_1_image,baseUrl+paidList.get(0).getShopimage());
        if(paidList.size()>1)
            netImage.setCoverImage(mQueue,paid_shop_2_image,baseUrl+paidList.get(1).getShopimage());
        if(paidList.size()>2)
            netImage.setCoverImage(mQueue,paid_shop_3_image,baseUrl+paidList.get(2).getShopimage());

        if(untookList.size()>0)
            netImage.setCoverImage(mQueue,untook_shop_1_image,baseUrl+paidList.get(0).getShopimage());
        if(paidList.size()>1)
            netImage.setCoverImage(mQueue,untook_shop_2_image,baseUrl+paidList.get(1).getShopimage());
        if(paidList.size()>2)
            netImage.setCoverImage(mQueue,untook_shop_3_image,baseUrl+paidList.get(2).getShopimage());

        if(tokenList.size()>0)
            netImage.setCoverImage(mQueue,token_shop_1_image,baseUrl+paidList.get(0).getShopimage());
        if(paidList.size()>1)
            netImage.setCoverImage(mQueue,token_shop_2_image,baseUrl+paidList.get(1).getShopimage());
        if(paidList.size()>2)
            netImage.setCoverImage(mQueue,token_shop_3_image,baseUrl+paidList.get(2).getShopimage());
}


    private void setUserInfoBlock() {
        NetImage image = new NetImage();
        String url = "http://47.100.226.176:8080/XueBaJun/head_image/"+user.getUsername()+".jpg";
        System.out.println(user.getUsername());
        image.setHeadImage(mQueue,head_image_button,url);
        user_name_text.setText(user.getUsername());

        if(user.getUsermajor().compareTo("computer") == 0){
            user_major_text.setText("计算机技术与科学");
        }else{
            user_major_text.setText("其他学院");
        }

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

        question_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mcontext, "管理员QQ:XXXXXXXXX" , Toast.LENGTH_SHORT).show();
            }
        });

        log_off_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://193.112.98.224:8080/shopapp/user/logoff/"+tokenHelper.getToken();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

                    public void onResponse(org.json.JSONObject jsonObject) {
                        // 跳转登陆页面
                        Intent intent =new Intent();
                        intent.setClass(mcontext, LoginActivity.class);
                        startActivity(intent);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Toast.makeText(mcontext, "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
                        // 他后台没有返回信息，但是method是get所以从error出来了
                        // 跳转登陆页面
                        Intent intent =new Intent();
                        intent.setClass(mcontext, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                mQueue.add(jsonObjectRequest);
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
