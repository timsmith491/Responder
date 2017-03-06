package com.timsmith.responder;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.timsmith.responder.GeoFence.Constants;
import com.timsmith.responder.GeoFence.GeofenceErrorMessages;
import com.timsmith.responder.GeoFence.GeofenceTransitionsIntentService;

import java.util.ArrayList;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {


    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseReactions;
    private DatabaseReference mDatabaseHazard;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mCurrentUser;//stores the current user
    private DatabaseReference mDatabaseUser;


    private GoogleApiClient mGoogleApiClient;/////////////////////////////////////////////////////////////////////
    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;
    // Buttons for kicking off the process of adding or removing geofences.
    private Button mAddGeofencesButton;
    private Button mRemoveGeofencesButton;


    private static final String TAG = "MainActivity";

    private FloatingActionButton mHazardButton;
    private boolean mReactionService = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);//added 12/1
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        String token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Message token! "+token);

        //Mapping
        // Create an instance of GoogleAPIClient.///////////////////////////////////////////////////////////////////////
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//stops user going back
                    startActivity(loginIntent);
                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Incidents");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReactions = FirebaseDatabase.getInstance().getReference().child("Reactions");

        mDatabaseReactions.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);

        mBlogList = (RecyclerView) findViewById(R.id.incident_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);


        hazardFunction();
        checkUserExist();

        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
        mRemoveGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);
        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();
        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);
        setButtonsEnabledState();

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();


    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }
    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            setButtonsEnabledState();

            Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }
    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.GEOFENCE_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }

    /**
     * Ensures that only one button is enabled at any time. The Add Geofences button is enabled
     * if the user hasn't yet added geofences. The Remove Geofences button is enabled if the
     * user has added geofences.
     */
    private void setButtonsEnabledState() {
        if (mGeofencesAdded) {
            mAddGeofencesButton.setEnabled(false);
            mRemoveGeofencesButton.setEnabled(true);
        } else {
            mAddGeofencesButton.setEnabled(true);
            mRemoveGeofencesButton.setEnabled(false);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        //Google Location////////////////////////////////////////////////////////////////////////////////////////////////
        mGoogleApiClient.connect();

        mAuth.addAuthStateListener(mAuthListener);//sets the authentication listener on

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.incident_row,
                BlogViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, final int position) {
                final String incidentKey = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setUsername((model.getUsername()));
                viewHolder.setImage(getApplicationContext(), model.getImage());
                //viewHolder.setTimestamp(model.getTimestamp());
                viewHolder.setReactionButton(incidentKey);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MainActivity.this, incidentKey, Toast.LENGTH_LONG).show();
                        Intent incidentIntent = new Intent(MainActivity.this, IncidentActivity.class);
                        incidentIntent.putExtra("incident_id", incidentKey);//incident key is the id of the incident in the list
                        startActivity(incidentIntent);
                    }
                });

                //Responder OnClickListener to attach a responder to the incident
                viewHolder.mReactionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialogDemo();
                        mReactionService = true;

                        final DatabaseReference newPost = mDatabaseReactions.push();

                        mDatabaseReactions.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (mReactionService) {
                                    //below checks to see if the user has clicked responding to the incident
                                    if (dataSnapshot.child(incidentKey).hasChild(mAuth.getCurrentUser().getUid())) {

                                        //Removes the user from responding and sets reactionService to false
                                        mDatabaseReactions.child(incidentKey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        mReactionService = false;

                                    } else {

//                                        newPost.child("uid").setValue(mCurrentUser.getUid());//gets the current user//below uses the user id to get the username from the databse snapshot
//                                        newPost.child("username").setValue(dataSnapshot.child("name").getValue())
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if(task.isSuccessful()){
//                                                            startActivity(new Intent(MainActivity.this, MainActivity.class));
//                                                        }
//                                                    }
//                                                });
                                        mDatabaseReactions.child(incidentKey).child(mAuth.getCurrentUser().getUid()).setValue("UserResponding");
                                        mReactionService = false;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }
        };
        //adds users profile picture
        // Get userimage url.
//                mDatabaseUsers.child(model.getUid()).child("image").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        String imageUrl = dataSnapshot.getValue().toString();
//                        viewHolder.setUserimage(getApplicationContext(), imageUrl);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public void hazardFunction(){

        //Gets the location of the Hazards table
        mDatabaseHazard = FirebaseDatabase.getInstance().getReference().child("Hazards");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();//Current user that is logged in
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());//Gets current users UID


        mHazardButton = (FloatingActionButton) findViewById(R.id.floatHazard);
        mHazardButton.bringToFront();
        mHazardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
//                Toast.makeText(MainActivity.this, "Hazard Clicked", Toast.LENGTH_LONG);


                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                View promptView = layoutInflater.inflate(R.layout.hazard_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);



                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                resultText.setText("Hello, " + editText.getText());
                                final String title = editText.getText().toString().trim();
//                                final Long timeStamp = SystemClock.uptimeMillis();
                                final long timeStamp = currentTimeMillis();

//                                final Long timestamp = new Timestamp(System.currentTimeMillis());
//                                final Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
//                                final Long timeStamp = new Firebase.

                                final DatabaseReference newHazard = mDatabaseHazard.push();
                                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        newHazard.child("title").setValue(title);
                                        newHazard.child("latitude").setValue(mLatitudeText);
                                        newHazard.child("longitude").setValue(mLongitudeText);
                                        newHazard.child("uid").setValue(mCurrentUser.getUid());
                                        //gets the current user//below uses the user id to get the username from the databse snapshot
                                        newHazard.child("username").setValue(dataSnapshot.child("name").getValue())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                           // startActivity(new Intent(MainActivity.this, MainActivity.class));
                                                        }
                                                    }
                                                });
                                        newHazard.child("timestamp").setValue(timeStamp);
                                        Snackbar.make(v, "Hazard Logged", Snackbar.LENGTH_LONG).setAction("Action", null).setDuration(3500).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

            }
        });
    }



    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//stops user going back
                        startActivity(setupIntent);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }




    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton mReactionButton;
        DatabaseReference mDatabaseReactions;
        FirebaseAuth mAuth;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mReactionButton = (ImageButton) mView.findViewById(R.id.reaction_button);
            mDatabaseReactions = FirebaseDatabase.getInstance().getReference().child("Reactions");
            mAuth = FirebaseAuth.getInstance();
            mDatabaseReactions.keepSynced(true);
        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String desc) {
            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setUsername(String username) {
            TextView post_username = (TextView) mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }


        public void setImage(Context ctx, String image) {
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
            //mSelectImage.setImageBitmap(ImageBitmap.decodeSampledBitmapFromResource(getResources(), R.id.incidentImage, 200, 200));


        }

        public void setReactionButton(final String incidentKey) {
            //Changes the responding button image from on state to another depending on the user signed in
            mDatabaseReactions.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(incidentKey).hasChild(mAuth.getCurrentUser().getUid())) {
                        mReactionButton.setImageResource(R.mipmap.ic_error_outline_white_36dp);
                    } else {
                        mReactionButton.setImageResource(R.mipmap.ic_error_white_36dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


//        public void setTimestamp(Long timestamp){
//            TextView time_stamp = (TextView) mView.findViewById(R.id.time_stamp);
//            time_stamp.setText(timestamp);
//        }

        //set user
//        public void setUserimage(Context context, String imageUrl) {
//            ImageView imageViewPostUserImage = (ImageView) mView.findViewById(R.id.imageview_post_userimage);
//            Picasso.with(context).load(imageUrl).fit().into(imageViewPostUserImage);
//        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }

        if (item.getItemId() == R.id.action_logout) {
            logout();
        }
        if (item.getItemId() == R.id.action_map) {
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        }
        if (item.getItemId() == R.id.action_video) {
            startActivity(new Intent(MainActivity.this, Video.class));
        }
        if (item.getItemId() == R.id.action_allUser) {
            startActivity(new Intent(MainActivity.this, ListUserActivity.class));
        }
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
        FirebaseAuth.getInstance().signOut();
    }

    private void confirmDialogDemo() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Response has been sent");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        String msg = "news";
        Log.d(TAG, msg);
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private Location mLastLocation;
    private String mLatitudeText;
    private String mLongitudeText;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;

    @Override
    public void onConnected(Bundle bundle) {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSION_LOCATION_REQUEST_CODE);
//            return;
//        }

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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


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
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);
                    if (mLastLocation != null) {
                        mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                        mLongitudeText = String.valueOf(mLastLocation.getLongitude());
                    }

                } else {
                    Toast.makeText(this, "Location must be enabled", Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
