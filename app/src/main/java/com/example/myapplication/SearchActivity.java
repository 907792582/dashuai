package com.example.myapplication;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;


public class SearchActivity extends AppCompatActivity {

    private String[] mStrs = {"kk", "kk", "wskx", "wksx"};
    private SearchView mSearchView;
    private ListView lListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        View underline =mSearchView.findViewById(R.id.search_plate);
        underline.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
        mSearchView = (SearchView) findViewById(R.id.searchView);
        lListView = (ListView) findViewById(R.id.listView);
        lListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mStrs));
        lListView.setTextFilterEnabled(true);

        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
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
}
