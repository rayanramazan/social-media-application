package com.example.sociamedia.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sociamedia.LoginActivity;
import com.example.sociamedia.Model.Chat;
import com.example.sociamedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

import io.github.muddz.styleabletoast.StyleableToast;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyHolder>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chat> chatList;
    String imageUrl;

    FirebaseUser fUser;

    AlertDialog dialog;


    public ChatAdapter(Context context,List<Chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimestamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = android.text.format.DateFormat.format("hh:mm aa", cal).toString();
        holder.messageIv.setText(message);
        holder.timeIv.setText(dateTime);

        Glide.with(context).load(imageUrl).into(holder.profileIv);

        holder.messageLAyout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view1 = layoutInflater.inflate(R.layout.delete_msg_dialog, null);
                Button deletemsg, backmsg;

                deletemsg = view1.findViewById(R.id.delete_msg);
                backmsg = view1.findViewById(R.id.no_delete_msg);

                builder.setView(view1);

                backmsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                deletemsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteMessage(position);
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog = builder.show();
                return true;
            }
        });
//        holder.messageLAyout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//                View view1 = layoutInflater.inflate(R.layout.delete_msg_dialog, null);
//                Button deletemsg, backmsg;
//
//                deletemsg = view1.findViewById(R.id.delete_msg);
//                backmsg = view1.findViewById(R.id.no_delete_msg);
//
//                builder.setView(view1);
//
//                backmsg.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//
//                deletemsg.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        deleteMessage(position);
//                        dialog.dismiss();
//                    }
//                });
//                dialog = builder.create();
//                dialog = builder.show();
//            }
//        });

        if (position == chatList.size()-1){
            if (chatList.get(position).isSeen()){
                holder.isSeen.setText("بینى");
            } else {
                holder.isSeen.setText("هاتە گەهاندن");
            }
        } else {
            holder.isSeen.setVisibility(View.GONE);
        }
    }

    private void deleteMessage(int i) {
        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        String msgTimeStamp = chatList.get(i).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){

                    if (ds.child("sender").getValue().equals(myUID)){
                        // am shein 2 reka msg msah kain 2 taibat mandi
                        // * 1) msah krn nav chate da
                        // * 2) haka ma msah kr jhe risale bnvesit av msg hatya msah krn

                        // 1 msah krn risale
                        //ds.getRef().removeValue();

                        // 2 reka di
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "ئەف نامە هاتیە ژێبرن ...");
                        ds.getRef().updateChildren(hashMap);

//                        Toast.makeText(context, "نامە هاتە ژێبرن", Toast.LENGTH_SHORT).show();
                    } else {
                        StyleableToast.makeText(context,"تە مافێ هەى نامێن خۆ تنێ ژێبەى", R.style.errorToast).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView profileIv;
        TextView messageIv, timeIv, isSeen;
        LinearLayout messageLAyout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
            messageIv = itemView.findViewById(R.id.messageIv);
            timeIv = itemView.findViewById(R.id.time);
            isSeen = itemView.findViewById(R.id.isSeen);
            messageLAyout = itemView.findViewById(R.id.messageLayout);
        }
    }
}
