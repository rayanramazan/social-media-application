package com.example.sociamedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class RegisterActivity extends AppCompatActivity {

    EditText username , fullname , email , password,Repassword;
    Button regsiter;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        regsiter = findViewById(R.id.Btnregsiter);
        txt_login = findViewById(R.id.pageLogin);
        Repassword = findViewById(R.id.Repassword);

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });

        regsiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();
                String str_repassword = Repassword.getText().toString();

                if (str_username.isEmpty()){
                    StyleableToast.makeText(RegisterActivity.this,"ناسنامێ خۆ بنڤێسە !", R.style.errorToast).show();
                } else if (str_username.contains(" ")){
                    StyleableToast.makeText(RegisterActivity.this,"ناسنامێ خۆ دروستاهى بنڤێسە", R.style.errorToast).show();
                }
                else if(str_fullname.isEmpty()){
                    StyleableToast.makeText(RegisterActivity.this,"ناڤێ دروستى خۆ بنڤێسە !", R.style.errorToast).show();
                } else if(str_email.isEmpty()){
                    StyleableToast.makeText(RegisterActivity.this,"ناڤ و نیشانێن ئەلێکترۆنى بنڤێسە !", R.style.errorToast).show();
                } else if(str_password.isEmpty()){
                    StyleableToast.makeText(RegisterActivity.this,"پەیڤا نهێنى بنڤێسە !", R.style.errorToast).show();
                } else if(str_password.length() < 8){
                    StyleableToast.makeText(RegisterActivity.this,"پەیڤا نهێنێ کێمتر ل ٨ پیتا ناچێبت !", R.style.errorToast).show();
                }
                else if(str_username.length() > 'A' && str_username.length() < 'Z'){
                    StyleableToast.makeText(RegisterActivity.this,"ناسنامێ خۆ دروستاهى بنڤێسە !", R.style.errorToast).show();
                }
                else if (!str_password.equals(str_repassword)){
                    StyleableToast.makeText(RegisterActivity.this,"پەیڤا نهێنى وەک ئێک بنڤێسە", R.style.errorToast).show();
                }
                else{
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.show();
                    pd.setContentView(R.layout.progress_dialog);
                    pd.getWindow().setBackgroundDrawableResource(
                            android.R.color.transparent
                    );
                    register(str_username, str_fullname, str_email, str_password);
                }
            }
        });
    }
    private void register(final String username , final String fullname , String email , String password){
        auth.createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio" , "");
                            hashMap.put("onlineStatus", "online");
                            hashMap.put("typingTo", "noOne");
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/socialmeda-b29a0.appspot.com/o/user.jpg?alt=media&token=df92af74-0664-4490-b357-a89dc7f4c80b");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        // فرێکرنا ناما پشتڕاستکرنێ
                                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    pd.dismiss();
                                                    StyleableToast.makeText(RegisterActivity.this, "ناما پشتڕاستکرنێ بۆ هەژمارا تە ئەلێکترۆنى", R.style.successToast).show();
                                                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    pd.dismiss();
                                                    StyleableToast.makeText(RegisterActivity.this, "دووبارە هەوڵبدە", R.style.errorToast).show();
                                                    auth.signOut();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        } else{
                            pd.dismiss();
                            StyleableToast.makeText(RegisterActivity.this,"ببورە, توو نەشێى ڤى ناڤ و نیشانى خۆ تۆمار بکەى !", R.style.errorToast).show();
                        }
                    }
                });
    }
}