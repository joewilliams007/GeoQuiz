package com.dev.geoquizworld;

import static com.dev.geoquizworld.MainActivity.toast;
import static com.dev.geoquizworld.MainActivity.vibrate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.dev.geoquizworld.animations.Tools;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class LocateMapActivity extends Activity {
    Intent intent;
    GeoPoint loc;
    TextView top;

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
    private RotationGestureOverlay mRotationGestureOverlay;
    private static final int MENU_ABOUT = Menu.FIRST + 1;

    Boolean selectedPoint = false;

    String name, longitude, latitude, area;
    Marker startMarker, correctMarker;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tools.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_map);
        top = findViewById(R.id.top);
        intent = getIntent();
        selectedPoint = false;
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        area = intent.getStringExtra("area");
        mMapView = findViewById(R.id.map);
        mMapView.setDestroyMode(false);
        mMapView.setTag("mapView"); // needed for OpenStreetMapViewTest
        loc = null;
        final Context context = LocateMapActivity.this;
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

        name = intent.getStringExtra("name");
        touchOverlay = new Overlay() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {

                Projection proj = mapView.getProjection();
                proj = mapView.getProjection();
                loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());

                mMapView.getOverlays().remove(startMarker);
                GeoPoint startPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
                selectedPoint = true;
                startMarker = new Marker(mMapView);
                startMarker.setPosition(startPoint);
                startMarker.setTitle("My guess");
                startMarker.setSubDescription("i guess "+name+" is here");
                startMarker.setIcon(getDrawable(R.drawable.location));
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mMapView.getOverlays().add(startMarker);
                return true;
            }
        };
        mMapView.getOverlays().add(this.touchOverlay);
    }

    Boolean isSubmit = false;
    public static Boolean next = false;
    public void submit(View view) {
        vibrate();
        if (!selectedPoint) {
            toast("select point");
            return;
        }
        if (isSubmit) {
            next = true;
            finish();
        } else {
            isSubmit = true;
            mMapView.getOverlays().remove(this.touchOverlay);
            double distanceInMeters = calculateDistanceInMeters(Double.parseDouble(latitude),Double.parseDouble(longitude),loc.getLatitude(),loc.getLongitude());
            String distanceInKm = String.valueOf((distanceInMeters/1000)).split("\\.")[0];
            int distanceInKmInt = Integer.parseInt(distanceInKm);
            // toast("distance "+distanceInKm+" km still in range "+(Integer.parseInt(distanceInKm)-Math.sqrt(Integer.parseInt(area))));
            String inArea = "no";
            if (Integer.parseInt(distanceInKm)-Math.sqrt(Integer.parseInt(area))<0) {
                inArea = "yes";
            }
            Account.upGuessesLoc();
            int score;
            if (inArea.equals("yes")) {
                score = 10;
                updateDB(true, name);
            } else if (distanceInKmInt<500)  {
                updateDB(false, name);
                score = 7;
            } else if (distanceInKmInt<1000)  {
                updateDB(false, name);
                score = 5;
            } else if (distanceInKmInt<2000)  {
                updateDB(false, name);
                score = 2;
            } else {
                updateDB(false, name);
                score = 0;
            }


            if (score>0) {
                Account.upScoreLoc(score);
            } else {
                Account.resetScoreLoc();
            }

            String text = "R E S U L T"
                    +"\ndistance: "+distanceInKm+" km"
                    +"\nin country area: "+inArea
                    +"\npoints: +"+score
                    +"\ntap to continue";

            top.setText(text);

            GeoPoint correctPoint = new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude));
            correctMarker = new Marker(mMapView);
            correctMarker.setPosition(correctPoint);
            correctMarker.setTitle(name);
            correctMarker.setIcon(getDrawable(R.drawable.location_correct));
            correctMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mMapView.getOverlays().add(correctMarker);

            mMapView.setExpectedCenter(new GeoPoint(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        }

    }
    CountryReaderDbHelper dbHelper = new CountryReaderDbHelper(MyApplication.getAppContext());
    private void updateDB(Boolean correct, String correctCountryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String strSQL;
        if (correct) {
            strSQL = "UPDATE Countries SET loc_usages = loc_usages+1,loc_streak=loc_streak+1,loc_won=loc_won+1 WHERE name='" + correctCountryName + "'";
        } else {
            strSQL = "UPDATE Countries SET loc_usages = loc_usages+1,loc_streak=0,loc_lost=loc_lost+1 WHERE name='" + correctCountryName + "'";
        }
        db.execSQL(strSQL);
    }

    public double calculateDistanceInMeters(double lat1, double long1, double lat2,
                                            double long2) {

        return org.apache.lucene.util.SloppyMath.haversinMeters(lat1, long1, lat2, long2);
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
}