package com.cu.englishtomyanmardictionary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.englishtomyanmardictionary.Font.Rabbit;

import org.jetbrains.annotations.NotNull;

public class FontActivity extends AppCompatActivity {
    public static int REQUEST_PERMISSION_READ=1;
    public static int REQUEST_PERMISSION_WRITE=2;
    boolean read=false;
    boolean write=false;
    LinearLayout retry;
    ImageView rotate;
    TextView alert;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font);
        retry=findViewById(R.id.retry);
        rotate=findViewById(R.id.rotate);
        alert=findViewById(R.id.alert);
        permission1();
        permission2();
        refresh();
        final Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                try {
                    if(!read || !write){
                        refresh();
                    }
                    handler.postDelayed(this,500);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        handler.post(runnable);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.retry_animation);
                rotate.startAnimation(backward);
                permission1();
                permission2();
                refresh();
            }
        });
    }
    @SuppressLint("SetTextI18n")
    public void refresh(){
        if(!read || !write){
            retry.setVisibility(View.VISIBLE);
            alert.setVisibility(View.VISIBLE);
            alert.setText("Please allow the permission");
        }else {
            retry.setVisibility(View.GONE);
            alert.setText(getResources().getString(R.string.version_1_0_0));
        }
        if(read && write){
            if(isOnce()){
                FontChoose();
                Once(false);
            }else{
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }
        }
        Log.e("read", String.valueOf(read));
        Log.e("write", String.valueOf(write));
    }
    public void permission1() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ);
            }
        }else{
            read=true;
        }
    }
    public void permission2() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
            }
        }else{
            write=true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_PERMISSION_READ){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                read=true;
                Toast.makeText(this,"Allow the permission",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Please allow the permission",Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode==REQUEST_PERMISSION_WRITE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                write=true;
                Toast.makeText(this,"Allow the permission",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Please allow the permission",Toast.LENGTH_SHORT).show();
            }
        }
    }
    Boolean result=false;
    public void FontChoose(){
        View view=getLayoutInflater().inflate(R.layout.font_layout,null);
        final TextView title=view.findViewById(R.id.title);
        RadioGroup group=view.findViewById(R.id.radioGroup);
        RadioButton zawgyi=view.findViewById(R.id.zawgyi);
        RadioButton unicode=view.findViewById(R.id.unicode);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.zawgyi:
                        result=false;
                        title.setText(getResources().getText(R.string.thank));
                        break;
                    case R.id.unicode:
                        result=true;
                        title.setText(Rabbit.zg2uni((String) getResources().getText(R.string.thank)));
                        break;
                }
            }
        });
        if(isFont()){
            unicode.setChecked(true);
        }else {
            zawgyi.setChecked(true);
        }
        Button ok=view.findViewById(R.id.ok);
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setView(view);
        final android.app.AlertDialog ad=builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Font(result);
                ad.dismiss();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();

            }
        });
        ad.show();
    }
    public Boolean isFont(){
        SharedPreferences sharedPreferences=getSharedPreferences("Font", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Convert",false);
    }
    public void Font(Boolean result){
        SharedPreferences sharedPreferences=getSharedPreferences("Font", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("Convert",result).apply();
    }

    public void Once(Boolean result){
        SharedPreferences sharedPreferences=getSharedPreferences("FontChoose", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("Once",result).apply();
    }

    public Boolean isOnce(){
        SharedPreferences sharedPreferences=getSharedPreferences("FontChoose", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Once",true);
    }
}