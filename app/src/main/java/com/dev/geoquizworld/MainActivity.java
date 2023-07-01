package com.dev.geoquizworld;

import static com.dev.geoquizworld.GuessActivity.formatNumber;
import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.activity.ConfirmationActivity;
import androidx.wear.remote.interactions.RemoteActivityHelper;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.geoquizworld.animations.Tools;
import com.google.android.gms.wearable.NodeClient;
import com.google.android.gms.wearable.Wearable;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends Activity {
    WearableRecyclerView wearableRecyclerView;
    Boolean isBrowse = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wearableRecyclerView = findViewById(R.id.main_menu_view);
        isBrowse = false; // not browsing countries, refresh on reload ok
        if (Account.isInsertedToDb()) {
            createFeedList();
        } else {
            try {
                getData();
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


    // contacts JSONArray
    JSONArray contacts = null;

    CountryReaderDbHelper dbHelper = new CountryReaderDbHelper(MyApplication.getAppContext());




    public static void toast(String message) {
        Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getData() throws IOException, JSONException { // inserts country data to a database, runs only at first time
        InputStream is = getResources().openRawResource(R.raw.countries_geoquiz);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            is.close();
        }
        String jsonString = writer.toString();
        JSONObject json = new JSONObject(jsonString);
        String name = null;
        try {
            // Getting Array of Contacts
            contacts = json.getJSONArray("result");

            // looping through All Contacts
            for(int i = 0; i < contacts.length(); i++){
                JSONObject c = contacts.getJSONObject(i);

                // Storing each json item in variable
                name = c.getString("name");
                System.out.println(name);

                // Gets the data repository in write mode
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_COUNTRY_ID, c.getInt("country_id"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SHORTNAME, c.getString("shortName"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_NAME, c.getString("name"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_NATIVE, c.getString("native"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CURRENCY, c.getString("currency"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CONTINENT, c.getString("continent"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CAPITAL, c.getString("capital"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EMOJI, c.getString("emoji"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EMOJIU, c.getString("emojiU"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_PHONE, c.getString("phone"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EXTRACT, c.getString("extract"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LATITUDE, c.getString("latitude"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LONGITUDE, c.getString("longitude"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_REGION, c.getString("region"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SUBREGION, c.getString("subregion"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_DEU, c.getString("deu"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_FRA, c.getString("fra"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_RUS, c.getString("rus"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SPA, c.getString("spa"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_AREA, c.getString("area"));
                values.put(CountryReaderContract.FeedEntry. COLUMN_COUNTRIES_INDEPENDENT, c.getInt("independent"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_STATUS, c.getString("status"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_UNMEMBER, c.getInt("unMember"));
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_USAGES, 0);
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_WON, 0);
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LOST, 0);
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_STREAK, 0);
                values.put(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SAVED, false);
                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(CountryReaderContract.FeedEntry.TABLE_COUNTRIES, null, values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Account.setIsInsertedToDb(true);
        createFeedList();
    }

    public void createFeedList(){
        ArrayList<MainItem> menuItems = new ArrayList<>();

        menuItems.add(new MainItem("heading","G A M E",false,null));
        menuItems.add(new MainItem("any","any flags",false,null));
        menuItems.add(new MainItem("noStreak","flags without 2-win-streak",false,null));
        menuItems.add(new MainItem("least","least played",false,null));

        String stats = "S T A T I S T I C S\n\n"
                +"total guesses: "+Account.guesses()
                +"\n2+ win streak: "+round(getStreakCount(),2)+"%"
                +"\ncurrent game streak: "+Account.score()
                +"\nflags played "+getPlayedCount()+"/248"
                +"\nhigh score: "+Account.highScore();
        menuItems.add(new MainItem("section",stats,false,null));
        menuItems.add(new MainItem("browse","browse world",false,null));
        menuItems.add(new MainItem("heading","A B O U T\n\ndev :) joewilliams007",false,null));
        menuItems.add(new MainItem("github","GitHub",false,null));
        menuItems.add(new MainItem("section","created with <3",false,null));

        build(menuItems);
    }

    public void createContinentList(){
        ArrayList<MainItem> menuItems = new ArrayList<>();

        menuItems.add(new MainItem("heading","C O N T I N E N T S",false,null));
        menuItems.add(new MainItem("back","<- back to menu",false,null));

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
        menuItems.add(new MainItem("back","<- back to menu",false,null));
        menuItems.add(new MainItem("browse","<- back to continents",false,null));
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

    Country correctCountry;

    public void createCountryData(String country){
        isBrowse = true;
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

        menuItems.add(new MainItem("back","<- back to menu",false,null));
        menuItems.add(new MainItem("browse","<- back to continents",false,null));

        menuItems.add(new MainItem("extract_text",s,false,null));


        build(menuItems);
    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    public double getStreakCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT * FROM Countries WHERE streak > 1";
        Cursor cursor = db.rawQuery(countQuery, null);
        double count = cursor.getCount();
        cursor.close();

        if (count != 0) {
            count = count*0.40322580645 ;
        }
        return count;
    }

    public int getPlayedCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT * FROM Countries WHERE usages > 0";
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isBrowse) {
            createFeedList();
        }
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
                    case "any": {
                        Intent intent = new Intent(MainActivity.this, GuessActivity.class);
                        intent.putExtra("type","any");
                        isBrowse = false;
                        startActivity(intent);
                        break;
                    }
                    case "least": {
                        Intent intent = new Intent(MainActivity.this, GuessActivity.class);
                        intent.putExtra("type","least");
                        isBrowse = false;
                        startActivity(intent);
                        break;
                    }
                    case "noStreak": {
                        Intent intent = new Intent(MainActivity.this, GuessActivity.class);
                        intent.putExtra("type","noStreak");
                        isBrowse = false;
                        startActivity(intent);
                        break;
                    }
                    case "github": {
                        openUrl("https://github.com/joewilliams007/geoQuiz");
                        break;
                    }
                    case "back": {
                        createFeedList();
                        break;
                    }
                    case "browse": {
                        createContinentList();
                        break;
                    }
                    case "country": {
                        createCountryData(menuItem.getText());
                        break;
                    }
                    case "country_area": {
                        createCountryData(menuItem.getText().split("\n")[0]);
                        break;
                    }
                    case "EU":
                    case "AS":
                    case "NA":
                    case "SA":
                    case "AF":
                    case "OC":
                    case "AN":
                        createCountryList("select * from Countries WHERE continent = '"+menuItem.getType()+"' ORDER BY name ASC",false);
                        break;
                    case "all":
                        createCountryList("select * from Countries ORDER BY name ASC",false);
                        break;
                    case "size":
                        createCountryList("select * from Countries ORDER BY area DESC",true);
                        break;
                    case "map": {
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        intent.putExtra("name",correctCountry.name);
                        intent.putExtra("latitude",correctCountry.latitude);
                        intent.putExtra("longitude",correctCountry.longitude);
                        startActivity(intent);
                        break;
                    }
                    case "world_map": {
                        isBrowse = true;
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "extract_text":
                    case "extract": {
                        Intent intent = new Intent(MainActivity.this, ExtractActivity.class);
                        intent.putExtra("extract",correctCountry.emoji+" "+correctCountry.name+"\n\n"+correctCountry.extract);
                        startActivity(intent);
                        break;
                    }
                    case "translations": {
                        Intent intent = new Intent(MainActivity.this, ExtractActivity.class);
                        intent.putExtra("extract",correctCountry.emoji+" "+correctCountry.name+"\n\n"
                                +"\uD83C\uDDE9\uD83C\uDDEA "+correctCountry.deu
                                +"\n\uD83C\uDDEB\uD83C\uDDF7 "+correctCountry.fra
                                +"\n\uD83C\uDDF7\uD83C\uDDFA "+correctCountry.rus
                                +"\n\uD83C\uDDEA\uD83C\uDDF8 "+correctCountry.spa
                        );
                        startActivity(intent);
                        break;
                    }
                    default: {

                        break;
                    }
                }
            }
        }));




        wearableRecyclerView.requestFocus();
    }


    public static void vibrate() {
        if (!Account.vibrate()) {
            return;
        }
        Vibrator vibrator;
        VibratorManager vibratorManager;
        long[] VIBRATE_PATTERN = {500, 500};
        if (Build.VERSION.SDK_INT>=31) {
            vibratorManager = (VibratorManager) MyApplication.getAppContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            vibrator = vibratorManager.getDefaultVibrator();
        }
        else {
            vibrator = (Vibrator) MyApplication.getAppContext().getSystemService(Context.VIBRATOR_SERVICE);
        }

        // vibrator.vibrate(VibrationEffect.createWaveform(VIBRATE_PATTERN,0));
        try {
            vibrator.vibrate(VibrationEffect.createOneShot(10,255));
        } catch (Exception e) {
            System.out.println("vibration failed "+e.getMessage());
        }

    }

    public static void openUrl(String url) {
        Intent intent = new Intent(MyApplication.getAppContext(), ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(intent);
        vibrate();
        Executor executor = new Executor() {
            @Override
            public void execute(Runnable runnable) {

            }
        };
        RemoteActivityHelper remoteActivityHelper = new RemoteActivityHelper(MyApplication.getAppContext(), executor);

        NodeClient client = Wearable.getNodeClient(MyApplication.getAppContext());
        client.getConnectedNodes().addOnSuccessListener(nodes -> {
            if (nodes.size() > 0) {
                String nodeId = nodes.get(0).getId();
                ListenableFuture<Void> result = remoteActivityHelper.startRemoteActivity(
                        new Intent(Intent.ACTION_VIEW)
                                .addCategory(Intent.CATEGORY_BROWSABLE)
                                .setData(
                                        Uri.parse(url)
                                )
                        , nodeId);
                result.addListener(() -> {
                    try {
                        result.get();
                    } catch (Exception e) {
                        toast("Failed " + e);
                    }
                }, executor);
            } else {
                toast("no connected wear watch");
            }
        }).addOnFailureListener(failure -> {
            toast("failed "+failure.getMessage());
        });
    }
}