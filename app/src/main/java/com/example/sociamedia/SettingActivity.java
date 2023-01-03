package com.example.sociamedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.zip.Inflater;

import io.github.muddz.styleabletoast.StyleableToast;

public class SettingActivity extends AppCompatActivity {

    TextView changePass,deleteaccount;

    AlertDialog dialog;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        firebaseAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("رێکخستن");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        changePass = findViewById(R.id.changePassword);
//        deleteaccount = findViewById(R.id.deleteAccount);

//        deleteaccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
//
//                View view1 = getLayoutInflater().inflate(R.layout.delete_account_dialog, null);
//
//                builder.setView(view1);
//
//                Button deleteAccount, noDeleteAccount;
//                deleteAccount = view1.findViewById(R.id.deleteaccounts);
//                noDeleteAccount = view1.findViewById(R.id.nodeleteaccount);
//
//                noDeleteAccount.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//
//                deleteAccount.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        pd = new ProgressDialog(SettingActivity.this);
//                        pd.show();
//                        pd.setContentView(R.layout.progress_dialog);
//                        pd.getWindow().setBackgroundDrawableResource(
//                                android.R.color.transparent
//                        );
//
//
//                        FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
//                                    pd.dismiss();
//                                    dialog.dismiss();
//                                    Toast.makeText(SettingActivity.this, "deleted", Toast.LENGTH_SHORT).show();
//
//                                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                } else {
//                                    pd.dismiss();
//                                    Toast.makeText(SettingActivity.this, "error", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//
//
//                    }
//                });
//                dialog = builder.create();
//                dialog = builder.show();
//            }
//        });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

                View view1 = getLayoutInflater().inflate(R.layout.update_password_dialog, null);

                builder.setView(view1);
                EditText oldpassword,newpassword;
                Button updatepassword;

                oldpassword = view1.findViewById(R.id.passwordEt);
                newpassword = view1.findViewById(R.id.cPasswordEt);
                updatepassword = view1.findViewById(R.id.updatePasswordBtn);

                updatepassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pd = new ProgressDialog(SettingActivity.this);
                        pd.show();
                        pd.setContentView(R.layout.progress_dialog);
                        pd.getWindow().setBackgroundDrawableResource(
                                android.R.color.transparent
                        );

                        String oldPassword = oldpassword.getText().toString().trim();
                        String newPassword = newpassword.getText().toString().trim();
                        if (oldPassword.isEmpty() || newPassword.isEmpty()){
                            pd.dismiss();
                            StyleableToast.makeText(SettingActivity.this,"هیڤیدارین خانا پر بکە !", R.style.errorToast).show();
                        }
                        else if (newPassword.length() < 8){
                            pd.dismiss();
                            StyleableToast.makeText(SettingActivity.this,"پەیڤا نهێنێ کێمتر ل ٨ پیتا ناچێبت !", R.style.errorToast).show();
                        }
                        else {
                            updatePass(oldPassword, newPassword);
                        }
                    }
                });

                dialog = builder.create();
                dialog = builder.show();
            }
        });
    }

    private void updatePass(String oldPassword, String newpassword) {

        user = firebaseAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(newpassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        pd.dismiss();
                                        dialog.dismiss();
                                        StyleableToast.makeText(SettingActivity.this,"پەیڤا نهێنى سەرکەفتیانە هاتە نویى کرن", R.style.successToast).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pd.dismiss();
                                        dialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "error", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        dialog.dismiss();
                        StyleableToast.makeText(SettingActivity.this,"پەیڤا نهێنى نۆکە خەلەتە", R.style.errorToast).show();
                    }
                });
    }
}