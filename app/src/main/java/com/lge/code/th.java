package com.lge.code;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class th extends Thread{
    private Handler mHandler;
    private String str;
    th(Handler handler,String st){
        mHandler=handler;
        str=st;
    }

    @Override
    public void run() {
        super.run();
        try {
            URL url = new URL("https://chart.googleapis.com/chart?cht=qr&chs=512x512&chl=" + str);

            URLConnection connection = url.openConnection();
            HttpURLConnection HCon = (HttpURLConnection) connection;
            int ResCode = HCon.getResponseCode();


            if (ResCode == HttpURLConnection.HTTP_OK) {

                InputStream ins = ((URLConnection) HCon).getInputStream();
                Bitmap result = BitmapFactory.decodeStream(ins);

                Message msg = new Message();
                msg.obj=result;
                mHandler.sendMessage(msg);

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}