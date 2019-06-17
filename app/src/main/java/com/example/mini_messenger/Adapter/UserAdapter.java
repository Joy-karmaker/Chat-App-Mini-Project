package com.example.mini_messenger.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mini_messenger.MainActivity;
import com.example.mini_messenger.MessageActivity;
import com.example.mini_messenger.Model.Chat;
import com.example.mini_messenger.Model.User;
import com.example.mini_messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private boolean ischat;
    String theLastMessage;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public UserAdapter(Context context, List<User>users, boolean ischat) {
        this.context = context;
        this.users = users;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final User user = users.get(position);
            holder.username.setText(user.getUsername());
            if(user.getImageURL().equals("default")) {
                holder.profile_image.setImageResource(R.drawable.index3);
            }
            else {
                Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
            }

            if(ischat) {
                lastMessage(user.getId(), holder.last_msg);
            }
            else {
                holder.last_msg.setVisibility(View.GONE);
            }

            if(ischat) {
                if(user.getStatus().equals("online")) {
                    holder.img_on.setVisibility(View.VISIBLE);
                    holder.img_off.setVisibility(View.GONE);
                }
                else {
                    holder.img_on.setVisibility(View.GONE);
                    holder.img_off.setVisibility(View.VISIBLE);
                }
            }
            else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("userid", user.getId());
                    context.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image2);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            last_msg = itemView.findViewById(R.id.last_msg);

        }
    }

    private void lastMessage( final String userid, final TextView last_msg) {

        theLastMessage = "default";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        if (firebaseUser != null) {

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if ((chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid))
                                || (chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))) {

                            theLastMessage = chat.getMessage();

                        }
                    }

                    switch (theLastMessage) {

                        case "default":
                            last_msg.setText("No Message");
                            break;

                        default:
                            last_msg.setText(theLastMessage);
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
}
