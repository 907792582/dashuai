package com.example.myapplication.tool;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetImage {

    // 从服务器取出图片设置UI
    public void setHeadImage(RequestQueue mQueue, final ImageButton imageButton, String url){
       ImageRequest imageRequest = new ImageRequest(
                url,
                new com.android.volley.Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        BitmapDrawable temp = new BitmapDrawable(response);
                        imageButton.setBackground(temp);
                    }
                }, 300, 300, Bitmap.Config.RGB_565, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageButton.setBackgroundResource(R.drawable.ic_head_image);
            }
        });
        mQueue.add(imageRequest);
    }

    public void setHeadImage(RequestQueue mQueue, final ImageView imageButton, String url){
        ImageRequest imageRequest = new ImageRequest(
                url,
                new com.android.volley.Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        BitmapDrawable temp = new BitmapDrawable(response);
                        imageButton.setBackground(temp);
                    }
                }, 300, 300, Bitmap.Config.RGB_565, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageButton.setBackgroundResource(R.drawable.ic_head_image);
            }
        });
        mQueue.add(imageRequest);
    }

    // 从服务器取出图片设置封面
    public void setCoverImage(RequestQueue mQueue, final ImageView imageView, String url){
        ImageRequest imageRequest = new ImageRequest(
                url,
                new com.android.volley.Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        BitmapDrawable temp = new BitmapDrawable(response);
                        imageView.setBackground(temp);
                    }
                }, 300, 400, Bitmap.Config.RGB_565, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setBackgroundResource(R.drawable.image_loading);
            }
        });
        mQueue.add(imageRequest);
    }

    // 将图片上传到服务器
    public void uploadImage(Bitmap image, final String phone, final ImageButton imageButton, final RequestQueue mQueue){

        String basePath;
        // basePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            // SD卡根目录
            basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else{
            // 系统下载缓存根目录
            basePath = Environment.getDownloadCacheDirectory().getAbsolutePath();
        }

        // 将image存入手机内存中

        try {
            File file = new File(basePath+"/BookShop/"+phone+".jpg");
            // 如果之前有头像就删掉
            if(file.exists()){
                file.delete();
            }
            if(!file.exists()){
                file.getParentFile().mkdir();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("##","头像本地存入完成");

        // 将图片上传到服务器
        File file = new File(basePath+"/BookShop/"+phone+".jpg");
        String postUrl = "http://47.100.226.176:8080/XueBaJun/ImageServlet";

        postFile(postUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    String result = response.body().string();
                    Log.e("##","文件上传返回："+result);
                }

                // 返回后设置UI显示
                setHeadImage(mQueue,imageButton,"http://47.100.226.176:8080/XueBaJun/head_image/"+phone+".jpg");
            }
        }, file);
    }

    public void upImage(Bitmap image, final String phone, final RequestQueue mQueue){
        String basePath;
        // basePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            // SD卡根目录
            basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else{
            // 系统下载缓存根目录
            basePath = Environment.getDownloadCacheDirectory().getAbsolutePath();
        }

        // 将image存入手机内存中

        try {
            File file = new File(basePath+"/BookShop/"+phone+".jpg");
            // 如果之前有头像就删掉
            if(file.exists()){
                file.delete();
            }
            if(!file.exists()){
                file.getParentFile().mkdir();
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("##","头像本地存入完成");

        // 将图片上传到服务器
        File file = new File(basePath+"/BookShop/"+phone+".jpg");
        String postUrl = "http://47.100.226.176:8080/XueBaJun/ImageServlet";

        postFile(postUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    String result = response.body().string();
                    Log.e("##","文件上传返回："+result);
                }
            }
        }, file);
    }

    public void postFile(String url, Callback callback, File...files){

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("filename",files[0].getName());
        builder.addFormDataPart("position","0");
        builder.addFormDataPart("file",files[0].getName(), RequestBody.create(MediaType.parse("application/octet-stream"),files[0]));

        final MultipartBody multipartBody = builder.build();

        Request request  = new Request.Builder().url(url).post(new MyRequestBody(multipartBody)).build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10000, TimeUnit.MILLISECONDS)
                .readTimeout(10000,TimeUnit.MILLISECONDS)
                .writeTimeout(10000,TimeUnit.MILLISECONDS).build();

        okHttpClient.newCall(request).enqueue(callback);
    }
}
