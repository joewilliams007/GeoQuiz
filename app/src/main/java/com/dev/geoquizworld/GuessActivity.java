package com.dev.geoquizworld;

import static com.dev.geoquizworld.Account.vibrate;

import android.app.Activity;
import android.content.ContentValues;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.dev.geoquizworld.animations.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
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

    String correctCountryName = null;
    String correctCountryEmoji = null;

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
            menuItems.add(new MainItem("settings","wrong. correct name is\n"+correctCountryName,false,null));
        } else {
            menuItems.add(new MainItem("settings","correct! it is\n"+correctCountryName+"\n\nscore: "+(Account.score()+1),false,null));
        }
        Account.upGuesses();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        updateDb(correct);


        build(menuItems);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getFlag();
            }
        }, 3500);

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

                switch (menuItem.getType()) {
                    case "guess": {
                        vibrate();
                        createFeedSolution(menuItem.correct,menuItem.emoji);
                        break;
                    }
                    case "skip": {
                        vibrate();
                        getFlag();
                        break;
                    }
                    /*case "notif": {
                        Intent intent = new Intent(MainActivity.this, NotifActivity.class);
                        startActivity(intent);
                        break;
                    }*/
                    default: {
                        vibrate();
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
            String name = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_NAME));
            String code = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_CODE));
            String emoji = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_EMOJI));
            String unicode = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_UNICODE));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_IMAGE));

            Integer usages = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_USAGES)));
            Integer won = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_WON)));
            Integer lost = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_LOST)));
            Integer streak = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_STREAK)));
            int saved = cursor.getInt(cursor.getColumnIndexOrThrow(CountryReaderContract.FeedEntry.COLUMN_COUNTRIES_SAVED));
            boolean isSaved = saved == 1;
            items.add(new Country(itemId,name,code,emoji,unicode,image,usages,won,lost,streak,isSaved));
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
}