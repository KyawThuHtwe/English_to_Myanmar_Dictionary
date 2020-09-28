package com.cu.englishtomyanmardictionary.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.cu.englishtomyanmardictionary.Database.DatabaseHelper;
import com.cu.englishtomyanmardictionary.R;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

public class DataBaseFragment extends Fragment {


    public DataBaseFragment() {
        // Required empty public constructor
    }

    CoordinatorLayout default_database;
    ImageView analyze;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_data_base, container, false);
        default_database=view.findViewById(R.id.default_database);
        analyze=view.findViewById(R.id.analyze);
        default_database.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onLongClick(View v) {
                Animation backward = AnimationUtils.loadAnimation(getContext(),R.anim.retry_animation);
                analyze.startAnimation(backward);
                try {
                    DatabaseHelper helper = new DatabaseHelper(getContext());
                    helper.deleteRecentAll("eng");
                    helper.deleteRecentAll("myan");
                    helper.deleteParseAll("eng");
                    helper.deleteParseAll("myan");
                    setDefault_database();
                    @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.default_layout, null);
                    Toast toast = Toast.makeText(getContext(), "default", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 100);
                    toast.setView(view);
                    toast.show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            }
        });

        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setDefault_database(){
        SharedPreferences sharedPreferences= requireActivity().getSharedPreferences("Database", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("Database",false).apply();
    }

}