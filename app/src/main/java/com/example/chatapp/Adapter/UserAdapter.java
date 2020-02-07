package com.example.chatapp.Adapter;

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
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUser;
    private boolean isChat;

    String theLastMessage ;

    public UserAdapter(Context mContext , List<User> mUser, boolean isChat ){

        this.mUser = mUser;
        this.mContext = mContext;
        this.isChat=isChat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new  UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user =mUser.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")){

            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{

            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        if (isChat){
            lastMessage(user.getId(), holder.lastMsg);
        }else {
            holder.lastMsg.setVisibility(View.GONE);
        }
        if(isChat){
            if(user.getStatus().equals("online")){
                holder.imageOn.setVisibility(View.VISIBLE);
                holder.imageOff.setVisibility(View.GONE);
            }else{
                holder.imageOn.setVisibility(View.GONE);
                holder.imageOff.setVisibility(View.VISIBLE);
            }
        }else{
            holder.imageOn.setVisibility(View.GONE);
            holder.imageOff.setVisibility(View.GONE);
        }




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext , MessageActivity.class);
                intent.putExtra("userId",user.getId());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView imageOn;
        private ImageView imageOff;
        private TextView lastMsg;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username =itemView.findViewById(R.id.username);
            profile_image= itemView.findViewById(R.id.profile_image);
            imageOn =itemView.findViewById(R.id.img_on);
            imageOff =itemView.findViewById(R.id.img_off);
            lastMsg =itemView.findViewById(R.id.last_msg);


        }
    }

    private void lastMessage(final String userId, final TextView lastMsg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                        theLastMessage = chat.getMessage();
                    }
                }
                switch (theLastMessage){

                    case "default":
                        lastMsg.setText("No Message");
                        break;
                    default:
                        lastMsg.setText(theLastMessage);
                        break;

                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
