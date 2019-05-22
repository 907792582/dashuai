package com.example.myapplication.tool;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.homepage;
import com.example.myapplication.model.Msg;
import com.google.gson.Gson;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class TokenHelper {

    private String basePath;
    private String token;

    public TokenHelper() {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            // SD卡根目录
            basePath = Environment.getExternalStorageDirectory().toString();
        } else{
            // 系统下载缓存根目录
            basePath = Environment.getDownloadCacheDirectory().toString();
        }
    }

    // 检索本地是否存在token,如果存在返回token，如果不存在返回null
    public String getToken(){
        FileReader fReader = null;
        try {
            fReader = new FileReader("Demo.txt");

            char arr [] = new char[1024];
            int num = 0;
            while (num !=-1) {
                num = fReader.read(arr);
            }
            return String.valueOf(arr);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 保存token到本地
    public void saveToken(String token){

        String filePath;

        filePath = basePath + File.separator + "BookShop" + File.separator + "token.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(token.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 删除本地token
    public void deleteToken(){
        String filePath;

        filePath = basePath + File.separator + "BookShop" + File.separator + "token.txt";

        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从服务器取得token
    public String fetchToken(String username,RequestQueue mQueue){

        final int returnFlag = 0;

        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://193.112.98.224:8080/shopapp/user/returntoken";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                Log.e("##", jsonObject.toString());
                if(message.getCode() == 100){
                    token =  message.getExtend().get("token").toString();
                }else{
                    token = "fail";
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        mQueue.add(jsonObjectRequest);

        // 服务器没有返回，等待
        while (token == null){}

        if(token.compareTo("fail") ==0){
            return null;
        }else {
            return token;
        }

    }

}
