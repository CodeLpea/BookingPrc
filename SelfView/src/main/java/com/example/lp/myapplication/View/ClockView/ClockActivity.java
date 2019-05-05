package com.example.lp.myapplication.View.ClockView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;

import com.example.lp.myapplication.R;
import com.example.lp.myapplication.widget.OnSeekBarChangeSimpleListener;

import java.util.Random;

public class ClockActivity extends AppCompatActivity {
    private ClockView clockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        initView();

        ListenSeekBar();
    }
    private void initView() {
        clockView=findViewById(R.id.clock_view);//绑定控件
    }

    private void ListenSeekBar() {
        AppCompatSeekBar seekBar1=findViewById(R.id.seekBar1);
        AppCompatSeekBar seekBar2=findViewById(R.id.seekBar2);
        AppCompatSeekBar seekBar3=findViewById(R.id.seekBar3);

        seekBar1.setMax(100);
        seekBar2.setMax(100);
        seekBar3.setMax(100);

        /*表盘中心点颜色*/
        seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeSimpleListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Random myRandom = new Random();
                int ranColor = 0xff000000 | myRandom.nextInt(0x00ffffff);
                clockView.setAroundColor(ranColor);
            }
        });
        /*表盘边缘线的宽度*/
        seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeSimpleListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                clockView.setAroundStockWidth(progress);
            }
        });

        /*设置时钟字体的大小*/
        seekBar3.setOnSeekBarChangeListener(new OnSeekBarChangeSimpleListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                clockView.setClockTextSize(progress);
            }
        });


    }


}
