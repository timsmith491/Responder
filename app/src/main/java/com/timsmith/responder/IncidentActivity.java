package com.timsmith.responder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class IncidentActivity extends AppCompatActivity {

    private String mIncidentKey = null;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ImageView mIncidentDetailImage;
    private TextView mIncidentDetailTitle;
    private TextView mIncidentDetailDesc;
    private TextView mIncidentDetailUsername;
    private ImageButton mIncidentDetailPhone;
    private Button mIncidentRemove;
    private Button mIncidentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);

        mAuth = FirebaseAuth.getInstance();//gets user

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Incidents");

        mIncidentKey = getIntent().getExtras().getString("incident_id");
        //Toast.makeText(IncidentActivity.this, incidentKey, Toast.LENGTH_LONG).show();
        //mDatabase.keepSynced(true);

        mIncidentDetailImage = (ImageView) findViewById(R.id.incidentImage);
        mIncidentDetailTitle = (TextView) findViewById(R.id.incidentTitle);
        mIncidentDetailDesc = (TextView) findViewById(R.id.incidentDescription);
        mIncidentDetailUsername = (TextView) findViewById(R.id.incidentUsername);
        mIncidentDetailPhone = (ImageButton) findViewById(R.id.incidentPhoneButton);
        mIncidentRemove = (Button) findViewById(R.id.deleteIncidentBtn);
        mIncidentLocation = (Button) findViewById(R.id.incidentLocationMap);


        mIncidentDetailDesc.setMovementMethod(new ScrollingMovementMethod());//allows the description to scroll

        mDatabase.child(mIncidentKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String incidentTitle = (String) dataSnapshot.child("title").getValue();
                String incidentDesc = (String) dataSnapshot.child("desc").getValue();
                String incidentImage = (String) dataSnapshot.child("image").getValue();
                String incidentUid = (String) dataSnapshot.child("uid").getValue();
                String incidentUsername = (String) dataSnapshot.child("username").getValue();
                final String incidentPhone = (String) dataSnapshot.child("phone").getValue();
                final String incidentLatitude = (String) dataSnapshot.child("latitude").getValue();
                final String incidentLongitude = (String) dataSnapshot.child("longitude").getValue();

                mIncidentDetailTitle.setText(incidentTitle);
                mIncidentDetailDesc.setText(incidentDesc);
                Picasso.with(IncidentActivity.this).load(incidentImage).into(mIncidentDetailImage);
//                Picasso.with(IncidentActivity.this).load(incidentImage).into(mIncidentDetailImage.setImageBitmap(
//                        ImageBitmap.decodeSampledBitmapFromResource(getResources(), R.id.incidentImage, 100, 100))

                mIncidentDetailUsername.setText(incidentUsername);
                //mIncidentDetailImage.setImageBitmap(ImageBitmap.decodeSampledBitmapFromResource(getApplicationContext().getResources(), 1, 100, 100));

                if (mAuth.getCurrentUser().getUid().equals(incidentUid)) {//The user that created the incident can only delete the incident
                    mIncidentRemove.setVisibility(View.VISIBLE);
                    mIncidentDetailPhone.setVisibility(View.INVISIBLE);//Hides the phone button as logic says they cant call themselves
                }

                mIncidentLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Uri gmmIntentUri = Uri.parse("google.navigation:q=Taronga+Zoo,+Sydney+Australia");
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + incidentLatitude + "," + incidentLongitude + "&mode=b");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });


                //Allows a user to call the person in need. (The owner of the activity)
                mIncidentDetailPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + incidentPhone));
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e("Responder", "Failed to invoke call", e);
                        }
                    }
                });

            }


//            //not working at the moment phone function
//            mIncidentDetailPhone.setOnClick(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                    Intent callIntent = new Intent(Intent.ACTION_CALL);
//                    String number = "2125551212";
//
//                    callIntent.setData(Uri.parse("test:" + number));
//                    Toast.makeText(IncidentActivity.this, "click", Toast.LENGTH_LONG).show();
//                    //callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                    if (callIntent.resolveActivity(getPackageManager()) != null) {
//                        startActivity(callIntent);
//
//                        //http://stackoverflow.com/questions/41342880/android-phone-call-not-working
//
//                    }
//                }
//            });


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Allows the user who owns the incident to delete it
        mIncidentRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mIncidentKey).removeValue();
                Intent intent = new Intent(IncidentActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
