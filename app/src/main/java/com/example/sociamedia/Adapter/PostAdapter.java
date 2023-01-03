package com.example.sociamedia.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sociamedia.CommentActivity;
import com.example.sociamedia.FollowersActivity;
import com.example.sociamedia.Fragment.PostDetailsFragment;
import com.example.sociamedia.Fragment.ProfileFragment;
import com.example.sociamedia.HomeActivity;
import com.example.sociamedia.Model.Notification;
import com.example.sociamedia.Model.Post;
import com.example.sociamedia.Model.User;
import com.example.sociamedia.PersonProfileActivity;
import com.example.sociamedia.PostDetailsActivity;
import com.example.sociamedia.R;
import com.example.sociamedia.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import io.github.muddz.styleabletoast.StyleableToast;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;

    AlertDialog dialog;
    ProgressDialog pd;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);


        Glide.with(mContext).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(holder.post_image);

        if (post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        // method
        publisherInfo(holder.image_profile, holder.username, holder.publisher, post.getPublisher());
        isLiked(post.getPostid(), holder.like);
        nrLikes(holder.likes, post.getPostid());
        getComments(post.getPostid(), holder.comments);
        isSaved(post.getPostid(), holder.save);
        String x = holder.description.toString();

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit();
                    editor.putString("profileid" , post.getPublisher());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                } else {
                    String visit_user_id = post.getPublisher();
                    Intent intent = new Intent(mContext, PersonProfileActivity.class);
                    intent.putExtra("visit_user_id", visit_user_id);
                    ((FragmentActivity) mContext).startActivity(intent);
                }
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit();
                    editor.putString("profileid" , post.getPublisher());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                } else {
                    String visit_user_id = post.getPublisher();
                    Intent intent = new Intent(mContext, PersonProfileActivity.class);
                    intent.putExtra("visit_user_id", visit_user_id);
                    ((FragmentActivity) mContext).startActivity(intent);
                }
            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS" , Context.MODE_PRIVATE).edit();
                    editor.putString("profileid" , post.getPublisher());
                    editor.apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ProfileFragment()).commit();
                } else {
                    String visit_user_id = post.getPublisher();
                    Intent intent = new Intent(mContext, PersonProfileActivity.class);
                    intent.putExtra("visit_user_id", visit_user_id);
                    ((FragmentActivity) mContext).startActivity(intent);
                }
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid", post.getPostid());
                    editor.apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new PostDetailsFragment()).commit();
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }
            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);

                    addNotifications(post.getPublisher(), post.getPostid());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid" , post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent  intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid" , post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title", "دڵخواز");
                mContext.startActivity(intent);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = holder.description.getText().toString().trim();
                BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.post_image.getDrawable();
                if (bitmapDrawable == null){
                    // post without image
                    shareTextOnly(holder.description);
                }
                else {
                    // post with image
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(holder.description, bitmap);
                }
            }
        });
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit:
                                editPost(post.getPostid());
                                return true;
                            case R.id.delete:
//
                                pd = new ProgressDialog(mContext);
                                pd.show();
                                pd.setContentView(R.layout.progress_dialog);
                                pd.getWindow().setBackgroundDrawableResource(
                                        android.R.color.transparent
                                );
                                Query query = FirebaseDatabase.getInstance().getReference("Notifications").child(post.getPublisher()).orderByChild("postid").equalTo(post.getPostid());
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                ds.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                Query fquery = FirebaseDatabase.getInstance().getReference("posts").orderByChild("postid").equalTo(post.getPostid());
                                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            ds.getRef().removeValue();
                                        }
                                        pd.dismiss();
                                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                new ProfileFragment()).commit();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        pd.dismiss();
                                    }
                                });
//
//                                                               Query fquery = FirebaseDatabase.getInstance().getReference("posts").orderByChild("postid").equalTo(post.getPostid());
//                                                               fquery.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                   @Override
//                                                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                                                       for (DataSnapshot ds : snapshot.getChildren()) {
//                                                                           ds.getRef().removeValue();
//                                                                       }
//                                                                       pd.dismiss();
//                                                                       ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                                                                               new ProfileFragment()).commit();
//
////
//
//                                                                   }
//
//                                                                   @Override
//                                                                   public void onCancelled(@NonNull DatabaseError error) {
////                                                        pd.dismiss();
//                                                                   }
//
//                                                               });

