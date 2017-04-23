package com.timsmith.responder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.view.View.VISIBLE;


public class ProfileActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseUserPosts;
    private FirebaseAuth mAuth;
    private Query mUserQuery;
    private RecyclerView mUserIncidentList;//All of the users incidents


    private String mUserKey = null; //set to null until until the user is brought to the activity

    private ImageView mProfilePicture;
    private ImageButton mPhoneNumber;
    private ImageView mUpdateProfile;
    private TextView mUsername, mDob;
    private ImageView mProfileDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        mUserKey = getIntent().getExtras().getString("user_id");
        Toast.makeText(ProfileActivity.this, mUserKey, Toast.LENGTH_LONG).show();



        //String currentUserId = mAuth.getCurrentUser().getUid();
        //Reference to the incident table to retrieve the users incidents they own
        mDatabaseUserPosts = FirebaseDatabase.getInstance().getReference().child("Incidents");
        mUserQuery = mDatabaseUserPosts.orderByChild("uid").equalTo(mUserKey);


        //reference to the recycler view
        mUserIncidentList = (RecyclerView) findViewById(R.id.profileIncidentList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mUserIncidentList.setHasFixedSize(true);
        mUserIncidentList.setLayoutManager(layoutManager);

        FirebaseRecyclerAdapter<Blog, UserIncidentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, UserIncidentViewHolder>(
                Blog.class,
                R.layout.incident_row,
                UserIncidentViewHolder.class,
                mUserQuery
        ) {
            @Override
            protected void populateViewHolder(UserIncidentViewHolder viewHolder, Blog model, int position) {
                final String incidentKey = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setUsername((model.getUsername()));
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MainActivity.this, incidentKey, Toast.LENGTH_LONG).show();
                        Intent incidentIntent = new Intent(ProfileActivity.this, IncidentActivity.class);
                        incidentIntent.putExtra("incident_id", incidentKey);//incident key is the id of the incident in the list
                        startActivity(incidentIntent);
                    }
                });
            }

        };
        mUserIncidentList.setAdapter(firebaseRecyclerAdapter);


        //Reference to the logged in users data object
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");


//        String currentUserId = mAuth.getCurrentUser().getUid();
//        //Reference to the incident table to retrieve the users incidents they own
//        mDatabaseUserPosts = FirebaseDatabase.getInstance().getReference().child("Incidents");
//        mUserQuery= mDatabaseUserPosts.orderByChild("uid").equalTo(currentUserId);


//        mUserKey = getIntent().getExtras().getString("user_id");
//        Toast.makeText(ProfileActivity.this, mUserKey, Toast.LENGTH_LONG).show();


        mProfilePicture = (ImageView) findViewById(R.id.user_profile_photo);
        mPhoneNumber = (ImageButton) findViewById(R.id.user_phone_number);
        mUsername = (TextView) findViewById(R.id.user_profile_name);
        mDob = (TextView) findViewById(R.id.user_profile_dob);
        mUpdateProfile = (ImageView) findViewById(R.id.drop_down_option_menu);
        mProfileDeleteButton = (ImageView) findViewById(R.id.delete_user);

        if (mAuth.getCurrentUser().getUid().equals(mUserKey)) {//The user that created the incident can only delete the incident
            mUpdateProfile.setVisibility(VISIBLE);
            mProfileDeleteButton.setVisibility(VISIBLE);
        }

        //Retrieves the users data from the database
        mDatabaseUser.child(mUserKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_name = (String) dataSnapshot.child("name").getValue();
                final String phone_number = (String) dataSnapshot.child("phone").getValue();
                String date_birth = (String) dataSnapshot.child("dob").getValue();
                String profile_picture = (String) dataSnapshot.child("image").getValue();

                mUsername.setText(user_name);
                mDob.setText(date_birth);
                Picasso.with(ProfileActivity.this).load(profile_picture).into(mProfilePicture);

                //Allows a user to call the person(The owner of the profile)
                mPhoneNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + phone_number));
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
                mUpdateProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfile();
                    }
                });
                mProfileDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProfile();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public static class UserIncidentViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UserIncidentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
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
    }

    public void updateProfile(){

//        mAuth.getCurrentUser().getUid();

//        String newPhone = "08722222";
//        mDatabaseUser.child(user_id).child("phone").setValue(newPhone);

        Intent mainIntent = new Intent(ProfileActivity.this, SetupActivity.class);
        startActivity(mainIntent);
    }

    //delete a use
    public void deleteProfile(){
        final String user_id = mAuth.getCurrentUser().getUid();
//        // Method to Remove Account Option
//
        mDatabaseUser.child(user_id).removeValue();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);



    }
}