package com.cu.englishtomyanmardictionary.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FavoriteAdapter extends ArrayAdapter<Data> {
    Context context;
    ViewHolder viewHolder;
    ArrayList<Data> dataArrayList;
    TextToSpeech textToSpeech;
    int res;
    public FavoriteAdapter(Context context, ArrayList<Data> dataArrayList,int res) {
        super(context, R.layout.search_item,dataArrayList);
        this.context = context;
        this.dataArrayList = dataArrayList;
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
        viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    DatabaseHelper helper = new DatabaseHelper(context);
                    int res = helper.deleteParse(id);
                    if (res == 1) {
                        View view=LayoutInflater.from(context).inflate(R.layout.clear_layout,null);
                        Toast toast=Toast.makeText(context,"Clear",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM,0,100);
                        toast.setView(view);
                        toast.show();                    } else {
                        Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged();

                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        if(res==1) {
            viewHolder.word.setText(dataArrayList.get(position).getWord()+"  (" + dataArrayList.get(position).getState() + ")");
            //font
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
                deleteParse(dataArrayList.get(position).getId()+"");
            }
        });
        viewHolder.add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteParse(dataArrayList.get(position).getId()+"");
            }
        });
        return convertView;
    }
    public void deleteParse(String did){
        try {
            DatabaseHelper helper = new DatabaseHelper(context);
            int res = helper.deleteParse(did);
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
    }
    public Boolean isFont(){
        SharedPreferences sharedPreferences=getContext().getSharedPreferences("Font", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("Convert",false);
    }    public static class ViewHolder{
        TextView word,def;
        ImageView speak1,speak2,add1,add2;
        LinearLayout layout;
    }
}

