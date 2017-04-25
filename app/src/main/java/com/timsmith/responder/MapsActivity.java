package com.timsmith.responder;


import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.R.attr.key;
import static com.timsmith.responder.GeoFence.Constants.GEOFENCE_RADIUS_IN_METERS;
import static com.timsmith.responder.R.id.map;
import static com.timsmith.responder.R.id.mapIncidentPic;
import static com.timsmith.responder.R.layout.map_incident_info;
import static java.lang.System.currentTimeMillis;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback,GoogleMap.OnInfoWindowCloseListener, GoogleMap.OnInfoWindowClickListener {


    //Pins showing all defibs in greystones
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
    private static final String TAG = MapsActivity.class.getSimpleName();

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

    //Arraylist holds all the markers of incidents and hazards
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
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
//        loadMarker();
//        displayLocations();

        //Connects to the databases
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

//
//        if (mMap != null) {
//            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                @Override
//                public View getInfoWindow(Marker marker) {
//                    return null;
//                }
//
//                @Override
//                public View getInfoContents(final Marker marker) {
//                    View v = View.inflate(getApplicationContext(), R.layout.map_incident_info, null);
//
//                    ImageView imageView = (ImageView) v.findViewById(R.id.mapIncidentPic);
//                    final TextView incidentTitleText = (TextView) v.findViewById(R.id.mapIncidentTitle);
//                    TextView incidentLocation = (TextView) v.findViewById(R.id.mapIncidentLocation);
//
////
////                    mDatabase.addChildEventListener(new ChildEventListener() {
////                        @Override
////                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
////                            if (dataSnapshot.hasChildren()) {
////
////                                //Loops through all children
////                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
////                                    String incidentTitle = (String) dataSnapshot.child("title").getValue();
////                                    incidentTitleText.setText(incidentTitle);
////                                }
////                            }
////                        }
////
////                        @Override
////                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
////
////                        }
////
////                        @Override
////                        public void onChildRemoved(DataSnapshot dataSnapshot) {
////
////                        }
////
////                        @Override
////                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
////
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////
////                        }
////                    });
//
//
//
//                    return v;
//                }
//            });
//            mMap.setOnInfoWindowClickListener (this);
//
//        }

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


    private Marker mHazardMarker;
    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;

    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if (geoFenceLimits != null)
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center(mHazardMarker.getPosition())
                .strokeColor(Color.argb(50, 70, 70, 70))
                .fillColor(Color.argb(100, 150, 150, 150))
                .radius(GEOFENCE_RADIUS_IN_METERS);
        geoFenceLimits = mMap.addCircle(circleOptions);
    }


    public void showHazards() {
        mHazardDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChildren()) {
                    @SuppressWarnings("unchecked")
                    LatLngBounds bounds;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();


                    //Loops through all children
                    for (DataSnapshot p : dataSnapshot.getChildren()) {
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

                        if (timePassed <= 1) {
                            // Create LatLng for each locations
                            LatLng mLatlng = new LatLng(doubleLatitude, doubleLongitude);
//
//                      // Make sure the map boundary contains the location
                            builder.include(mLatlng);
                            bounds = builder.build();
//
//                      // Add a marker for each logged location
                            MarkerOptions mMarkerOption = new MarkerOptions()
                                    .position(mLatlng)
                                    .title(title);

                            mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(doubleLatitude, doubleLongitude))
                                    .radius(GEOFENCE_RADIUS_IN_METERS)
                                    .strokeColor(Color.BLACK)
                                    .strokeWidth(2)
                                    .fillColor(0x10EF4E49));

//                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.sireni_48));
                            Marker mMarker = mMap.addMarker(mMarkerOption);
                            mMarker.showInfoWindow();//display the info of the marker
                            markerList.add(mMarker);////////////////////////////////////////////To check

                            // Adds hazard location to the geofence Hashmap
//                            GEOFENCE_LANDMARKS.put("Hazard", new LatLng(doubleLatitude, doubleLongitude));
//                            System.out.println(GEOFENCE_LANDMARKS.toString() + "Hazard geofence");
//                            MainActivity.populateGeofenceList();
//                                // Zoom map to the boundary that contains every logged location
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
//                                        MAP_ZOOM_LEVEL));
//                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                        }
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

    public void showIncidents() {

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChildren()) {
                    @SuppressWarnings("unchecked")

                    LatLngBounds bounds;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();


                    //Loops through all children
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // Blog newBlog = postSnapshot.getValue(Blog.class);
                        String latitude = (String) dataSnapshot.child("latitude").getValue();
                        String longitude = (String) dataSnapshot.child("longitude").getValue();
                        final String title = (String) dataSnapshot.child("title").getValue();
                        final String usernameIncident = (String) dataSnapshot.child("username").getValue();
                        final String imageIncident = (String) dataSnapshot.child("image").getValue();

//                        String key = postSnapshot.getValue().
//                        String key = postSnapshot.aVC.getParent().getKey();

//                        postSnapshot.aVB.getParent().getKey();
                        final String myParentNode = dataSnapshot.getKey();
                        System.out.print("Parent key" + myParentNode);


                        System.out.print("Object key" + key);

                        System.out.println("Latitude: " + latitude);
                        System.out.println("Longitude: " + longitude);
                        System.out.println("Incident Title: " + title);
//                        Picasso.with(MapsActivity.this).load(imageIncident).into(mapIncidentPic);

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
                                .title("Incident: "+ title)
                                .snippet("username: " + usernameIncident + " imageurl$ " + imageIncident + " Intent:£" + myParentNode)
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.sireni_48));
                        Marker mMarker = mMap.addMarker(mMarkerOption);

