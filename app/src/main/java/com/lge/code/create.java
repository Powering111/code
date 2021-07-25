package com.lge.code;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class create extends AppCompatActivity implements View.OnClickListener {
    private ImageView img;
    private EditText editText;
    private Button button;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        button = findViewById(R.id.genBtn);
        editText = findViewById(R.id.input);
        img = findViewById(R.id.generatedImg);
        button.setOnClickListener(this);
        handler=new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                img.setImageBitmap((Bitmap)msg.obj);
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.genBtn:
                th t = new th(handler, editText.getText().toString());
                t.start();
                break;
        }

    }
}
