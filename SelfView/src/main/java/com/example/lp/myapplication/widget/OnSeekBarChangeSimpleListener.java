package com.example.lp.myapplication.widget;

import android.widget.SeekBar;


public abstract class OnSeekBarChangeSimpleListener implements SeekBar.OnSeekBarChangeListener {

    @Override
    public abstract void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