//                                createGeofence(mLatlng, 100);


                        markerList.add(mMarker);////////////////////////////////////////////To check
//
//                                // Zoom map to the boundary that contains every logged location
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
//                                        MAP_ZOOM_LEVEL));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 500));

                        if (mMap != null)
                            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                @Override
                                public View getInfoWindow(Marker marker) {

                                    return null;
                                }

                                @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
                                @Override
                                public View getInfoContents(final Marker marker) {
                                    View v = View.inflate(getApplicationContext(), map_incident_info, null);

                                    ImageView imageView = (ImageView) v.findViewById(mapIncidentPic);
                                    TextView incidentTitleText = (TextView) v.findViewById(R.id.mapIncidentTitle);
                                    TextView incidentUsernameText = (TextView) v.findViewById(R.id.mapIncidentLocation);
                                    Button incidentIntentButton = (Button) v.findViewById(R.id.mapIncidentButton);
//                                    ArrayList listOfMarkers = (ArrayList) dataSnapshot.getChildren();


                                    incidentTitleText.setText(marker.getTitle());
                                    incidentUsernameText.setText(marker.getSnippet());

                                    String example = "/abc/def/ghfj.doc";
                                    System.out.println(example.substring(example.lastIndexOf("/") + 1));

                                    String imageString = marker.getSnippet().substring(marker.getSnippet().lastIndexOf("$") + 2);
                                    System.out.print(imageString);

                                    final String intentKey = marker.getSnippet().substring(marker.getSnippet().indexOf("£") + 1);
                                    System.out.print("IntentKey " + intentKey);

//                                    String myParentNode = marker.getSnippet().substring(marker.getSnippet().indexOf("£") +3);
//                                    System.out.print(myParentNode);

                                    Picasso.with(MapsActivity.this).load(imageString).into(imageView);
//                                    Picasso.with(ctx).load(imageString).into(imageView);

//                                    incidentIntentButton.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Intent incidentIntent = new Intent(MapsActivity.this, IncidentActivity.class);
//                                            incidentIntent.putExtra("incident_id", myParentNode);//incident key is the id of the incident in the list
//                                            System.out.print("Intent Parent node" + myParentNode);
//                                            startActivity(incidentIntent);
//                                        }
//                                    });

//                                    v.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            //Toast.makeText(MainActivity.this, incidentKey, Toast.LENGTH_LONG).show();
//                                            Intent incidentIntent = new Intent(MapsActivity.this, IncidentActivity.class);
//                                            incidentIntent.putExtra("incident_id", intentKey);//incident key is the id of the incident in the list
//                                            System.out.print("Intent Parent node" + intentKey);
//                                            startActivity(incidentIntent);
//                                        }
//                                    });


                                    return v;

                                }
                            });
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
        mMap.setOnInfoWindowClickListener(this);

    }
    //////////////////////////////              GEOFENCE              ///////////////////////////////////////////////////////////////


//    private static final long GEO_DURATION = 60 * 60 * 1000;
//    private static final String GEOFENCE_REQ_ID = "My Geofence";
//    private static final float GEOFENCE_RADIUS = 500.0f; // in meters
//
//    // Create a Geofence
//    private Geofence createGeofence(LatLng latLng, float radius ) {
//        Log.d(TAG, "createGeofence");
//        return new Geofence.Builder()
//                .setRequestId(GEOFENCE_REQ_ID)
//                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
//                .setExpirationDuration( GEO_DURATION )
//                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
//                        | Geofence.GEOFENCE_TRANSITION_EXIT )
//                .build();
//    }
//
//    //Geofence Request
//    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
//        Log.d(TAG, "createGeofenceRequest");
//        return new GeofencingRequest.Builder()
//                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
//                .addGeofence( geofence )
//                .build();
//    }
//
//    private PendingIntent geoFencePendingIntent;
//    private final int GEOFENCE_REQ_CODE = 0;
//    private PendingIntent createGeofencePendingIntent() {
//        Log.d(TAG, "createGeofencePendingIntent");
//        if ( geoFencePendingIntent != null )
//            return geoFencePendingIntent;
//
//        Intent intent = new Intent( this, GeofenceTrasitionService.class);
//        return PendingIntent.getService(
//                this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
//    }
//
//    // Add the created GeofenceRequest to the device's monitoring list
//    private void addGeofence(GeofencingRequest request) {
//        Log.d(TAG, "addGeofence");
//        if (checkPermission())
//            LocationServices.GeofencingApi.addGeofences(
//                    client,
//                    request,
//                    createGeofencePendingIntent()
//            ).setResultCallback(this);
//    }


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


    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onInfoWindowClose(Marker marker) {

    }
}