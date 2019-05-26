package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.model.Msg;
import com.example.myapplication.tool.TokenHelper;
import com.google.gson.Gson;

import org.json.JSONException;

public class LoginWithTokenActivity extends AppCompatActivity {

    private Button login_again_button,login_button;
    TokenHelper tokenHelper;
    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_token);

        init();

        // 防止用户应用直接退出而账号没有下线的情况
        logoff();

        // setOnClickFun();
    }

    private void logoff() {
        String url = "http://193.112.98.224:8080/shopapp/user/logoff/"+tokenHelper.getToken();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // Toast.makeText(mcontext, "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
                // 他后台没有返回信息，但是method是get所以从error出来了
                // 跳转登陆页面
                setOnClickFun();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void setOnClickFun() {
        login_again_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 通过token再次登陆
                org.json.JSONObject jsonObject = new org.json.JSONObject();
                try {
                    jsonObject.put("token", tokenHelper.getToken());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = "http://193.112.98.224:8080/shopapp/user/loginagain/"+tokenHelper.getToken();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, new Response.Listener<org.json.JSONObject>() {

                    public void onResponse(org.json.JSONObject jsonObject) {
                        Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                        Log.e("##", jsonObject.toString());

                        if(message.getCode() == 100){
                            // 操作成功
                            if(message.getExtend().get("va_msg").toString().compareTo("此时登录成功为用户") == 0){
                                // 用户登陆成功跳转
                                Toast.makeText(getApplicationContext(), "欢迎登陆" , Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginWithTokenActivity.this, MainActivity.class);
                                // intent.putExtra("user",(Serializable) user);
                                startActivity(intent);
                            }else{
                                // 管理员登陆成功跳转
                                Intent intent = new Intent(LoginWithTokenActivity.this, MainActivity.class);
                                // intent.putExtra("user",(Serializable) user);
                                startActivity(intent);
                            }
                        }else{
                            // 操作失败
                            Toast.makeText(getApplicationContext(), message.getExtend().get("va_msg").toString()+"请重新登录", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "服务器返回异常，请联系管理员" , Toast.LENGTH_SHORT).show();
                    }
                });
                mQueue.add(jsonObjectRequest);
            }
        });



        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tokenHelper.deleteToken();
                Intent intent = new Intent(LoginWithTokenActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        login_again_button = findViewById(R.id.login_again_button);
        login_button = findViewById(R.id.login_button);
        tokenHelper = new TokenHelper();
        mQueue = Volley.newRequestQueue(LoginWithTokenActivity.this);
    }
}
