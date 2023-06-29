package com.dev.geoquizworld;

import static com.dev.geoquizworld.MainActivity.toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dev.geoquizworld.animations.Tools;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WikiActivity extends Activity {
    Intent intent;
    private TextView mTextView;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    ScrollView scroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki);
        mTextView = findViewById(R.id.text);
        scroll = findViewById(R.id.scroll);
        intent = getIntent();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        requestWiki(intent);

    }

    private void requestWiki(Intent intent) {
        MethodsWiki methods = RetrofitClient.getRetrofitInstance().create(MethodsWiki.class);
        String total_url = "https://en.wikipedia.org/w/api.php?action=query&exlimit=1&explaintext=1&exsentences=10&formatversion=2&prop=extracts&format=json&titles="+intent.getStringExtra("query");

        Call<ModelWiki> call = methods.getAllData(total_url);
        call.enqueue(new Callback<ModelWiki>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ModelWiki> call, @NonNull Response<ModelWiki> response) {
                if (response.isSuccessful()) {
                    // Do awesome stuff
                    assert response.body() != null;
                    List<Pages> pages = response.body().getQuery().getPages();
                    mTextView.setText(intent.getStringExtra("query")+" "+intent.getStringExtra("emoji")+"\n\n"+pages.get(0).extract);
                    scroll.requestFocus();
                } else if (response.code() == 429) {
                    // Handle unauthorized
                } else {
                    toast(response.message());
                    mTextView.setText("failed to fetch this flag");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ModelWiki> call, @NonNull Throwable t) {
                Log.d("error_contact", t.toString());
                toast("no internet connection");
                mTextView.setText("no internet connection");
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    CountryReaderDbHelper dbHelper = new CountryReaderDbHelper(MyApplication.getAppContext());
}