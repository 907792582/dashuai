package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.Serializable;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText student_ID_edit,student_pwd_edit;
    Button forget_pwd_button,register_button,login_button;
    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        setButtonOnClickFun();
    }

    private void init() {
        student_ID_edit = findViewById(R.id.student_ID_edit);
        student_pwd_edit = findViewById(R.id.student_pwd_edit);
        forget_pwd_button = findViewById(R.id.forget_pwd_button);
        register_button = findViewById(R.id.register_button);
        login_button = findViewById(R.id.login_button);
        mQueue  = Volley.newRequestQueue(LoginActivity.this);
    }

    private void setButtonOnClickFun() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String studentID = student_ID_edit.getText().toString();
                String pwd = student_pwd_edit.getText().toString();
                if(checkRules(studentID,pwd)){
                    // 发送给后台核对
                    if(sendToServer(studentID,pwd)){
                        // 登陆成功，跳转到主页
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //intent.putExtra("user",(Serializable) user);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(), "登陆失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        student_pwd_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                student_pwd_edit.setBackgroundResource(R.drawable.shapet);
                student_ID_edit.setBackgroundResource(R.drawable.shapet2);
                student_pwd_edit.setHint("");
                student_ID_edit.setHint("请输入账号");
            }
        });
        student_ID_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                student_ID_edit.setBackgroundResource(R.drawable.shapet);
                student_pwd_edit.setBackgroundResource(R.drawable.shapet2);
                student_ID_edit.setHint("");
                student_pwd_edit.setHint("请输入密码");
            }
        });

    }

    private Boolean checkRules(String studentID,String pwd) {
        // 查看是否为空
        if(studentID==null||pwd==null){
            Toast.makeText(getApplicationContext(), "账号密码均不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        // 查看账号格式是否为学号,xml已限制只能输入数字
        if(studentID.length()!=9){
            Toast.makeText(getApplicationContext(), "账号为9位学号", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Boolean sendToServer(String studentID, String pwd) {
        /*org.json.JSONObject jsonObject = new org.json.JSONObject();
                    try {
                        jsonObject.put("studentId", );
                        jsonObject.put("password", );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //String url = "http://47.100.226.176:8080/XueBaJun/GetMyNews";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, jsonObject, new Response.Listener<org.json.JSONObject>() {

                        public void onResponse(org.json.JSONObject jsonObject) {
                            User tempuser = new Gson().fromJson(jsonObject.toString(), User.class);
                            Log.e("##", jsonObject.toString());
                            if (tempuser != null) {
                                Log.e("##", "我的消息已返回");
                                List<News> MyNews = tempuser.getMyNews();

                                for (News n : MyNews) {
                                    news.add(n);
                                    Log.e("##", "new's content" + n.getContent()+ "Id: "+n.getId());
                                }
                                // 先执行内部类，后执行外部类
                                // setAdapter需要放在数据刷新成功之后。
                                list.setAdapter(mAdapter);//为ListView绑定Adapter
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
                    mQueue.add(jsonObjectRequest);*/
        return true;
    }
}
