package com.pluspark.playground_thread;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class MyHandler extends Handler {
    private TextView textView;

    public MyHandler(TextView textView){
        this.textView = textView;
    }
    @Override
    public void handleMessage(@NonNull Message msg){
        super.handleMessage(msg);

        Bundle bundle = msg.getData();
        int value = bundle.getInt("value");
        textView.setText("현재 값" + value);
    }
}
