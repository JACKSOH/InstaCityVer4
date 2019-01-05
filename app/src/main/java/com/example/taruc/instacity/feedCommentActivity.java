package com.example.taruc.instacity;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class feedCommentActivity extends AppCompatActivity {

    private ImageButton postCommentButton;
    private EditText commentInput;
    private RecyclerView commentList;
    private String PostKey,currentUserID,saveCurrentDate,saveCurrentTime;
    private DatabaseReference usersRef,PostRef;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<CommentClass,CommentsViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_comment);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        PostKey = getIntent().getExtras().get("PostKey").toString();
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("Comments");



        postCommentButton = (ImageButton)findViewById(R.id.post_comment_btn);
        commentInput = (EditText)findViewById(R.id.feed_comment_input);
        commentList = (RecyclerView)findViewById(R.id.comments_list);
        commentList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(false);
        llm.setStackFromEnd(true);
        commentList.setLayoutManager(llm);

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String userName = dataSnapshot.child("userName").getValue().toString();

                            ValidateComment(userName);
                            commentInput.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        Query query = PostRef.orderByKey();
        FirebaseRecyclerOptions<CommentClass> options = new
                FirebaseRecyclerOptions.Builder<CommentClass>()
                .setQuery(query,CommentClass.class).build();

        adapter= new FirebaseRecyclerAdapter<CommentClass,CommentsViewHolder>
                (options)
        {




            @Override
            protected void onBindViewHolder(CommentsViewHolder holder, int position, CommentClass model) {


                holder.setComment(model.getComment());
                holder.setDate(model.getDate());

                holder.setTime(model.getTime());
                holder.setUserName(model.getUserName());



            }
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.all_comment_layout, viewGroup, false);
                return new CommentsViewHolder(view);
            }


        };

        commentList.setAdapter(adapter);
    }

    private void ValidateComment(String userName) {
        String commentText = commentInput.getText().toString();

        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(this,"Please write text to comment....",Toast.LENGTH_SHORT).show();
        }else{
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate=currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime=currentTime.format(calFordDate.getTime());

            final String RandomKey=currentUserID+saveCurrentDate+saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid",currentUserID);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",saveCurrentDate);
            commentsMap.put("time",saveCurrentTime);
            commentsMap.put("userName",userName);

            PostRef.child(RandomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(feedCommentActivity.this,"You have commented successfully",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(feedCommentActivity.this,"Error occured, Try again",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setComment(String comment){
            TextView com = (TextView)mView.findViewById(R.id.comment_text);
            com.setText(comment);


        }
        public void setDate(String date){
            TextView d = (TextView)mView.findViewById(R.id.comment_date);
            d.setText("  "+date);

        }
        public void setTime(String time){
            TextView t = (TextView)mView.findViewById(R.id.comment_time);
            t.setText(" "+time);

        }
        public void setUserName(String userName){
            TextView name = (TextView)mView.findViewById(R.id.comment_username);
            name.setText(userName);

        }
    }
}
