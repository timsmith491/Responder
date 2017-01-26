package com.timsmith.responder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseReactions;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private GoogleApiClient mGoogleApiClient;/////////////////////////////////////////////////////////////////////


    private boolean mReactionService = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);//added 12/1
        }


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

        checkUserExist();

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
                        mReactionService = true;
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
                                        mDatabaseReactions.child(incidentKey).child(mAuth.getCurrentUser().getUid()).setValue("UserResponding");
                                        //.setValue((dataSnapshot.child("name").getValue())); //Allows name to be added to the responder button
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
            //Changes the responding button image from on state to another
            mDatabaseReactions.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(incidentKey).hasChild(mAuth.getCurrentUser().getUid())) {
                        mReactionButton.setImageResource(R.mipmap.ic_error_outline_white_24dp);
                    } else {
                        mReactionButton.setImageResource(R.mipmap.ic_error_white_24dp);
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


        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
        FirebaseAuth.getInstance().signOut();
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
