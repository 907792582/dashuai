package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class BottomBar extends AppCompatActivity {

    TextView book_image_button,cart_image_button,user_image_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottombar);

        init();
        setOnClickFun();
    }

    private void setOnClickFun() {
        book_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BottomBar.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

        cart_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BottomBar.this, ShoppingCartActivity.class);
                startActivity(intent);
            }
        });

        user_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BottomBar.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        book_image_button = findViewById(R.id.book_image_button);
        cart_image_button = findViewById(R.id.cart_image_button);
        user_image_button = findViewById(R.id.user_image_button);
    }
}
