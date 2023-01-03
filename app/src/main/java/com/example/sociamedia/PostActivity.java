package com.example.sociamedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView close, image_added;
    TextView post;
    EditText description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);


        // create child posts
        storageReference = FirebaseStorage.getInstance().getReference("posts");

        //btn clode appbar
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this,HomeActivity.class));
                finish();
            }
        });


        // btn post app bar method uploadImage
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });


        // library crop image
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);
    }


    // get file extension image
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // method to click btn post
    private void uploadImage(){
        // dialog post
        ProgressDialog pd = new ProgressDialog(PostActivity.this);
        pd.show();
        pd.setContentView(R.layout.profress_dialog_upload);
        pd.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        if (imageUri != null){
            StorageReference filereference = storageReference.child(System.currentTimeMillis()
            + "." + getFileExtension(imageUri));

            uploadTask = filereference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return filereference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("posts");

                        // push id to firebase
                        String postid = reference.push().getKey();

                        // if success task insert data to child in firebase
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        // link image
                        hashMap.put("postimage", myUri);
                        //description
                        hashMap.put("description", description.getText().toString());
                        // user id
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());


                        // set value
                        reference.child(postid).setValue(hashMap);

                        // close dialog
                        pd.dismiss();

                        startActivity(new Intent(PostActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(PostActivity.this, "Failed !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            StyleableToast.makeText(PostActivity.this,"وێنە نەهاتیە دەستنیشان کرن !", R.style.errorToast).show();
            }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // library crop image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            image_added.setImageURI(imageUri);
        } else {
            startActivity(new Intent(PostActivity.this, HomeActivity.class));
            finish();
        }
    }
}