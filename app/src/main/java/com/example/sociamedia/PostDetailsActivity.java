package com.example.sociamedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sociamedia.Adapter.PostAdapter;
import com.example.sociamedia.Model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailsActivity extends AppCompatActivity {

    String postid;
    ImageView share,postImage;
    TextView dec;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        SharedPreferences preferences = getApplication().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        postid = preferences.getString("postid" , "none");

        share = findViewById(R.id.share);
        dec = findViewById(R.id.description);
        postImage = findViewById(R.id.post_image);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        postlist = new ArrayList<>();
        postAdapter = new PostAdapter(this, postlist);
        recyclerView.setAdapter(postAdapter);

        readPost();


    }

    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postlist.clear();
                Post post = snapshot.getValue(Post.class);
                postlist.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}