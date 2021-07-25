package com.lge.code;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    CameraSource cameraSource;
    SurfaceView cameraSurface;
    Button button,newbutton;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newbutton = (Button) findViewById(R.id.menu);
        newbutton.setOnClickListener(this);
        button =(Button)findViewById(R.id.ResultText);
        button.setOnClickListener(this);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
                Toast.makeText(getApplicationContext(),"권한 허용 필요함",Toast.LENGTH_SHORT).show();
            }else{
                requestPermissions(new String[]{Manifest.permission.CAMERA},1);
            }
        }
        cameraSurface = (SurfaceView) findViewById(R.id.cameraSurface);

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        Log.d("NowStatus", "BarcodeDetector Build Complete");
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .setRequestedPreviewSize(size.y, size.x)
                .setAutoFocusEnabled(true)
                .build();
        Log.d("NowStatus", "CameraSource Build Complete");

        // Callback을 이용해서 SurfaceView를 실시간으로 Mobile Vision API와 연결
        cameraSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {   // try-catch 문은 Camera 권한획득을 위한 권장사항
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(cameraSurface.getHolder());  // Mobile Vision API 시작
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();    // SurfaceView가 종료되었을 때, Mobile Vision API 종료
                Log.d("NowStatus", "SurfaceView Destroyed and CameraSource Stopped");
            }
        });

        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                button.setText(msg.obj.toString());
                return false;
            }
        });
        barcodeProcesser bp = new barcodeProcesser(handler);
        barcodeDetector.setProcessor(bp);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ResultText:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) button.getText()));
                    startActivity(intent);
                }catch(Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.menu:
                Intent intent = new Intent(this,create.class);
                startActivity(intent);
        }
    }
    class barcodeProcesser implements Detector.Processor<Barcode> {
        Handler mHandler;
        barcodeProcesser(Handler handler){
            mHandler=handler;
        }
        @Override
        public void release() {
            Log.d("NowStatus", "BarcodeDetector SetProcessor Released");
        }

        @Override
        public void receiveDetections(Detector.Detections<Barcode> detections) {
            final SparseArray<Barcode> barcodes = detections.getDetectedItems();
            if(barcodes.size() != 0) {
                String barcodeContents = barcodes.valueAt(0).displayValue;
                Message msg = new Message();
                msg.obj=barcodeContents;
                mHandler.sendMessage(msg);
            }
        }
    }
}