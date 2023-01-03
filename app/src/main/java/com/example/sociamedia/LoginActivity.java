package com.example.sociamedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView txt_signup,txt_Recovery;

    ProgressDialog pd;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;

    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && firebaseUser.isEmailVerified()){
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.Btnlogin);
        txt_signup = findViewById(R.id.pageRegister);
        txt_Recovery = findViewById(R.id.forgotpassword);

        auth = FirebaseAuth.getInstance();

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        // recovery password
        txt_Recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecoverPasswordDialog();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password))
                    StyleableToast.makeText(LoginActivity.this,"زانیاریێن خۆ بنڤێسە !", R.style.errorToast).show();
                else {
                        // alter progress loading
                        pd = new ProgressDialog(LoginActivity.this);
                        pd.show();
                        pd.setContentView(R.layout.progress_dialog);
                        pd.getWindow().setBackgroundDrawableResource(
                                android.R.color.transparent
                        );

                        auth.signInWithEmailAndPassword(str_email, str_password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {


                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                                    .child(auth.getCurrentUser().getUid());

                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    FirebaseUser user = auth.getCurrentUser();
                                                    if (user.isEmailVerified()){
                                                        pd.dismiss();
                                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        pd.dismiss();
                                                        StyleableToast.makeText(LoginActivity.this, "هیڤیدارین هەژمارا خۆ پشتڕاست بکە", R.style.errorToast).show();
                                                        auth.signOut();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    pd.dismiss();
                                                }
                                            });

                                        } else {
                                            pd.dismiss();
                                            StyleableToast.makeText(LoginActivity.this, "دووبارە هەوڵبدە !", R.style.errorToast).show();
                                        }
                                    }
                                });
                    }
                }
        });
    }
    private void RecoverPasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.recover_dialog, null);
        builder.setView(view);
        EditText emailRec;
        Button Reco,BackRec;

        emailRec = view.findViewById(R.id.recover_email);
        Reco = view.findViewById(R.id.Recover);
        BackRec = view.findViewById(R.id.exit);

        BackRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Reco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailRec.getText().toString().trim();
                beginRecovery(email);
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog = builder.show();
    }

    private void beginRecovery(String email) {
        pd = new ProgressDialog(LoginActivity.this);
        pd.show();
        pd.setContentView(R.layout.progress_dialog);
        pd.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    StyleableToast.makeText(LoginActivity.this,"نامە هاتە فرێکرن بۆ هەژمارا تە ئیمێلى", R.style.successToast).show();
                }
                else {
                    StyleableToast.makeText(LoginActivity.this,"هەژمار ب ڤی ناڤ و نیشانى نینن", R.style.errorToast).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                StyleableToast.makeText(LoginActivity.this,"ببورە , دووبارە هەوڵبدە", R.style.errorToast).show();
            }
        });
    }
    public void onBackPressed(){
    }
}