package com.cu.englishtomyanmardictionary.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cu.englishtomyanmardictionary.Database.DatabaseHelper;
import com.cu.englishtomyanmardictionary.Font.Rabbit;
import com.cu.englishtomyanmardictionary.Model.Data;
import com.cu.englishtomyanmardictionary.R;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;

public class SearchListAdapter extends ArrayAdapter<Data> {
    Context context;
    ViewHolder viewHolder;
    ArrayList<Data> dataArrayList;
    ArrayList<Data> arrayList;
    TextToSpeech textToSpeech;
    int res;
    public SearchListAdapter(Context context, ArrayList<Data> dataArrayList,int res) {
        super(context, R.layout.search_item,dataArrayList);
        this.context = context;
        this.dataArrayList = dataArrayList;
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(dataArrayList);
        this.res=res;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return dataArrayList.size();
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NotNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.search_item,parent,false);
            viewHolder= new ViewHolder();
            viewHolder.word=convertView.findViewById(R.id.work);
            viewHolder.def=convertView.findViewById(R.id.def);
            viewHolder.speak1=convertView.findViewById(R.id.speak1);
            viewHolder.speak2=convertView.findViewById(R.id.speak2);
            viewHolder.add1=convertView.findViewById(R.id.add1);
            viewHolder.add2=convertView.findViewById(R.id.add2);
            viewHolder.layout=convertView.findViewById(R.id.layout);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        final String id=dataArrayList.get(position).getId()+"";
        if(res==0){
            viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        DatabaseHelper helper = new DatabaseHelper(context);
                        int res = helper.deleteRecent(id);
                        if (res == 1) {
                            View view=LayoutInflater.from(context).inflate(R.layout.clear_layout,null);
                            Toast toast=Toast.makeText(context,"Clear",Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM,0,100);
                            toast.setView(view);
                            toast.show();
                        } else {
                            Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                        }
                        notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

        }

        if(getVerify()) {
            viewHolder.word.setText(dataArrayList.get(position).getWord()+"  (" + dataArrayList.get(position).getState() + ")");
            ///font
            if(isFont()){
                viewHolder.def.setText(Rabbit.zg2uni(dataArrayList.get(position).getDef()));
            }else {
                viewHolder.def.setText(dataArrayList.get(position).getDef());
            }
            textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.UK);
                    }
                }
            });
            viewHolder.speak1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view=LayoutInflater.from(context).inflate(R.layout.speak_layout,null);
                    Toast toast=Toast.makeText(context,"speak",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,100);
                    toast.setView(view);
                    toast.show();
                    textToSpeech.speak(dataArrayList.get(position).getWord(), TextToSpeech.QUEUE_FLUSH, null);
                    if(res==1) {
                        insertRecent(dataArrayList.get(position).getWord() + "", dataArrayList.get(position).getState() + "", dataArrayList.get(position).getDef() + "","eng");
                    }

                }
            });
            viewHolder.speak1.setVisibility(View.VISIBLE);
            viewHolder.speak2.setVisibility(View.GONE);
            viewHolder.add1.setVisibility(View.GONE);
            viewHolder.add2.setVisibility(View.VISIBLE);
        }else {
            //font
            if(isFont()){
                viewHolder.word.setText(Rabbit.zg2uni(dataArrayList.get(position).getDef()));
            }else {
                viewHolder.word.setText(dataArrayList.get(position).getDef());
            }
            viewHolder.def.setText(dataArrayList.get(position).getWord()+"  (" + dataArrayList.get(position).getState() + ")");
            textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(Locale.UK);
                    }
                }
            });
            viewHolder.speak2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view=LayoutInflater.from(context).inflate(R.layout.speak_layout,null);
                    Toast toast=Toast.makeText(context,"speak",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM,0,100);
                    toast.setView(view);
                    toast.show();
                    textToSpeech.speak(dataArrayList.get(position).getWord(), TextToSpeech.QUEUE_FLUSH, null);
                    if (res==1) {
                        insertRecent(dataArrayList.get(position).getDef() + "", dataArrayList.get(position).getState() + "", dataArrayList.get(position).getWord() + "","myan");

                    }
                }
            });
            viewHolder.speak1.setVisibility(View.GONE);
            viewHolder.speak2.setVisibility(View.VISIBLE);
            viewHolder.add1.setVisibility(View.VISIBLE);
            viewHolder.add2.setVisibility(View.GONE);
        }
        viewHolder.add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertParse(dataArrayList.get(position).getWord()+"",dataArrayList.get(position).getState()+"",dataArrayList.get(position).getDef()+"","myan");
            }
        });
        viewHolder.add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertParse(dataArrayList.get(position).getWord()+"",dataArrayList.get(position).getState()+"",dataArrayList.get(position).getDef()+"","eng");
            }
        });

        return convertView;
    }

    public static class ViewHolder{
        TextView word,def;
        ImageView speak1,speak2,add1,add2;
        LinearLayout layout;
    }
    public void insertRecent(String word,String state,String def,String lan){
        DatabaseHelper helper=new DatabaseHelper(context);
        boolean res=helper.insertRecent(word,state,def,lan);
        if(res){
           // Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT).show();
        }
    }
    public void insertParse(String word,String state,String def,String lan){
        DatabaseHelper helper=new DatabaseHelper(context);
        boolean res=helper.insertParse(word,state,def,lan);
        if(res){
            View view=LayoutInflater.from(context).inflate(R.layout.favorite_layout,null);
            Toast toast=Toast.makeText(context,"Add to Favorite",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM,0,100);
            toast.setView(view);
            toast.show();

        }else {
            //Toast.makeText(getContext(),"Fail",Toast.LENGTH_SHORT).show();
        }
    }
    public Boolean isFont(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("Font", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Convert",false);
    }

    public void filter(String charText){
        if(getVerify()){
            charText = charText.toLowerCase(Locale.getDefault());
            dataArrayList.clear();
            if (charText.length()==0){
                dataArrayList.addAll(arrayList);
            }
            else {
                for (Data model : arrayList){
                    if (model.getWord().toLowerCase(Locale.getDefault()).contains(charText)){
                        dataArrayList.add(model);
                    }
                }
            }
        }else {
            charText = charText.toLowerCase(Locale.getDefault());
            dataArrayList.clear();
            if (charText.length()==0){
                dataArrayList.addAll(arrayList);
            }
            else {
                for (Data model : arrayList){
                    if (model.getDef().toLowerCase(Locale.getDefault()).contains(charText)){
                        dataArrayList.add(model);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    public void filterSubmit(String charText){
        if(getVerify()){
            charText = charText.toLowerCase(Locale.getDefault());
            dataArrayList.clear();
            if (charText.length()==0){
                dataArrayList.addAll(arrayList);
            }
            else {
                for (Data model : arrayList){
                    if (model.getWord().toLowerCase(Locale.getDefault()).equals(charText)){
                        dataArrayList.add(model);
                        insertRecent(model.getWord() + "", model.getState() + "", model.getDef() + "","eng");
                    }
                }
            }

        }else {
            charText = charText.toLowerCase(Locale.getDefault());
            dataArrayList.clear();
            if (charText.length()==0){
                dataArrayList.addAll(arrayList);
            }
            else {
                for (Data model : arrayList){
                    if (model.getDef().toLowerCase(Locale.getDefault()).equals(charText)){
                        dataArrayList.add(model);
                        insertRecent(model.getDef() + "", model.getState() + "", model.getWord() + "","myan");
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
    private boolean getVerify(){
        SharedPreferences sharedPreferences= context.getSharedPreferences("Language", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Language",true);
    }
}

