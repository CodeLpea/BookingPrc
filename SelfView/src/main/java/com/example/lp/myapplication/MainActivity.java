package com.example.lp.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.lp.myapplication.View.ClockView.ClockActivity;

/**
 * 自定义View
 * lp
 * 2019/05/05
 * */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void startActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }
/**
 * 开启时钟界面
 * */
    public void StartClokActivity(View view) {
        startActivity(ClockActivity.class);
    }
}
