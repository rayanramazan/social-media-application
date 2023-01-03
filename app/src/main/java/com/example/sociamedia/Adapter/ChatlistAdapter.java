package com.example.sociamedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sociamedia.ChatActivity;
import com.example.sociamedia.Model.User;
import com.example.sociamedia.R;

import java.util.HashMap;
import java.util.List;

public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.MyHolder>{
    Context context;
    List<User> userList;
    private HashMap<String , String> lastMessageMap;

    public ChatlistAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String hisUid = userList.get(position).getId();
        String userImage = userList.get(position).getImageurl();
        String username = userList.get(position).getUsername();
        String lastMessage = lastMessageMap.get(hisUid);

        holder.username.setText(username);

        if (lastMessage == null || lastMessage.equals("default")){
            holder.lastMessage.setVisibility(View.GONE);
        } else {
            holder.lastMessage.setVisibility(View.VISIBLE);
            holder.lastMessage.setText(lastMessage);
        }
        Glide.with(context).load(userImage).into(holder.profileIv);

//        if (userList.get(position).getOnlineStatus().equals("online")){
//            // online
//            holder.onlineStatus.setImageResource(R.drawable.circle_online);
//        } else {
//            // offline
//            holder.onlineStatus.setImageResource(R.drawable.circle_offline);
//        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("visit_user_id", hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String userId, String lastMessage){
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIv, onlineStatus;
        TextView username, lastMessage;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIv = itemView.findViewById(R.id.profileIv);
//            onlineStatus = itemView.findViewById(R.id.onlineStatus);
            username = itemView.findViewById(R.id.username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
