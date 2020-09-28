package com.cu.englishtomyanmardictionary.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.englishtomyanmardictionary.Adapter.SearchListAdapter;
import com.cu.englishtomyanmardictionary.Database.DatabaseHelper;
import com.cu.englishtomyanmardictionary.Model.Data;
import com.cu.englishtomyanmardictionary.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    ListView listView;
    SearchListAdapter adapter;
    ArrayList<Data> dataList = new ArrayList<>();
    ArrayList<Data> recentList = new ArrayList<>();
    SearchView searchView;
    ImageView speak,convert;
    private final int REQ_CODE = 100;
    TextView lang1,lang2;
    LinearLayout recent_layout;
    ListView listviewRecent;
    TextView clear_all;
    int c=0;
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recent_layout=view.findViewById(R.id.recent_layout);
        listviewRecent=view.findViewById(R.id.listviewRecent);
        //
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle(getResources().getString(R.string.app_name)); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        final Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                try {
                    c++;
                    if (c == 2) {
                        dataLoading();
                    }
                    handler.postDelayed(this,500);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        handler.post(runnable);

        listviewRecent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recent();
            }
        });
        listView = view.findViewById(R.id.listView);
        Recent();
        clear_all=view.findViewById(R.id.clear_all);
        clear_all.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                DatabaseHelper helper=new DatabaseHelper(getContext());
                if(getVerify()){
                    boolean res=helper.deleteRecentAll("eng");
                    if(res){
                        @SuppressLint("InflateParams") View view=LayoutInflater.from(getContext()).inflate(R.layout.clear_layout,null);
                        TextView textView=view.findViewById(R.id.clear);
                        textView.setText("Clear All");
                        Toast toast=Toast.makeText(getContext(),"Clear",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM,0,100);
                        toast.setView(view);
                        toast.show();
                    }
                }else {
                    boolean res=helper.deleteRecentAll("myan");
                    if(res){
                        @SuppressLint("InflateParams") View view=LayoutInflater.from(getContext()).inflate(R.layout.clear_layout,null);
                        TextView textView=view.findViewById(R.id.clear);
                        textView.setText("Clear All");
                        Toast toast=Toast.makeText(getContext(),"Clear",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM,0,100);
                        toast.setView(view);
                        toast.show();
                    }
                }
                Recent();
            }
        });
        listviewRecent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Recent();
                return true;
            }
        });

        searchView = view.findViewById(R.id.search);
        convert=view.findViewById(R.id.convertImage);
        lang1=view.findViewById(R.id.lang1);
        lang2=view.findViewById(R.id.lang2);
        refresh();
        convert.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(getVerify()){
                    Animation forward =
                            AnimationUtils.loadAnimation(getContext(),
                                    R.anim.convert_animation);
                    convert.startAnimation(forward);
                    verifyLanguage(false);
                    lang1.setText("Myanmar");
                    lang2.setText("English");

                }else {
                    Animation backward =
                            AnimationUtils.loadAnimation(getContext(),
                                    R.anim.back_animation);
                    convert.startAnimation(backward);
                    verifyLanguage(true);
                    lang1.setText("English");
                    lang2.setText("Myanmar");
                }
                searchView.setQuery("",false);
                Recent();
            }
        });
        speak = view.findViewById(R.id.speak);
        speak.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                promptSpeechInput();
                return true;
            }

        });
        try {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (TextUtils.isEmpty(s)) {
                        adapter.filterSubmit("");
                        listView.setAdapter(null);
                        recent_layout.setVisibility(View.VISIBLE);
                        Recent();

                    } else {
                        try {
                            recent_layout.setVisibility(View.GONE);
                            adapter.filterSubmit(s);
                            if (adapter != null) {
                                listView.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    if (TextUtils.isEmpty(s)) {
                        adapter.filter("");
                        listView.setAdapter(null);
                        recent_layout.setVisibility(View.VISIBLE);
                        Recent();
                    } else {
                        try {
                            recent_layout.setVisibility(View.GONE);
                            adapter.filter(s);
                            if (adapter != null) {
                                listView.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
            });
        }catch (Exception e){
            Toast.makeText(getContext(),
                    e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void Recent(){
        try{
            recentList = new ArrayList<>();
            if(recentList.size()>0){
                recentList.clear();
            }
            DatabaseHelper helper=new DatabaseHelper(getContext());
            Cursor cursor = helper.getRecent();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if(getVerify()){
                        if(cursor.getString(4).equals("eng")){
                            recentList.add(new Data(cursor.getString(0) + "", cursor.getString(1) + "", cursor.getString(2) + "", cursor.getString(3) + "", "1", "1"));
                        }
                    }else {
                        if(cursor.getString(4).equals("myan")){
                            recentList.add(new Data(cursor.getString(0) + "", cursor.getString(3) + "", cursor.getString(2) + "", cursor.getString(1) + "", "1", "1"));
                        }
                    }
                }
                recent_layout.setVisibility(View.VISIBLE);
            }else {
                recent_layout.setVisibility(View.GONE);
            }
            listviewRecent.setAdapter(new SearchListAdapter(getContext(),recentList,0));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    public void refresh(){
        if(getVerify()){
            lang1.setText("English");
            lang2.setText("Myanmar");
        }else {
            lang2.setText("English");
            lang1.setText("Myanmar");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void verifyLanguage(boolean change) {
        SharedPreferences sharedPreferences= requireActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("Language",change).apply();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean getVerify(){
        SharedPreferences sharedPreferences= requireActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Language",true);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE) {
            if (resultCode == getActivity().RESULT_OK && null != data) {

                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                assert result != null;
                searchView.setQuery(result.get(0) + "", false);
                Toast.makeText(getContext(),
                        result.get(0),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void dataLoading() {
        try {
            DatabaseHelper helper = new DatabaseHelper(getContext());
            try {
                helper.createDatabase();
            } catch (IOException e) {
                throw new Error("Unable to create database");
            }
            helper.openDatabase();

            dataList = new ArrayList<>();
            Cursor cursor1 = helper.query("dblist");
            if (cursor1 != null && cursor1.getCount() > 0) {
                while (cursor1.moveToNext()) {
                    dataList.add(new Data(cursor1.getString(0) + "", cursor1.getString(1) + "", cursor1.getString(2) + "", cursor1.getString(3) + "", cursor1.getString(4) + "", cursor1.getString(5) + ""));
                }
            }
            Cursor cursor2 = helper.query("mydblist");
            if (cursor2 != null && cursor2.getCount() > 0) {
                while (cursor2.moveToNext()) {
                    dataList.add(new Data(cursor2.getString(0) + "", cursor2.getString(1) + "", cursor2.getString(2) + "", cursor2.getString(3) + "", cursor2.getString(4) + "", cursor2.getString(5) + ""));
                }
            }
            Cursor cursor3 = helper.query("myen");
            if (cursor3 != null && cursor3.getCount() > 0) {
                while (cursor3.moveToNext()) {
                    dataList.add(new Data(cursor3.getString(0) + "", cursor3.getString(3) + "", cursor3.getString(2) + "", cursor3.getString(1) + "", cursor3.getString(4) + "", ""));
                }
            }
            helper.close();
            adapter = new SearchListAdapter(getContext(), dataList,1);
            assert cursor1 != null;
            assert cursor2 != null;
            assert cursor3 != null;
            int total_db=cursor1.getCount()+cursor2.getCount()+cursor3.getCount();
            int total_list=dataList.size();
           if(total_db==total_list){
               progressDialog.dismiss();
           }
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}