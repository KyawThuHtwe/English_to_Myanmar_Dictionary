package com.cu.englishtomyanmardictionary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cu.englishtomyanmardictionary.Font.Rabbit;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final DrawerLayout drawerLayout=findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        drawerLayout.close();
        NavigationView navigationView=findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        NavController navController= Navigation.findNavController(this,R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView,navController);
        final TextView textTitle=findViewById(R.id.textTitle);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                textTitle.setText(destination.getLabel());
            }
        });
        View view=navigationView.getHeaderView(0);
        RadioGroup radioGroup=view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.zawgyi:
                        Font(false);
                        break;
                    case R.id.unicode:
                        Font(true);
                        break;
                }
                refresh();
            }
        });
        refresh();
        if(isOnce()){
            FontChoose();
            Once(false);
        }
    }
    public void refresh(){
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                NavigationView navigationView = findViewById(R.id.navigationView);
                View view=navigationView.getHeaderView(0);
                RadioButton zawgyi=view.findViewById(R.id.zawgyi);
                RadioButton unicode=view.findViewById(R.id.unicode);

                if(isFont()){
                    unicode.setChecked(true);
                }else {
                    zawgyi.setChecked(true);
                }
            }
        });
    }
    Boolean result=false;
    public void FontChoose(){
        View view=getLayoutInflater().inflate(R.layout.font_layout,null);
        final TextView title=view.findViewById(R.id.title);
        RadioGroup group=view.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.zawgyi:
                        result=false;
                        title.setText(getResources().getText(R.string.app_name));
                        break;
                    case R.id.unicode:
                        result=true;
                        title.setText(Rabbit.zg2uni(title.getText().toString()));
                        break;
                }
            }
        });
        Button ok=view.findViewById(R.id.ok);
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setView(view);
        final android.app.AlertDialog ad=builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Font(result);
                ad.dismiss();
                refresh();
            }
        });
        ad.show();
    }
    public void Font(Boolean result){
        SharedPreferences sharedPreferences=getSharedPreferences("Font", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("Convert",result).apply();
    }

    public Boolean isFont(){
        SharedPreferences sharedPreferences=getSharedPreferences("Font", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Convert",false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}