package com.example.mybubbles;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    androidx.appcompat.widget.AppCompatButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    button=findViewById(R.id.BtnAbrir);

    getPermission();

    button.setOnClickListener(new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
        if(!Settings.canDrawOverlays(MainActivity.this)){
           getPermission();
        }else{
        Intent intent=new Intent(MainActivity.this,WidgetService.class);
        startService(intent);
        finish();
        }
        }
    });
    }

    
    @Override
    protected void onResume() {
       // Intent intent=new Intent(MainActivity.this,WidgetService.class);
       // startService(intent);
        super.onResume();
    }

    public void getPermission(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)){

            Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:"+getPackageName()));
           startActivityForResult(intent,1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1){
        if(!Settings.canDrawOverlays(MainActivity.this)){
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

        }
    }
}