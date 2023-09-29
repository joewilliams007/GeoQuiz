package com.dev.geoquizworld;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.core.view.InputDeviceCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewConfigurationCompat;

import com.dev.geoquizworld.animations.Tools;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MapActivity extends Activity {
    Intent intent;
    LocationManager locationManager;
    GeoPoint loc;

    private static final String PREFS_NAME = "org.andnav.osm.prefs";
    private static final String PREFS_TILE_SOURCE = "tilesource";
    private static final String PREFS_LATITUDE_STRING = "latitudeString";
    private static final String PREFS_LONGITUDE_STRING = "longitudeString";
    private static final String PREFS_ORIENTATION = "orientation";
    private static final String PREFS_ZOOM_LEVEL_DOUBLE = "zoomLevelDouble";
    private SharedPreferences mPrefs;
    private MapView mMapView;
    private MyLocationNewOverlay mLocationOverlay;
    private Overlay touchOverlay;
    private CompassOverlay mCompassOverlay = null;
    private MinimapOverlay mMinimapOverlay;
    private ScaleBarOverlay mScaleBarOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private CopyrightOverlay mCopyrightOverlay;
    private static final int MENU_ABOUT = Menu.FIRST + 1;
    private static final int MENU_LAST_ID = MENU_ABOUT + 1; // Always set to last unused id
    String name, longitude, latitude;
    Marker startMarker;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTheme(R.style.Theme_Block);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        intent = getIntent();


        mMapView = findViewById(R.id.map);
        mMapView.setDestroyMode(false);
        mMapView.setTag("mapView"); // needed for OpenStreetMapViewTest
        loc = null;
        final Context context = MapActivity.this;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        //My Location
        //note you have handle the permissions yourself, the overlay did not do it for you
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mMapView);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.disableFollowLocation();
        mLocationOverlay.setDrawAccuracyEnabled(true);
        mMapView.getOverlays().add(this.mLocationOverlay);


        //support for map rotation
        mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
        mRotationGestureOverlay.setEnabled(true);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);

        //needed for pinch zooms
        mMapView.setMultiTouchControls(true);

        //scales tiles to the current screen's DPI, helps with readability of labels
        mMapView.setTilesScaledToDpi(true);

        //the rest of this is restoring the last map location the user looked at
        final float zoomLevel = mPrefs.getFloat(PREFS_ZOOM_LEVEL_DOUBLE, 1);
        mMapView.getController().setZoom(zoomLevel);
        final float orientation = mPrefs.getFloat(PREFS_ORIENTATION, 0);
        mMapView.setMapOrientation(orientation, false);
        /*final String latitudeString = mPrefs.getString(PREFS_LATITUDE_STRING, "1.0");
        final String longitudeString = mPrefs.getString(PREFS_LONGITUDE_STRING, "1.0");
        final double latitude = Double.valueOf(latitudeString);
        final double longitude = Double.valueOf(longitudeString);*/

        name = intent.getStringExtra("name");
        if (name != null) {
            longitude = intent.getStringExtra("longitude");
            latitude = intent.getStringExtra("latitude");

            mMapView.setExpectedCenter(new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude)));

            GeoPoint geoPoint = new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
            startMarker = new Marker(mMapView);
            startMarker.setPosition(geoPoint);
            startMarker.setTitle(name);
            startMarker.setIcon(getDrawable(R.drawable.location));
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mMapView.getOverlays().add(startMarker);
        } else {
            getAllCountries();
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_SCROLL &&
                event.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)
        ) {
            // Don't forget the negation here
            float delta = -event.getAxisValue(MotionEventCompat.AXIS_SCROLL) *
                    ViewConfigurationCompat.getScaledVerticalScrollFactor(
                            ViewConfiguration.get(this), this
                    );

            if (Math.round(delta)>0) {
                mMapView.getController().setZoom(mMapView.getZoomLevelDouble()+1);
            } else {
                mMapView.getController().setZoom(mMapView.getZoomLevelDouble()-1);
            }

            //toast(String.valueOf(Math.round(delta)));
            // Swap these axes to scroll horizontally instead
            //v.scrollBy(0, Math.round(delta));

            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private void getAllCountries() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery("select * from Countries",null);

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

        for (Country country : items){
            GeoPoint c_geoPoint = new GeoPoint(Double.parseDouble(country.latitude), Double.parseDouble(country.longitude));
            startMarker = new Marker(mMapView);
            startMarker.setPosition(c_geoPoint);
            startMarker.setTitle(country.name+" "+country.emoji);
            startMarker.setIcon(getDrawable(R.drawable.location));
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mMapView.getOverlays().add(startMarker);
        }
    }

    @Override
    public void onPause() {
        //save the current location
        final SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREFS_TILE_SOURCE, mMapView.getTileProvider().getTileSource().name());
        edit.putFloat(PREFS_ORIENTATION, mMapView.getMapOrientation());
        edit.putString(PREFS_LATITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLatitude()));
        edit.putString(PREFS_LONGITUDE_STRING, String.valueOf(mMapView.getMapCenter().getLongitude()));
        edit.putFloat(PREFS_ZOOM_LEVEL_DOUBLE, (float) mMapView.getZoomLevelDouble());
        edit.apply();

        mMapView.onPause();
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        final String tileSourceName = mPrefs.getString(PREFS_TILE_SOURCE,
                TileSourceFactory.DEFAULT_TILE_SOURCE.name());
        try {
            final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
            mMapView.setTileSource(tileSource);
        } catch (final IllegalArgumentException e) {
            mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }

        mMapView.onResume();
    }

    CountryReaderDbHelper dbHelper = new CountryReaderDbHelper(MyApplication.getAppContext());
}