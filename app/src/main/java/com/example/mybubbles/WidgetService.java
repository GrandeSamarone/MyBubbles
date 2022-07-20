package com.example.mybubbles;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WidgetService  extends Service {

    int LAYOUT_FLAG;
    View mFloatingview;
    WindowManager windowManager;
    ImageView imageViewClose;
    int MAX_CLICK_DURATION=200;
    TextView txtWidget;
    float height,width;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
         LAYOUT_FLAG= WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG= WindowManager.LayoutParams.TYPE_PHONE;
        }

           ///INFLA
        mFloatingview= LayoutInflater.from(this).inflate(R.layout.layout_widget,null);

        WindowManager.LayoutParams layoutParams= new
                WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        ///Position inicial
        layoutParams.gravity= Gravity.TOP|Gravity.RIGHT;
        layoutParams.x=0;
        layoutParams.y=100;



        ///Parametro do layout close button
        WindowManager.LayoutParams imageparams= new WindowManager.LayoutParams(
                140,
                140,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

           imageparams.gravity=Gravity.BOTTOM|Gravity.CENTER;
           imageparams.y=100;

        windowManager=(WindowManager)getSystemService(WINDOW_SERVICE);

        imageViewClose=new ImageView(this);
        imageViewClose.setImageResource(R.drawable.close);

        imageViewClose.setVisibility(View.INVISIBLE);
        windowManager.addView(imageViewClose,imageparams);
        windowManager.addView(mFloatingview,layoutParams);
        mFloatingview.setVisibility(View.VISIBLE);

        height=windowManager.getDefaultDisplay().getHeight();
        width =windowManager.getDefaultDisplay().getWidth();

        txtWidget=(TextView) mFloatingview.findViewById(R.id.text_widget);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               txtWidget.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
               handler.postDelayed(this,1000);
            }
        },10);

        //movimentar
        txtWidget.setOnTouchListener(new View.OnTouchListener() {

            int initialX,initialY;
            float initialTouchX,initialTouchY;
             long startclickTime;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){

                    case  MotionEvent.ACTION_DOWN:
                        startclickTime= Calendar.getInstance().getTimeInMillis();
                        imageViewClose.setVisibility(View.VISIBLE);

                        initialX=layoutParams.x;
                        initialY=layoutParams.y;
                        initialTouchX=motionEvent.getRawX();
                        initialTouchY=motionEvent.getRawY();

                        return true;

                    case MotionEvent.ACTION_UP:
                    long clickDuration=Calendar.getInstance().getTimeInMillis()-startclickTime;
                    imageViewClose.setVisibility(View.GONE);
                    layoutParams.x=initialX+(int)(initialTouchX-motionEvent.getRawX());
                    layoutParams.y=initialY+(int)(motionEvent.getRawY()-initialTouchY);

                    if(clickDuration<MAX_CLICK_DURATION){
                        Toast.makeText(WidgetService.this, "Time"+txtWidget.getText().toString(), Toast.LENGTH_SHORT).show();
                    }else{
                         if(layoutParams.y>(height*0.6)){
                             stopSelf();
                         }
                    }
                        return true;

                    case MotionEvent.ACTION_MOVE:

                        //calcular cordenadas da view
                   layoutParams.x=initialX+(int)(initialTouchX-motionEvent.getRawX());
                   layoutParams.y=initialY+(int)(motionEvent.getRawY()-initialTouchY);

                   //Atualizar layout
                   windowManager.updateViewLayout(mFloatingview,layoutParams);

                   if(layoutParams.y>(height*0.6)){
                       imageViewClose.setImageResource(R.drawable.close);
                   }else{
                       imageViewClose.setImageResource(R.drawable.close_white);
                   }
                   return true;

                }


                return false;
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if(mFloatingview!=null){
            windowManager.removeView(mFloatingview);
        }
        if(imageViewClose!=null){
            windowManager.removeView(imageViewClose);
        }


        super.onDestroy();
    }
}
