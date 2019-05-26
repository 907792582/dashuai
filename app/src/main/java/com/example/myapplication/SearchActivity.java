package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.Msg;
import com.example.myapplication.model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private String[] mStrs = {"kk", "kk", "wskx", "wksx"};
    private SearchView mSearchView;
    private ListView lListView;

    User user;
    // volley
    RequestQueue mQueue;
    List<Book> bookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        //View underline =mSearchView.findViewById(R.id.search_plate);
        //underline.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
        mSearchView = (SearchView) findViewById(R.id.searchView);
        lListView = (ListView) findViewById(R.id.listView);
        lListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mStrs));
        lListView.setTextFilterEnabled(true);

        mQueue = Volley.newRequestQueue(SearchActivity.this);

        user = (User) getIntent().getSerializableExtra("user");

        bookList = new ArrayList<>();

        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {

                bookList.clear();

                if(query == null){
                    searchAll();
                }else{
                    search(query);
                }


                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    lListView.setFilterText(newText);
                }else{
                    lListView.clearTextFilter();
                }
                return false;
            }
        });

    }

    private void search(String searchContent){

        String url = "http://193.112.98.224:8080/shopapp/book/findbook/"+searchContent;
        Log.e("##", "搜索书籍url:"+url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);
                if(message.getCode() == 100){

                    Book temp = new Book();
                    temp.setBookid((String) message.getExtend().get("bookId"));
                    temp.setBookname((String) message.getExtend().get("bookname"));
                    temp.setBookfrom((String) message.getExtend().get("bookForm"));
                    temp.setBookimage((String) message.getExtend().get("bookImage"));
                    temp.setBookintroduction((String) message.getExtend().get("bookIntro"));
                    temp.setBookprice((Double) message.getExtend().get("bookprice"));
                    temp.setBookstock("50");

                    bookList.add(temp);

                    setJumpFun();

                }else{
                    // 操作失败
                    Toast.makeText(getApplicationContext(), "找不到相关书籍！换个关键词试试吧~" , Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "服务器返回出错，请联系管理员" , Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void searchAll(){
        String url = "http://193.112.98.224:8080/shopapp/book/getAll";
        Log.e("##", "搜索书籍url:"+url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, new Response.Listener<org.json.JSONObject>() {

            public void onResponse(org.json.JSONObject jsonObject) {
                 Msg message = new Gson().fromJson(jsonObject.toString(), Msg.class);

                 if(message.getCode() == 100){
                     try {

                         JSONArray ob = jsonObject.getJSONObject("extend").getJSONArray("booklist");

                         // List<Book> bookList = new ArrayList<>();
                         for(int i = 0;i<ob.length();i++){
                             JSONObject temp = ob.getJSONObject(i);
                             bookList.add(new Gson().fromJson(temp.toString(), Book.class));
                             Log.e("##", "搜索获得书籍："+temp.toString());
                         }

                     } catch (JSONException e) {
                         e.printStackTrace();
                     }

                     setJumpFun();
                 }else {
                     Log.e("##", "搜索书籍出错");
                     Toast.makeText(getApplicationContext(), "搜索书籍出错，请联系管理员" , Toast.LENGTH_SHORT).show();
                 }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "服务器返回出错，请联系管理员" , Toast.LENGTH_SHORT).show();
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void setJumpFun() {
        Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
        Msg message = new Msg();
        message.getExtend().put("bookList",bookList);
        intent.putExtra("bookList", message);
        intent.putExtra("user",user);
        startActivity(intent);
    }
}
