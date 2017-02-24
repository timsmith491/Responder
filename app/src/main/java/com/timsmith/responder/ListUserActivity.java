package com.timsmith.responder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ListUserActivity extends AppCompatActivity {


    private RecyclerView mUserList;
    private DatabaseReference mDatabaseUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);

        mUserList = (RecyclerView) findViewById(R.id.user_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(layoutManager);


        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.user_row,
                UserViewHolder.class,
                mDatabaseUsers
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {
                final String userKey = getRef(position).getKey();//gets the user at the position in the list

                viewHolder.setName(model.getName());
                viewHolder.setPhone(model.getPhone());
                viewHolder.setImage(getApplicationContext(), model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(ListUserActivity.this, "You clicked a view", Toast.LENGTH_LONG).show();
                        Intent profileIntent = new Intent(ListUserActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", userKey);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        mUserList.setAdapter(firebaseRecyclerAdapter);
}




    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView username = (TextView) mView.findViewById(R.id.user_name);
            username.setText(name);//the name must match the db column name
        }

        public void setPhone(String phone){
            TextView phoneNumber = (TextView) mView.findViewById(R.id.user_phone);
            phoneNumber.setText(phone);
        }

        public void setImage(Context ctx, String image){
            ImageView profilePic = (ImageView) mView.findViewById(R.id.user_picture);
            Picasso.with(ctx).load(image).into(profilePic);
        }
    }
}


