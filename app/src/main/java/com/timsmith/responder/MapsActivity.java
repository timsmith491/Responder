package com.timsmith.responder;


import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private static final LatLng Burnaby = new LatLng(53.144116, -6.061681);
    private static final LatLng DannsPub = new LatLng(53.150062, -6.066326);
    private static final LatLng TheatreLane = new LatLng(53.144301, -6.064360);
    private static final LatLng DonnybrookFair = new LatLng(53.144566, -6.077875);
    private static final LatLng SuperValueCharlesland = new LatLng(53.126573, -6.063872);
    private static final LatLng StKilianFamCentre = new LatLng(53.146466, -6.062501);
    private static final LatLng StPatricksChurch = new LatLng(53.147923, -6.070842);
    private static final LatLng ShorelineLeisure = new LatLng(53.136106, -6.065137);
    private static final LatLng GreystonesTennisClub = new LatLng(53.132663, -6.069814);
    private static final LatLng SkatePark = new LatLng(53.121336, -6.065016);
    private static final LatLng CharlselandGolfClub = new LatLng(53.130687, -6.055709);

    private Marker mBurnaby;
    private Marker mDannsPub;
    private Marker mTheatreLane;
    private Marker mDonnybrookFair;
    private Marker mSuperValueCharlesland;
    private Marker mStKilianFamCentre;
    private Marker mStPatricksChurch;
    private Marker mShorelineLeisure;
    private Marker mGreystonesTennisClub;
    private Marker mSkatePark;
    private Marker mCharlselandGolfClub;


    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Called when the map is ready.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(Burnaby));
        //map.animateCamera(CameraUpdateFactory.zoomIn());
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        map.animateCamera(zoom);
        // Add some markers to the map, and add a data object to each marker.
        mBurnaby = mMap.addMarker(new MarkerOptions()
                .position(Burnaby)
                .title("Burnaby"));
        mBurnaby.setTag(0);

        mDannsPub = mMap.addMarker(new MarkerOptions()
                .position(DannsPub)
                .title("Harbour"));
        mDannsPub.setTag(0);

        mTheatreLane = mMap.addMarker(new MarkerOptions()
                .position(TheatreLane)
                .title("Theatre Lane"));
        mTheatreLane.setTag(0);

        mDonnybrookFair = mMap.addMarker(new MarkerOptions()
                .position(DonnybrookFair)
                .title("Donnybrook Fair"));
        mDonnybrookFair.setTag(0);

        mSuperValueCharlesland = mMap.addMarker(new MarkerOptions()
                .position(SuperValueCharlesland)
                .title("SuperValue Charlesland"));
        mSuperValueCharlesland.setTag(0);

        mStKilianFamCentre = mMap.addMarker(new MarkerOptions()
                .position(StKilianFamCentre)
                .title("St Kilian Family Centre"));
        mStKilianFamCentre.setTag(0);

        mStPatricksChurch = mMap.addMarker(new MarkerOptions()
                .position(StPatricksChurch)
                .title("St Patrick's Church Centre"));
        mStPatricksChurch.setTag(0);

        mShorelineLeisure = mMap.addMarker(new MarkerOptions()
                .position(ShorelineLeisure)
                .title("Shoreline Leisure Centre"));
        mShorelineLeisure.setTag(0);

        mGreystonesTennisClub = mMap.addMarker(new MarkerOptions()
                .position(GreystonesTennisClub)
                .title("Greystones Tennis Club"));
        mGreystonesTennisClub.setTag(0);

        mSkatePark = mMap.addMarker(new MarkerOptions()
                .position(SkatePark)
                .title("Skate Park"));
        mSkatePark.setTag(0);

        mCharlselandGolfClub = mMap.addMarker(new MarkerOptions()
                .position(CharlselandGolfClub)
                .title("Charlseland Golf Club"));
        mCharlselandGolfClub.setTag(0);

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}