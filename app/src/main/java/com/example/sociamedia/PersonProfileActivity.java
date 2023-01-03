package com.example.sociamedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sociamedia.Adapter.MyPhotoAdapter;
import com.example.sociamedia.Model.Post;
import com.example.sociamedia.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonProfileActivity extends AppCompatActivity {

    ImageView image_profile, Sendmsg;
    TextView posts, followers, following, fullname, bio, username, About;
    Button btn_follow_profile;

    RecyclerView recyclerView;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> postList;

    private DatabaseReference profileUserRef , UserRef;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    String persone_profile_id, receiverUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        toolbar.setTitleTextColor(000);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        IntializeFields();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(PersonProfileActivity.this, 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(PersonProfileActivity.this, postList);
        recyclerView.setAdapter(myPhotoAdapter);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        persone_profile_id = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        checkFollow();
        getFollowers();
        getNrPost();
        myPhotos();

        btn_follow_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = btn_follow_profile.getText().toString();
               if (btn.equals("پەیڕەوى")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(receiverUserId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(receiverUserId)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                } else if (btn.equals("دیفکەفتى")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(receiverUserId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(receiverUserId)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        Sendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ChatIntent = new Intent(PersonProfileActivity.this, ChatActivity.class);
                ChatIntent.putExtra("visit_user_id", receiverUserId);
                startActivity(ChatIntent);
            }
        });

        UserRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    String myUserName = snapshot.child("username").getValue().toString();
                    String myFullName = snapshot.child("fullname").getValue().toString();
                    String myBio = snapshot.child("bio").getValue().toString();

                    if (myBio.equals("")){
                        About.setVisibility(View.GONE);
                    } else{
                        About.setVisibility(View.VISIBLE);
                    }
                    username.setText("@" + myUserName);
                    fullname.setText(myFullName);
                    bio.setText(myBio);
                }
                User user = snapshot.getValue(User.class);

                Glide.with(PersonProfileActivity.this).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonProfileActivity.this, FollowersActivity.class);
                intent.putExtra("id", receiverUserId);
                intent.putExtra("title", "پەیڕەوى");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonProfileActivity.this, FollowersActivity.class);
                intent.putExtra("id", receiverUserId);
                intent.putExtra("title", "دیفکەفتى");
                startActivity(intent);
            }
        });




    }

    private void IntializeFields() {
        image_profile = findViewById(R.id.persone_image_profile);
        posts = findViewById(R.id.persone_posts);
        followers = findViewById(R.id.persone_followers);
        following = findViewById(R.id.persone_following);
        fullname = findViewById(R.id.persone_fullname);
        bio = findViewById(R.id.persone_bio);
        username = findViewById(R.id.persone_username);
        btn_follow_profile = findViewById(R.id.persone_btn_follow);
        Sendmsg = findViewById(R.id.sendmsg);
        About = findViewById(R.id.about);
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(receiverUserId).exists()){
                    btn_follow_profile.setText("دیفکەفتى");
                    btn_follow_profile.setTextColor(0xFF000000);
                    btn_follow_profile.setBackgroundColor(0xFFFFFFFF);
                } else {
                    btn_follow_profile.setText("پەیڕەوى");
                    btn_follow_profile.setTextColor(0xFFFFFFFF);
                    btn_follow_profile.setBackgroundColor(0xFFFF9063);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(receiverUserId).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(receiverUserId).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNrPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(receiverUserId)){
                        i++;
                    }
                }

                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(receiverUserId)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}