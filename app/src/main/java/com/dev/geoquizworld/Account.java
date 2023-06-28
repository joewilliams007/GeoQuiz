package com.dev.geoquizworld;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Account {
    public static String theme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getString("theme", "amoled_part");
    }

    public static void setTheme(String theme) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putString("theme",theme);
        editor.apply();
    }

    public static Boolean animate() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("animate", true);
    }

    public static void setAnimate(Boolean animate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("animate",animate);
        editor.apply();
    }

    public static Boolean vibrate() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("vibrate", true);
    }

    public static void setVibrate(Boolean vibrate) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("vibrate",vibrate);
        editor.apply();
    }

    public static Boolean isInsertedToDb() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getBoolean("isInsertedToDb", false);
    }

    public static void setIsInsertedToDb(Boolean verified) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putBoolean("isInsertedToDb",verified);
        editor.apply();
    }

    public static int highScore() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("highScore", 0);
    }

    public static void setHighScore(int highScore) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("highScore",highScore);
        editor.apply();
    }

    public static int score() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("score", 0);
    }

    public static void upScore() {
        int highScore = score()+1;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("score",highScore);
        editor.apply();
    }

    public static void resetScore() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("score",0);
        editor.apply();
    }

    public static int guesses() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        return preferences.getInt("guesses", 0);
    }

    public static void upGuesses() {
        int guesses = guesses()+1;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit().putInt("guesses",guesses);
        editor.apply();
    }
}
