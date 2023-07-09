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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wearableRecyclerView = findViewById(R.id.main_menu_view);
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
        menuItems.add(new MainItem("heading","F L A G S",false,null));
        menuItems.add(new MainItem("any","any flags",false,null));
        menuItems.add(new MainItem("noStreak","flags without 2-win-streak",false,null));
        menuItems.add(new MainItem("least","least played",false,null));
        menuItems.add(new MainItem("heading","L O C A T E",false,null));
        menuItems.add(new MainItem("locate","locate country",false,null));
        menuItems.add(new MainItem("noStreakLoc","locations without 2-win-streak",false,null));
        menuItems.add(new MainItem("heading","S T A T I S T I C S",false,null));
        String stats = "F L A G S\n\n"
                +"total guesses: "+Account.guesses()
                +"\n2+ win streak: "+round(getStreakCount("flags"),2)+"%"
                +"\ncurrent game streak: "+Account.score()
                +"\nflags played "+getPlayedCount()+"/248"
                +"\nhigh score: "+Account.highScore();
        menuItems.add(new MainItem("section",stats,false,null));

        String statsLoc = "L O C A T I O N\n\n"
                +"total guesses: "+Account.guessesLoc()
                +"\n2+ win streak: "+round(getStreakCount("loc"),2)+"%"
                +"\ncurrent game streak: "+Account.scoreLoc()
                +"\nhigh score: "+Account.highScoreLoc();
        menuItems.add(new MainItem("section",statsLoc,false,null));

        menuItems.add(new MainItem("browse","browse world",false,null));
        menuItems.add(new MainItem("heading","A B O U T\n\ndev :) joewilliams007",false,null));
        menuItems.add(new MainItem("github","GitHub",false,null));
        menuItems.add(new MainItem("section","created with <3",false,null));

        build(menuItems);
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
    public double getStreakCount(String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String countQuery;
        if (type.equals("flags")) {
            countQuery = "SELECT * FROM Countries WHERE streak > 1";
        } else {
            countQuery = "SELECT * FROM Countries WHERE loc_streak > 1";
        }

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
        createFeedList();
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
                        startActivity(intent);
                        break;
                    }
                    case "least": {
                        Intent intent = new Intent(MainActivity.this, GuessActivity.class);
                        intent.putExtra("type","least");
                        startActivity(intent);
                        break;
                    }
                    case "noStreak": {
                        Intent intent = new Intent(MainActivity.this, GuessActivity.class);
                        intent.putExtra("type","noStreak");
                        startActivity(intent);
                        break;
                    }
                    case "noStreakLoc": {
                        Intent intent = new Intent(MainActivity.this, LocateActivity.class);
                        intent.putExtra("type", "noStreak");
                        startActivity(intent);
                        break;
                    }
                    case "github": {
                        openUrl("https://github.com/joewilliams007/geoQuiz");
                        break;
                    }
                    case "browse": {
                        Intent intent = new Intent(MainActivity.this, BrowseActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case "locate": {
                        Intent intent = new Intent(MainActivity.this, LocateActivity.class);
                        intent.putExtra("type", "any");
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