//                                        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(post.getPostimage());
//                                        picRef.delete()
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void unused) {

//                                HashMap<String, Object> hashMap = new HashMap<>();
//                                hashMap.put("ispost", false);
//                                hashMap.put("postid", post.getPostid());
//                                hashMap.put("text", "delete");
//                                hashMap.put("userid", firebaseUser.getUid());

//                                FirebaseDatabase.getInstance().getReference("Notifications")
//                                        .child(post.getPublisher()).updateChildren(hashMap);

//                                                        FirebaseDatabase.getInstance().getReference("Notifications").child(post.getPublisher()).orderByChild("postid").equalTo(post.getPostid())
//                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                    @Override
//                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                        snapshot.getRef().removeValue();
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                    }
//                                                                });

//                                                    }
//                                                });
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });




                                return true;
                            case R.id.report:
                                Toast.makeText(mContext, "هێشتا ل بەر دەست نینە", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisher().equals(firebaseUser.getUid())){
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });

    }
    private void shareTextOnly(TextView description) {
        String shareBody = description+"";
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody); //text to share
        mContext.startActivity(Intent.createChooser(sIntent, "Share Via")); //message to show in share dialog
    }

    private void shareImageAndText(TextView description, Bitmap bitmap) {
        String shareBody = description+"";

        Uri uri = saveImageToShare(bitmap);

        //share intent
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
        sIntent.setType("image/png");
        mContext.startActivity(Intent.createChooser(sIntent,"Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(mContext.getCacheDir(),"images");
        Uri uri = null;
        try {
            imageFolder.mkdir(); //create if not exists
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(mContext,"com.example.sociamedia.fileprovider",file);
        } catch (Exception e) {
            Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }


    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, like, comment, save, share, more;
        public TextView username, likes, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            share = itemView.findViewById(R.id.share);
            more = itemView.findViewById(R.id.more);

        }
    }

    private void getComments(String postid,final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // if isset like if liked
    private void isLiked(String postid, ImageView imageView){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.heartlike);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.heartdislike);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ///


    private void editPost(String postid){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view1 = layoutInflater.inflate(R.layout.editpost_dialog, null);

        EditText txt_editpost;
        Button updatePost,NoupdatePost;

        txt_editpost = view1.findViewById(R.id.txt_editPost);
        updatePost = view1.findViewById(R.id.Updatepost);
        NoupdatePost = view1.findViewById(R.id.noUpdatepost);

        builder.setView(view1);

        getText(postid, txt_editpost);

        updatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("description", txt_editpost.getText().toString());

                FirebaseDatabase.getInstance().getReference("posts")
                        .child(postid).updateChildren(hashMap);
                dialog.dismiss();
                StyleableToast.makeText(mContext,"بابەت هاتە نویى کرن ڤە", R.style.successToast).show();
            }
        });

        NoupdatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog = builder.show();


//
//        builder.setTitle("Edit Post");
//
//        EditText editText = new EditText(mContext);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT
//        );
//        editText.setLayoutParams(lp);
//        builder.setView(editText);
//
//        getText(postid, editText);
//
//        builder.setPositiveButton("Edit",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("description", editText.getText().toString());
//
//                        FirebaseDatabase.getInstance().getReference("posts")
//                                .child(postid).updateChildren(hashMap);
//                        StyleableToast.makeText(mContext,"بابەت هاتە نویى کرن ڤە", R.style.successToast).show();
//                    }
//                });
//        builder.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//        builder.create().show();
    }

    private void getText(String postid, EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editText.setText(snapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    ///

    private void addNotifications(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "پۆستێ تە هاتە دڵخواز کرن");
        hashMap.put("postid", postid);
        hashMap.put("ispost", true);

        reference.push().setValue(hashMap);
    }

    // number likes post
    private void nrLikes(TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText(snapshot.getChildrenCount()+ "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void publisherInfo(ImageView image_profile, TextView username, TextView publisher, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);

                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void isSaved(String postid, ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
