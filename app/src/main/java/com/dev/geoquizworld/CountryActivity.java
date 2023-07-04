package com.dev.geoquizworld;

import static com.dev.geoquizworld.GuessActivity.formatNumber;
import static com.dev.geoquizworld.MainActivity.vibrate;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.geoquizworld.animations.Tools;

import java.util.ArrayList;

public class CountryActivity extends Activity {
    WearableRecyclerView wearableRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        wearableRecyclerView = findViewById(R.id.main_menu_view);
        Intent intent = getIntent();
        switch (intent.getStringExtra("type")) {
            case "EU":
            case "AS":
            case "NA":
            case "SA":
            case "AF":
            case "OC":
            case "AN":
                createCountryList("select * from Countries WHERE continent = '"+intent.getStringExtra("type")+"' ORDER BY name ASC",false);
                break;
            case "all":
                createCountryList("select * from Countries ORDER BY name ASC",false);
                break;
            case "size":
                createCountryList("select * from Countries ORDER BY area DESC",true);
                break;
        }
    }
    CountryReaderDbHelper dbHelper = new CountryReaderDbHelper(MyApplication.getAppContext());
    public void createCountryList(String query, Boolean showArea){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery(query,null);


        ArrayList<Country> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry._ID));
            Integer country_id = cursor.getInt(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_COUNTRY_ID));
            String shortName = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SHORTNAME));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_NAME));
            String nativeName = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_NATIVE));
            String currency = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CURRENCY));
            String continent = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CONTINENT));
            String capital = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CAPITAL));
            String emoji = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EMOJI));
            String emojiU = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EMOJIU));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_PHONE));
            String extract = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EXTRACT));
            String latitude = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LONGITUDE));
            String region = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_REGION));
            String subregion = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SUBREGION));
            String deu = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_DEU));
            String fra = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_FRA));
            String rus = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_RUS));
            String spa = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SPA));
            Integer area = cursor.getInt(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_AREA));
            Integer independent = cursor.getInt(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry. COLUMN_COUNTRIES_INDEPENDENT));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_STATUS));
            Integer unmember = cursor.getInt(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_UNMEMBER));
            Integer usages = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_USAGES)));
            Integer won = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_WON)));
            Integer lost = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LOST)));
            Integer streak = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_STREAK)));
            int saved = cursor.getInt(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SAVED));
            boolean isSaved = saved == 1;
            boolean isIndependent = independent == 1;
            boolean isUnmember = unmember == 1;
            items.add(new Country(itemId,country_id,shortName,name,nativeName,currency,continent,capital,emoji,emojiU,phone,extract,latitude,longitude,
                    region,subregion,deu,fra,rus,spa,area,isIndependent,status,isUnmember,usages,won,lost,streak,isSaved));
        }
        cursor.close();


        ArrayList<MainItem> menuItems = new ArrayList<>();

        menuItems.add(new MainItem("heading","C O U N T R I E S",false,null));
        for (Country country : items){
            if (showArea) {
                menuItems.add(new MainItem("country_area",country.name+"\n"+formatNumber(String.valueOf(country.getArea()))+" km²",false,country.emoji));
            } else {
                menuItems.add(new MainItem("country",country.name,false,country.emoji));
            }
        }
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
                Intent intent = new Intent(CountryActivity.this, InfoActivity.class);
                intent.putExtra("country",menuItem.getText());
                startActivity(intent);
            }
        }));
        wearableRecyclerView.requestFocus();
    }

}