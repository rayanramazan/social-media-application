package com.example.sociamedia.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sociamedia.Adapter.PostAdapter;
import com.example.sociamedia.Model.Post;
import com.example.sociamedia.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PostDetailsFragment extends Fragment {

    String postid;
    ImageView share,postImage;
    TextView dec;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postlist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS" , Context.MODE_PRIVATE);
        postid = preferences.getString("postid" , "none");

        share = view.findViewById(R.id.share);
        dec = view.findViewById(R.id.description);
        postImage = view.findViewById(R.id.post_image);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postlist = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postlist);
        recyclerView.setAdapter(postAdapter);

        readPost();

//        share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String desc = dec.getText().toString().trim();
//                BitmapDrawable bitmapDrawable = (BitmapDrawable)postImage.getDrawable();
//                if (bitmapDrawable == null){
//                    // post without image
//                    shareTextOnly(desc);
//                }
//                else {
//                    // post with image
//                    Bitmap bitmap = bitmapDrawable.getBitmap();
//                    shareImageAndText(desc, bitmap);
//                }
//            }
//        });

        return view;
    }
//    private void shareTextOnly(String description) {
//        String shareBody = description+"";
//        Intent sIntent = new Intent(Intent.ACTION_SEND);
//        sIntent.setType("text/plain");
//        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
//        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody); //text to share
//        startActivity(Intent.createChooser(sIntent, "Share Via")); //message to show in share dialog
//    }
//
//    private void shareImageAndText(String description, Bitmap bitmap) {
//        String shareBody = description+"";
//
//        Uri uri = saveImageToShare(bitmap);
//
//        //share intent
//        Intent sIntent = new Intent(Intent.ACTION_SEND);
//        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
//        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Sybject here");
//        sIntent.setType("image/png");
//        startActivity(Intent.createChooser(sIntent,"Share Via"));
//    }
//
//    private Uri saveImageToShare(Bitmap bitmap) {
//        File imageFolder = new File(getContext().getCacheDir(),"images");
//        Uri uri = null;
//        try {
//            imageFolder.mkdir(); //create if not exists
//            File file = new File(imageFolder, "shared_image.png");
//
//            FileOutputStream stream = new FileOutputStream(file);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
//            stream.flush();
//            stream.close();
//            uri = FileProvider.getUriForFile(getContext(),"com.example.sociamedia.fileprovider",file);
//        } catch (Exception e) {
//            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        return uri;
//    }

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