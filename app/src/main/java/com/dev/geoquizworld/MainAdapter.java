package com.dev.geoquizworld;

import static android.text.format.DateUtils.getRelativeTimeSpanString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import java.util.ArrayList;

public class MainAdapter extends WearableRecyclerView.Adapter<MainAdapter.RecyclerViewHolder> {

    private ArrayList<MainItem> dataSource = new ArrayList<MainItem>();
    public interface AdapterCallback{
        void onItemClicked(Integer menuPosition);
    }
    private AdapterCallback callback;

    private String drawableIcon;
    private Context context;


    public MainAdapter(Context context, ArrayList<MainItem> dataArgs, AdapterCallback callback){
        this.context = context;
        this.dataSource = dataArgs;
        this.callback = callback;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        RelativeLayout menuContainer;
        TextView menuItem;

        public RecyclerViewHolder(View view) {
            super(view);
            menuContainer = view.findViewById(R.id.menu_container);
            menuItem = view.findViewById(R.id.menu_item);
        };
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        MainItem data_provider = dataSource.get(position);
        holder.setIsRecyclable(false);
        String text = data_provider.getText();
        Boolean correct = data_provider.getCorrect();
        String type = data_provider.getType();

        switch (type) {
            case "smallFlag":
                holder.menuItem.setText(text);
                holder.menuItem.setBackground(null);
                holder.menuItem.setTextSize(70);
                break;
            case "flag":
                holder.menuItem.setText(text);
                holder.menuItem.setBackground(null);
                holder.menuItem.setTextSize(100);
                break;
            case "guess":
            case "option":
            case "any":
            case "noStreak":
            case "least":
            case "github":
            case "next":
            case "extract":
            case "map":
            case "EU":
            case "AS":
            case "NA":
            case "SA":
            case "AF":
            case "OC":
            case "AN":
            case "back":
            case "browse":
            case "world_map":
            case "translations":
            case "size":
            case "all":
            case "country_area":
                holder.menuItem.setText(text);
                break;
            case "country":
                holder.menuItem.setText(data_provider.getEmoji()+" "+text);
                break;
            default:
                holder.menuItem.setText(text);
                holder.menuItem.setBackground(null);
                break;
        }

        holder.menuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                holder.menuItem.setTextColor(Color.parseColor("#FFFFFF"));
                if(callback != null) {
                    callback.onItemClicked(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}

