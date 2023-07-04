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

public class InfoActivity extends Activity {
    WearableRecyclerView wearableRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        wearableRecyclerView = findViewById(R.id.main_menu_view);
        Intent intent = getIntent();
        String country = intent.getStringExtra("country").split("\n")[0];
        createCountryData(country);

    }
    CountryReaderDbHelper dbHelper = new CountryReaderDbHelper(MyApplication.getAppContext());
    Country correctCountry;
    public void createCountryData(String country){
        ArrayList<MainItem> menuItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery("select * from Countries WHERE name = '"+country+"'",null);

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

        Country c = items.get(0);


        correctCountry = c;

        menuItems.add(new MainItem("smallFlag",c.emoji,false,null));
        menuItems.add(new MainItem("info",c.name,false,null));
        String continent = null;
        switch (c.getContinent()) {
            case "AF":
                continent = "Africa";
                break;
            case "EU":
                continent = "Europe";
                break;
            case "AN":
                continent = "Antarctica";
                break;
            case "AS":
                continent = "Asia";
                break;
            case "OC":
                continent = "Oceania";
                break;
            case "NA":
                continent = "North America";
                break;
            case "SA":
                continent = "South America";
                break;
        }


        menuItems.add(new MainItem("info","C O N T I N E N T\n\n"+continent,false,null));
        menuItems.add(new MainItem("info","R E G I O N\n\n"+c.getRegion()+"\n"+c.getSubregion(),false,null));
        menuItems.add(new MainItem("info","C A P I T A L\n\n"+c.getCapital(),false,null));
        menuItems.add(new MainItem("info","A R E A\n\n"+formatNumber(String.valueOf(c.getArea()))+" km²",false,null));
        menuItems.add(new MainItem("info","C U R R E N C Y\n\n"+c.getCurrency(),false,null));
        if (c.getUnMember()) {
            menuItems.add(new MainItem("info","U N  M E M B E R\n\n"+"yes",false,null));
        } else {
            menuItems.add(new MainItem("info","U N  M E M B E R\n\n"+"no",false,null));
        }

        menuItems.add(new MainItem("map","map",false,null));

        String s = c.getExtract();
        if (s.length() > 100) {
            s = s.substring(0, Math.min(s.length(), 100)) + "...";
        }
        menuItems.add(new MainItem("translations","translations",false,null));
        menuItems.add(new MainItem("extract","read more",false,null));
        menuItems.add(new MainItem("extract_text",s,false,null));
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
                    case "map": {
                        Intent intent = new Intent(InfoActivity.this, MapActivity.class);
                        intent.putExtra("name",correctCountry.name);
                        intent.putExtra("latitude",correctCountry.latitude);
                        intent.putExtra("longitude",correctCountry.longitude);
                        startActivity(intent);
                        break;
                    }
                    case "world_map": {
                        Intent intent = new Intent(InfoActivity.this, MapActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "extract_text":
                    case "extract": {
                        Intent intent = new Intent(InfoActivity.this, ExtractActivity.class);
                        intent.putExtra("extract",correctCountry.emoji+" "+correctCountry.name+"\n\n"+correctCountry.extract);
                        startActivity(intent);
                        break;
                    }
                    case "translations": {
                        Intent intent = new Intent(InfoActivity.this, ExtractActivity.class);
                        intent.putExtra("extract",correctCountry.emoji+" "+correctCountry.name+"\n\n"
                                +"\uD83C\uDDE9\uD83C\uDDEA "+correctCountry.deu
                                +"\n\uD83C\uDDEB\uD83C\uDDF7 "+correctCountry.fra
                                +"\n\uD83C\uDDF7\uD83C\uDDFA "+correctCountry.rus
                                +"\n\uD83C\uDDEA\uD83C\uDDF8 "+correctCountry.spa
                        );
                        startActivity(intent);
                        break;
                    }
                }
            }
        }));
        wearableRecyclerView.requestFocus();
    }

}