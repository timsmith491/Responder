package com.timsmith.responder.chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.timsmith.responder.R;

import java.util.ArrayList;


public class ChatGroups extends AppCompatActivity {

    private Button add_room;
    private EditText room_name;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private DatabaseReference mChatDatabase;
    private String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_groups);

        add_room = (Button) findViewById(R.id.btn_add_room);
        room_name = (EditText) findViewById(R.id.room_name_edittext);
        listView = (ListView) findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_of_rooms);

        listView.setAdapter(arrayAdapter );

        request_user_name();

        mChatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat");
        mChatDatabase.keepSynced(true);


//
//        add_room.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put(room_name.getText().toString(), "");
//                mChatDatabase.child("Chats").getRoot().updateChildren(map);
//            }
//        });

    }

    private void request_user_name(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Name");
        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                name = input_field.getText().toString();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
