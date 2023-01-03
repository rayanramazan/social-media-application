package com.example.sociamedia.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sociamedia.Adapter.ChatlistAdapter;
import com.example.sociamedia.ChatActivity;
import com.example.sociamedia.LoginActivity;
import com.example.sociamedia.Model.Chat;
import com.example.sociamedia.Model.ChatList;
import com.example.sociamedia.Model.User;
import com.example.sociamedia.PersonProfileActivity;
import com.example.sociamedia.R;
import com.example.sociamedia.notifications.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatListFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ChatList> chatListList;
    List<User> userList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    ChatlistAdapter chatlistAdapter;
    LinearLayoutManager linearLayoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list , container , false);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recycler_view_list);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userList = new ArrayList<>();
        // adapter
        chatlistAdapter = new ChatlistAdapter(getContext(), userList);
        // set adapter
        recyclerView.setAdapter(chatlistAdapter);

        chatListList = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            chatListList.clear();
            for (DataSnapshot ds : snapshot.getChildren()){
                ChatList chatList = ds.getValue(ChatList.class);
                chatListList.add(chatList);
            }
            loadChats();
        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void loadChats() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    for (ChatList chatList : chatListList){
                        if (user.getId() != null && user.getId().equals(chatList.getId())){
                            userList.add(user);
                            break;
                        }
                    }
                    // adapter
                    chatlistAdapter = new ChatlistAdapter(getContext(), userList);
                    // set adapter
                    recyclerView.setAdapter(chatlistAdapter);
                    // set last message
                    for (int i = 0; i<userList.size(); i++){
                        lastMessage(userList.get(i).getId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for (DataSnapshot ds : snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if (chat == null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String reciver = chat.getReceiver();
                    if (sender == null || reciver == null){
                        continue;
                    }
                    if (chat.getReceiver().equals(currentUser.getUid()) &&
                            chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) &&
                                    chat.getSender().equals(currentUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }
                chatlistAdapter.setLastMessageMap(userId, theLastMessage);
                chatlistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            Toast.makeText(getContext(), "not null", Toast.LENGTH_SHORT).show();
            //
        } else {
            Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
        }
    }



}