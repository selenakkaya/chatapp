package com.example.chatapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    StorageReference storageRef = storage.getReference();


    private DatabaseReference mUserDatabase;

    FirebaseUser fuser;


    public MessageAdapter(List<Chat> mChatList) {

        this.mChat = mChatList;

    }

    public MessageAdapter(){

    }


    public MessageAdapter(Context mContext , List<Chat> mChat, String imageurl){

        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT){

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new  MessageAdapter.ViewHolder(view);}
        else {

            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left ,parent,false);
            return new  MessageAdapter.ViewHolder(view);
        }
    }



    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView messageImage;
        public CircleImageView profileImage;
        public TextView textSeen;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message =itemView.findViewById(R.id.show_message);
            profileImage = itemView.findViewById(R.id.profile_image);
            messageImage = itemView.findViewById(R.id.message_image_layout);
            textSeen =itemView.findViewById(R.id.text_seen);

        }


    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        Chat chat =mChat.get(position);
        String fromUser = chat.getReceiver();
        String messageType = chat.getType();


        if(messageType.equals("text")){

            viewHolder.show_message.setText(chat.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);

        }if(messageType.equals("image")){
            viewHolder.show_message.setVisibility(View.INVISIBLE);
            storageRef.child(chat.getMessage())
                    .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    Picasso.get()
                            .load(task.getResult())
                            .placeholder(R.drawable.aplaceholder)
                            .into(viewHolder.messageImage);
                }
            });

        }




        if (imageurl.equals("default")){
            viewHolder.profileImage.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(imageurl).into(viewHolder.profileImage);
        }

        if (position == mChat.size()-1){
            if (chat.isSeen()){
                viewHolder.textSeen.setText("seen");
            }else {
                viewHolder.textSeen.setText("Delivered");
            }
        }else {
            viewHolder.textSeen.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}