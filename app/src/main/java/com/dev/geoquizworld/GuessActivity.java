package com.dev.geoquizworld;

import static com.dev.geoquizworld.MainActivity.openUrl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.geoquizworld.animations.Tools;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class GuessActivity extends Activity {
    Intent intent;
    WearableRecyclerView wearableRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);
        wearableRecyclerView = findViewById(R.id.main_menu_view);
        intent = getIntent();
        toast(intent.getStringExtra("type"));
        getFlag();
    }

    String correctCountryName = null, correctCountryEmoji = null, latitude, longitude;
    Country correctCountry;

    public void createFeedList(ArrayList<Country> items){
        ArrayList<MainItem> menuItems = new ArrayList<>();
        int randomNum = ThreadLocalRandom.current().nextInt(0, 3 + 1);

        menuItems.add(new MainItem("flag",items.get(randomNum).emoji,false,null));

        int i = 0;
        for (Country country : items){
            boolean isCorrect = false;
            if (i == randomNum) {
                isCorrect = true;
                correctCountryName = country.getName();
                correctCountryEmoji = country.getEmoji();
                longitude = country.getLongitude();
                latitude = country.getLatitude();
                correctCountry = country;
            }
            menuItems.add(new MainItem("guess",country.name,isCorrect,country.emoji));
            i++;
        }
        // menuItems.add(new MainItem("skip","skip",false));
        menuItems.add(new MainItem("settings","",false,null));
        build(menuItems);
    }
    Boolean isSolution = false;
    public void createFeedSolution(Boolean correct, String emoji){
        ArrayList<MainItem> menuItems = new ArrayList<>();

        isSolution = true;

        if (correctCountryEmoji.equals(emoji)) { // in case there are multiple correct answers using same flag
            correct = true;
        }

        menuItems.add(new MainItem("smallFlag",correctCountryEmoji,false,null));
        if (!correct) {
            menuItems.add(new MainItem("settings","wrong. correct name is "+correctCountryName,false,null));
        } else {
            menuItems.add(new MainItem("settings","correct! it is "+correctCountryName+"\nscore: "+(Account.score()+1),false,null));
        }

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

        Account.upGuesses();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        updateDb(correct);


        build(menuItems);
    }

    private void updateDb(Boolean correct) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (correct) {
            String strSQL = "UPDATE Countries SET usages = usages+1,streak=streak+1,won=won+1 WHERE name='"+correctCountryName+"'";
            db.execSQL(strSQL);


            Account.upScore();
            if (Account.score()>Account.highScore()) {
                Account.setHighScore(Account.score());
            }
        } else {
            String strSQL ="UPDATE Countries SET usages = usages+1,streak=0,lost=lost+1 WHERE name='"+correctCountryName+"'";
            db.execSQL(strSQL);
            Account.resetScore();
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
                    case "guess": {
                        createFeedSolution(menuItem.correct,menuItem.emoji);
                        break;
                    }
                    case "next":
                    case "skip": {
                        getFlag();
                        break;
                    }
                    case "map": {
                        Intent intent = new Intent(GuessActivity.this, MapActivity.class);
                        intent.putExtra("name",correctCountryName);
                        intent.putExtra("latitude",latitude);
                        intent.putExtra("longitude",longitude);
                        startActivity(intent);
                        break;
                    }
                    case "extract_text":
                    case "extract": {
                        Intent intent = new Intent(GuessActivity.this, ExtractActivity.class);
                        intent.putExtra("extract",correctCountryEmoji+" "+correctCountryName+"\n\n"+correctCountry.extract);
                        startActivity(intent);
                        break;
                    }
                    case "translations": {
                        Intent intent = new Intent(GuessActivity.this, ExtractActivity.class);
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

        if (!isSolution) {
            wearableRecyclerView.setAlpha(0);
            wearableRecyclerView.setTranslationY(Tools.dpToPx(40));
            wearableRecyclerView.animate().alpha(1).translationY(0).setDuration(300).withLayer();
        } else {
            isSolution = false;
        }


        wearableRecyclerView.requestFocus();
    }
    CountryReaderDbHelper dbHelper = new CountryReaderDbHelper(MyApplication.getAppContext());

    private void getFlag() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;
        String type = intent.getStringExtra("type");
        if (type.equals("any")) {
            cursor= db.rawQuery("select * from Countries order by RANDOM() limit 4",null);
        } else if (type.equals("noStreak")) {
            cursor= db.rawQuery("select * from Countries WHERE streak = 0 order by RANDOM() limit 4",null);
        } else { // least
            cursor= db.rawQuery("select * from Countries order by usages ASC limit 4",null);
        }

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

        Integer i = 0;
        for (Country country: items) {
            i++;
        }
        createFeedList(items);
    }


    public static void toast(String message) {
        Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_SHORT).show();
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

    public static String formatNumber(String number) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(number);
        String string = String.valueOf(number);

        if(string.contains("."))
            string= (String) string.subSequence(0,string.indexOf("."));

        if(string.length() > 3) {
            int firstComma=(string.length() % 3);
            int countComma = (string.length()-1)/3;

            if(firstComma != 0)
                stringBuilder.insert(firstComma, ",");

            for(int i = stringBuilder.indexOf(",")+4; i<string.length()+countComma; i+=4)
                stringBuilder.insert(i, ",");
        }
        return stringBuilder.toString();
    }
}