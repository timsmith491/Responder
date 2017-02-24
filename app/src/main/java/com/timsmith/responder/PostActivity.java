package com.timsmith.responder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static com.timsmith.responder.R.id.spinner;


public class PostActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    private ImageButton mSelectImage;
    //private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitBtn;
    private Uri mImageUri = null;
    private ProgressDialog mProgress;
    private Spinner mSpinTitle;
    private Button mLocationButton;


    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;//stores the current user

    private DatabaseReference mDatabaseUser;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    protected GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        buildGoogleApiClient();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Incidents");


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());//gets the current user's id from the Users table




        mSelectImage = (ImageButton) findViewById(R.id.imageSelect);
        //mPostTitle = (EditText) findViewById(R.id.titleField);
        mPostDesc = (EditText) findViewById(R.id.descField);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mSpinTitle = (Spinner) findViewById(R.id.spinner);
        mLatitudeText = (TextView) findViewById(R.id.latitudeTextId);
        mLongitudeText = (TextView) findViewById(R.id.longitudeTextId);


        //mLocationButton = (Button) findViewById(R.id.locationButton);

        mProgress = new ProgressDialog(this);

        addItemsOnSpinner();

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//                v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                //final MediaPlayer mp = MediaPlayer.create(this, R.raw.sample);

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                //mSelectImage.setImageBitmap(ImageBitmap.decodeSampledBitmapFromResource(getResources(), R.id.incidentImage, 200, 200));//Added 24/01
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Double lat = mLastLocation.getLatitude();
            Double lon = mLastLocation.getLongitude();
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            Toast.makeText(this, "Location Received", Toast.LENGTH_LONG).show();
        } else {
        }
    }


    public void addItemsOnSpinner() {

        mSpinTitle = (Spinner) findViewById(spinner);
        List<String> list = new ArrayList<String>();
        list.add("Medical");
        list.add("Mechanical");
        list.add("Other");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinTitle.setAdapter(dataAdapter);
    }

    public void startPosting(){
        mProgress.setMessage("Posting to Feed");


        //final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();
        final String title_val = mSpinTitle.getSelectedItem().toString().trim();
        final String latitude = mLatitudeText.getText().toString().trim();
        final String longitude = mLongitudeText.getText().toString().trim();
        //Spinner item to be set as the title

        //All fields must be populated
        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null){

            mProgress.show();

            StorageReference filepath = mStorage.child("Incident Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUri = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mDatabase.push();



                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("title").setValue(title_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("image").setValue((downloadUri.toString()));
                            newPost.child("latitude").setValue(latitude);
                            newPost.child("longitude").setValue(longitude);
                            newPost.child("uid").setValue(mCurrentUser.getUid());

//                            Map<String,Object> checkoutData=new HashMap<>();
//                            checkoutData.put("time",ServerValue.TIMESTAMP);
//                            newPost.child("timestamp").setValue(checkoutData);

                            //newPost.child("timestamp").setValue(time_stamp, ServerValue.TIMESTAMP);//Timestamp

                            //gets the current user//below uses the user id to get the username from the databse snapshot
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(PostActivity.this, MainActivity.class));
                                            }
                                        }
                                    });

                            //adds the users phone to the incident CHECK
                            newPost.child("phone").setValue(dataSnapshot.child("phone").getValue())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                                    }
                                }
                            });

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                   // mProgress.dismiss();startActivity(new Intent(PostActivity.this, MainActivity.class));
                        }
                    });
                }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();

            mSelectImage.setImageURI(mImageUri);

        }

    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
