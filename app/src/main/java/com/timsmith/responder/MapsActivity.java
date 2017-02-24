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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

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

    private DatabaseReference mDatabase;
    private DatabaseReference mHazardDatabase;

    public ArrayList<Marker> markerList = new ArrayList<>();



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
//        loadMarker();
//        displayLocations();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Incidents");
        mHazardDatabase = FirebaseDatabase.getInstance().getReference().child("Hazards");

    }

    /**
     * Called when the map is ready.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(Burnaby));
        map.animateCamera(CameraUpdateFactory.zoomIn());
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(500);


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
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()));
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


//        displayLocations();
        showIncidents();
        showHazards();
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

//    public void loadMarker(){
//
//        mDatabaseLocations = FirebaseDatabase.getInstance().getReference().child("Incidents");
//        mDatabaseLocations.keepSynced(true);
//        mDatabaseLocations.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
//                mMap.clear();
//                for (DataSnapshot dataSnapshot1 : dataSnapshots){
//                    Blog blog = dataSnapshot1.getValue(Blog.class);
//
//
//                    Double latitude = Double.parseDouble(blog.getLatitudeText());
//                    Double longitude = Double.parseDouble(blog.getLongitudeText());
//                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void displayLocations() {
//
//        //mapsrefrence.child("testlocation").addListenerForSingleValueEvent(new ValueEventListener() {
//        mDatabase.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.hasChildren()) {
//                            @SuppressWarnings("unchecked")
//
//                            LatLngBounds bounds;
//                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                                // TODO: handle the post
//                    //Location model changed to blog
//                                Blog blog = postSnapshot.getValue(Blog.class);
//                                String slatitude = blog.getLatitudeText().toString();
//                                String slongitude = blog.getLongitudeText().toString();
//
//
//
//                                System.out.print("The Latitude " + slatitude);
//
//                                Double latitude = Double.parseDouble(slatitude);
//                                Double longitude = Double.parseDouble(slongitude);
////                                Log.i(TAG, "lat"+latitude);
//
//                                // Create LatLng for each locations
//                                LatLng mLatlng = new LatLng(latitude, longitude);
//
//                                // Make sure the map boundary contains the location
//                                builder.include(mLatlng);
//                                bounds = builder.build();
//
//                                // Add a marker for each logged location
//                                MarkerOptions mMarkerOption = new MarkerOptions()
//                                        .position(mLatlng)
//                                        .title("Incidents")
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.common_google_signin_btn_icon_light));
//                                Marker mMarker = mMap.addMarker(mMarkerOption);
//                                markerList.add(mMarker);////////////////////////////////////////////To check
//
//                                // Zoom map to the boundary that contains every logged location
////                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
////                                        MAP_ZOOM_LEVEL));
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
//                            }
//
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                    }
//                });
//    }

    public void showHazards(){
        mHazardDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChildren()) {
                    @SuppressWarnings("unchecked")

                    LatLngBounds bounds;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();


                    //Loops through all children
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // Blog newBlog = postSnapshot.getValue(Blog.class);
                        Long timestamp = (Long) dataSnapshot.child("timestamp").getValue();
                        String latitude = (String) dataSnapshot.child("latitude").getValue();
                        String longitude = (String) dataSnapshot.child("longitude").getValue();
                        String title = (String) dataSnapshot.child("title").getValue();
                        System.out.println("Hazards Latitude: " + latitude);
                        System.out.println("Hazards Longitude: " + longitude);


                        Double doubleLatitude = Double.parseDouble(latitude);
                        Double doubleLongitude = Double.parseDouble(longitude);



                        long dbTimeStamp = timestamp.longValue();//gets the timestamp from the db and converts it to a long
                        long timeStampNow = currentTimeMillis();//current timestamp

                        double timePassed = (timeStampNow - dbTimeStamp) / 1000 / 60 / 60;
                        System.out.print("Time passed result: " + timePassed);
                        if(timePassed <= 1){

//                        System.out.println("The time is " + timestamp );
//                        int secondsInADay   =0*60*24;

                        // Create LatLng for each locations
                        LatLng mLatlng = new LatLng(doubleLatitude, doubleLongitude);
//
//                                // Make sure the map boundary contains the location
                        builder.include(mLatlng);
                        bounds = builder.build();
//
//                                // Add a marker for each logged location
                        MarkerOptions mMarkerOption = new MarkerOptions()
                                .position(mLatlng)
                                .title(title);

//                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.sireni_48));
                        Marker mMarker = mMap.addMarker(mMarkerOption);
                        mMarker.showInfoWindow();//display the info of the marker
                        markerList.add(mMarker);////////////////////////////////////////////To check
//
//                                // Zoom map to the boundary that contains every logged location
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
//                                        MAP_ZOOM_LEVEL));
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                    }
                }
            }}

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showIncidents(){
        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChildren()) {
                            @SuppressWarnings("unchecked")

                            LatLngBounds bounds;
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();


                            //Loops through all children
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                               // Blog newBlog = postSnapshot.getValue(Blog.class);
                                String latitude = (String) dataSnapshot.child("latitude").getValue();
                                String longitude = (String) dataSnapshot.child("longitude").getValue();
                                System.out.println("Latitude: " + latitude);
                                System.out.println("Longitude: " + longitude);

                                Double dlatitude = Double.parseDouble(latitude);
                                Double dlongitude = Double.parseDouble(longitude);
                                // Create LatLng for each locations
                                LatLng mLatlng = new LatLng(dlatitude, dlongitude);
//
//                                // Make sure the map boundary contains the location
                                builder.include(mLatlng);
                                bounds = builder.build();
//
//                                // Add a marker for each logged location
                                MarkerOptions mMarkerOption = new MarkerOptions()
                                        .position(mLatlng)
                                        .title("Incident")
                                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.sireni_48));
                                Marker mMarker = mMap.addMarker(mMarkerOption);
                                markerList.add(mMarker);////////////////////////////////////////////To check
//
//                                // Zoom map to the boundary that contains every logged location
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
//                                        MAP_ZOOM_LEVEL));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 500));
                            }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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