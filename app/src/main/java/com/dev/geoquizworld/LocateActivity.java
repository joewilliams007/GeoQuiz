package com.dev.geoquizworld;

import static com.dev.geoquizworld.GuessActivity.formatNumber;
import static com.dev.geoquizworld.GuessActivity.vibrate;
import static com.dev.geoquizworld.LocateMapActivity.next;
import static com.dev.geoquizworld.MainActivity.openUrl;

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

public class LocateActivity extends Activity {
    Intent intent;
    WearableRecyclerView wearableRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);
        wearableRecyclerView = findViewById(R.id.main_menu_view);
        intent = getIntent();
        next = false; // isNextGuess
        getCountry();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (next) {
            createFeedSolution();
            next = false;
        }
    }

    String correctCountryName = null, correctCountryEmoji = null, latitude, longitude, area;
    Country correctCountry = null;

    public void createFeedList(ArrayList<Country> items){
        ArrayList<MainItem> menuItems = new ArrayList<>();
        menuItems.add(new MainItem("smallFlag",items.get(0).emoji,false,items.get(0).emoji));

        for (Country country : items){
            correctCountryName = country.getName();
            correctCountryEmoji = country.getEmoji();
            longitude = country.getLongitude();
            latitude = country.getLatitude();
            area = String.valueOf(country.getArea());
            correctCountry = country;
            menuItems.add(new MainItem("info",country.name,true,country.emoji));
        }
        menuItems.add(new MainItem("map_select","select on map",true,null));
        menuItems.add(new MainItem("settings","",false,null));
        build(menuItems);
    }


    public void createFeedSolution(){
        ArrayList<MainItem> menuItems = new ArrayList<>();

        String continent = null;
        switch (correctCountry.continent) {
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
        menuItems.add(new MainItem("smallFlag",correctCountry.emoji,false,null));
        menuItems.add(new MainItem("info",correctCountry.name+"\nscore: "+Account.scoreLoc(),false,null));
        menuItems.add(new MainItem("next","next",false,null));

        menuItems.add(new MainItem("info","C O N T I N E N T\n\n"+continent,false,null));
        menuItems.add(new MainItem("info","R E G I O N\n\n"+correctCountry.region+"\n"+correctCountry.subregion,false,null));
        menuItems.add(new MainItem("info","C A P I T A L\n\n"+correctCountry.capital,false,null));
        menuItems.add(new MainItem("info","A R E A\n\n"+formatNumber(String.valueOf(correctCountry.area))+" km²",false,null));
        menuItems.add(new MainItem("info","C U R R E N C Y\n\n"+correctCountry.currency,false,null));
        if (correctCountry.unMember) {
            menuItems.add(new MainItem("info","U N  M E M B E R\n\n"+"yes",false,null));
        } else {
            menuItems.add(new MainItem("info","U N  M E M B E R\n\n"+"no",false,null));
        }
        menuItems.add(new MainItem("map","map",false,null));

        String s = correctCountry.getExtract();
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
                    case "next":
                    case "skip": {
                        getCountry();
                        break;
                    }
                    case "map_select": {
                        Intent intent = new Intent(LocateActivity.this, LocateMapActivity.class);
                        intent.putExtra("name",correctCountryName);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);
                        intent.putExtra("area",area);
                        startActivity(intent);
                        break;
                    }
                    case "map": {
                        Intent intent = new Intent(LocateActivity.this, MapActivity.class);
                        intent.putExtra("name",correctCountry.name);
                        intent.putExtra("latitude",correctCountry.latitude);
                        intent.putExtra("longitude",correctCountry.longitude);
                        startActivity(intent);
                        break;
                    }
                    case "extract_text":
                    case "extract": {
                        Intent intent = new Intent(LocateActivity.this, ExtractActivity.class);
                        intent.putExtra("extract",correctCountryEmoji+" "+correctCountryName+"\n\n"+correctCountry.extract);
                        startActivity(intent);
                        break;
                    }
                    case "translations": {
                        Intent intent = new Intent(LocateActivity.this, ExtractActivity.class);
                        intent.putExtra("extract",correctCountry.emoji+" "+correctCountry.name+"\n\n"
                                +"\uD83C\uDDE9\uD83C\uDDEA "+correctCountry.deu
                                +"\n\uD83C\uDDEB\uD83C\uDDF7 "+correctCountry.fra
                                +"\n\uD83C\uDDF7\uD83C\uDDFA "+correctCountry.rus
                                +"\n\uD83C\uDDEA\uD83C\uDDF8 "+correctCountry.spa
                        );
                        startActivity(intent);
                        break;
                    }
                    case "google": {
                        openUrl("https://www.google.com/search?client=mobile&q=");
                        break;
                    }
                    /*case "notif": {
                        Intent intent = new Intent(MainActivity.this, NotifActivity.class);
                        startActivity(intent);
                        break;
                    }*/
                    default: {
                        break;
                    }
                }
            }
        }));

        wearableRecyclerView.setAlpha(0);
        wearableRecyclerView.setTranslationY(Tools.dpToPx(40));
        wearableRecyclerView.animate().alpha(1).translationY(0).setDuration(300).withLayer();
        wearableRecyclerView.requestFocus();
    }
    CountryReaderDbHelper dbHelper = new CountryReaderDbHelper(MyApplication.getAppContext());

    private void getCountry() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from Countries order by RANDOM() limit 1",null);

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
        createFeedList(items);
    }
}