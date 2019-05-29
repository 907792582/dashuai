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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.AdminInfoAdapter;
import com.example.myapplication.CheckOrderActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.SearchActivity;
import com.example.myapplication.UserUnconfiguredOrderActivity;
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
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoAdmin extends Fragment {

    ImageButton head_image_button;
    RequestQueue mQueue;
    User user = new User();
    int ALBUM_REQUEST_CODE = 1;
    int CROP_REQUEST_CODE = 3;
    TokenHelper tokenHelper = new TokenHelper();
    // 环境
    private Context mcontext;
    private AdminInfoAdapter adapter;
    private List<HashMap<String,String>> goodsList;
    private ListView mListView;

    private Button logoff_button;
    private List<Shop> shopList = new ArrayList<>();


    public InfoAdmin() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_admin_info, container, false);
    }
    public void onViewCreated(View view,Bundle savedInstanceState) {
        //页面初始化
        super.onViewCreated(view,savedInstanceState);
        mcontext=getActivity();
        goodsList=new ArrayList<>();
        mListView=view.findViewById(R.id.listView);
        ButterKnife.bind(this,view);
        TextView tvsearch=view.findViewById(R.id.search_text);
        tvsearch.setVisibility(View.GONE);
        TextView title=view.findViewById(R.id.titleView);
        title.setText("用户中心");


        init(view);
        getUser();
        setClickFunction();

        getTokenOrder();
    }


    private void init(View view) {
        user = new User();
        head_image_button = view.findViewById(R.id.head_image_button);
        mQueue = Volley.newRequestQueue(mcontext);

        logoff_button = view.findViewById(R.id.log_off_button);
    }

    private void getUser() {

        String url = "http://47.100.226.176:8080/shopapp/User/finduser/"+tokenHelper.getToken();

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

    private void getTokenOrder() {
        String url = "http://47.100.226.176:8080/shopapp/shop/findshopyes";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());

                // 操作成功
                if(message.getCode() == 100){

                    JSONArray temp = null;
                    try {
                        temp = jsonObject.getJSONObject("extend").getJSONArray("shop");

                        for(int i = 0;i<temp.length();i++){
                            Shop item = new Gson().fromJson(temp.get(i).toString(), Shop.class);
                            shopList.add(item);
                            setTokenOrder();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }else{
                    // 操作失败
                    Toast.makeText(mcontext, message.getExtend().get("va_msg").toString() , Toast.LENGTH_SHORT).show();
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

    private void setTokenOrder() {
        initDate();
        initView();
    }

    private void initView() {
        adapter = new AdminInfoAdapter(mcontext, goodsList, R.layout.item_admin_info);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
    private void initDate() {
        goodsList = new ArrayList<>();
        for (int i = 0; i < shopList.size(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put("stu_number", shopList.get(i).getShopid());
            map.put("stu_name", shopList.get(i).getBookname());
            map.put("order_status", shopList.get(i).getShopstatus());
            goodsList.add(map);
        }
    }

    private void setUserInfoBlock() {
        NetImage image = new NetImage();
        String url = "http://47.100.226.176:8080/XueBaJun/head_image/"+user.getUsername()+".jpg";
        image.setHeadImage(mQueue,head_image_button,url);
        logoff_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://47.100.226.176:8080/shopapp/user/logoff/"+tokenHelper.getToken();

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
