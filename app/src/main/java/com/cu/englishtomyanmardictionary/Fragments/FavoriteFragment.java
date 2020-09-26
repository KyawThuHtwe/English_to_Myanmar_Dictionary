package com.cu.englishtomyanmardictionary.Fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.englishtomyanmardictionary.Adapter.FavoriteAdapter;
import com.cu.englishtomyanmardictionary.Database.DatabaseHelper;
import com.cu.englishtomyanmardictionary.Model.Data;
import com.cu.englishtomyanmardictionary.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class FavoriteFragment extends Fragment {

    public FavoriteFragment() {
        // Required empty public constructor
    }

    ListView listView_eng,listView_myan;
    ArrayList<Data> arrayList_eng=new ArrayList<>();
    FavoriteAdapter adapter_eng;
    ArrayList<Data> arrayList_myan=new ArrayList<>();
    FavoriteAdapter adapter_myan;
    LinearLayout eng,myan;
    TextView clear_all_eng,clear_all_myan;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_favorite, container, false);
        listView_eng=view.findViewById(R.id.listView_eng);
        listView_myan=view.findViewById(R.id.listView_myan);
        eng=view.findViewById(R.id.eng);
        myan=view.findViewById(R.id.myan);
        clear_all_eng=view.findViewById(R.id.clear_all_eng);
        clear_all_myan=view.findViewById(R.id.clear_all_myan);
        delete(clear_all_eng);
        delete(clear_all_myan);
        dataLoading();
        listView_eng.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dataLoading();
            }
        });
        listView_myan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dataLoading();
            }
        });
        return view;
    }
    public void delete(final TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (textView.getId()){
                    case R.id.clear_all_eng:
                        deleteTable("eng");
                        break;
                    case R.id.clear_all_myan:
                        deleteTable("myan");
                        break;
                }
            }
        });

    }
    public void deleteTable(String s){
        try {
            DatabaseHelper helper = new DatabaseHelper(getContext());
            boolean res = helper.deleteParseAll(s);
            if (res) {
                Toast.makeText(getContext(), "Clear", Toast.LENGTH_SHORT).show();
                dataLoading();
            } else {
                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public void dataLoading(){
        try{
            arrayList_eng = new ArrayList<>();
            if(arrayList_eng.size()>0){
                arrayList_eng.clear();
            }
            arrayList_myan = new ArrayList<>();
            if(arrayList_myan.size()>0){
                arrayList_myan.clear();
            }
            DatabaseHelper helper=new DatabaseHelper(getContext());
            Cursor cursor = helper.getParse();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    if(cursor.getString(4).equals("eng")){
                        arrayList_eng.add(new Data(cursor.getString(0) + "", cursor.getString(1) + "", cursor.getString(2) + "", cursor.getString(3) + "", "1", "1"));
                    }else if(cursor.getString(4).equals("myan")){
                        arrayList_myan.add(new Data(cursor.getString(0) + "", cursor.getString(1) + "", cursor.getString(2) + "", cursor.getString(3) + "", "1", "1"));
                    }
                }
            }
            if(arrayList_eng.size()==0){
                eng.setVisibility(View.GONE);
            }
            if(arrayList_myan.size()==0){
                myan.setVisibility(View.GONE);
            }
            adapter_eng=new FavoriteAdapter(getContext(),arrayList_eng,1);
            adapter_myan= new FavoriteAdapter(getContext(),arrayList_myan,0);
            listView_eng.setAdapter(adapter_eng);
            listView_myan.setAdapter(adapter_myan);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}