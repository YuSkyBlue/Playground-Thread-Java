package com.pluspark.playground_thread;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    MyHandler myHandler;
    int value = 0;
    BackgroundThread backgroundThread;
    ReentrantLock lock = new ReentrantLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        myHandler = new MyHandler(textView);

        Button button1 = findViewById(R.id.threadStart);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isThreadRunning()) {
                    startBackgroundThread();
                } else {
                    Log.d("MainActivity", "Thread is already running");
                }
            }
        });

        Button button2 = findViewById(R.id.valueCheck);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isThreadRunning()) {
                    stopBackgroundThread();
                } else {
                    Log.d("MainActivity", "Thread is not running");
                }
            }
        });
    }
    class BackgroundThread extends Thread {

        volatile boolean running = true;
        volatile boolean interrupt = false;

        public void run() {
            while (running && !interrupt) {
                value += 1;

                // Use post or runOnUiThread to update UI from the main thread
                textView.post(new Runnable() {
                    @Override
                    public void run() {
                        Message message = myHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putInt("value", value);
                        message.setData(bundle);
                        myHandler.sendMessage(message);
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                    interrupt = true;
                    break;
                }
            }
        }
    }
    private boolean isThreadRunning() {
        return backgroundThread != null && backgroundThread.isAlive();
    }
    private void startBackgroundThread() {
        lock.lock();
        try {
            backgroundThread = new BackgroundThread();
            backgroundThread.start();
        } finally {
            lock.unlock();
        }
    }
    private void stopBackgroundThread() {
        lock.lock();
        try {
            if (isThreadRunning()) {
                backgroundThread.running = false;
                backgroundThread.interrupt();
                backgroundThread.interrupt = true;
            }
        } finally {
            lock.unlock();
        }
    }

}