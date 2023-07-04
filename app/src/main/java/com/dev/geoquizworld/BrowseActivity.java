package com.dev.geoquizworld;

import static com.dev.geoquizworld.GuessActivity.formatNumber;
import static com.dev.geoquizworld.MainActivity.vibrate;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.geoquizworld.animations.Tools;
import java.util.ArrayList;

public class BrowseActivity extends Activity {
    WearableRecyclerView wearableRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        wearableRecyclerView = findViewById(R.id.main_menu_view);
        createContinentList();
    }

    public void createContinentList(){
        ArrayList<MainItem> menuItems = new ArrayList<>();
        menuItems.add(new MainItem("heading","C O N T I N E N T S",false,null));
        menuItems.add(new MainItem("EU","Europe",false,null));
        menuItems.add(new MainItem("AS","Asia",false,null));
        menuItems.add(new MainItem("NA","North America",false,null));
        menuItems.add(new MainItem("SA","South America",false,null));
        menuItems.add(new MainItem("AF","Africa",false,null));
        menuItems.add(new MainItem("OC","Oceania",false,null));
        menuItems.add(new MainItem("AN","Antarctica",false,null));
        menuItems.add(new MainItem("heading","M O R E",false,null));
        menuItems.add(new MainItem("all","All countries",false,null));
        menuItems.add(new MainItem("size","Countries ordered by area size",false,null));
        menuItems.add(new MainItem("world_map","World Map",false,null));
        menuItems.add(new MainItem("empty","",false,null));
        menuItems.add(new MainItem("empty","",false,null));
        build(menuItems);
    }

    private void build(ArrayList<MainItem> menuItems) {
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));

        wearableRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView wearableRecyclerView, int newState) {
                super.onScrollStateChanged(wearableRecyclerView, newState);
            }
        });

        wearableRecyclerView.setAdapter(new MainAdapter(this, menuItems, new MainAdapter.AdapterCallback() {
            @Override
            public void onItemClicked(final Integer menuPosition) {
                MainItem menuItem = menuItems.get(menuPosition);
                vibrate();
                switch (menuItem.getType()) {
                    case "world_map": {
                        Intent intent = new Intent(BrowseActivity.this, MapActivity.class);
                        startActivity(intent);
                        break;
                    }
                    default:
                        Intent intent = new Intent(BrowseActivity.this, CountryActivity.class);
                        intent.putExtra("type",menuItem.getType());
                        startActivity(intent);
                        break;
                }
            }
        }));
        wearableRecyclerView.requestFocus();
    }
}