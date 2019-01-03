package com.example.taruc.instacity;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;




/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link feedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link feedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class activityFragment extends Fragment{
    private RecyclerView eventList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef,postsRef,likesRef;
    private FirebaseRecyclerAdapter<EventClass,eventViewHolder> adapter;

    String currentUserID;
    Boolean likesCheck;



    private OnFragmentInteractionListener mListener;

    public activityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @
     * @return A new instance of fragment feedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static activityFragment newInstance() {
        activityFragment fragment = new activityFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        eventList = (RecyclerView)view.findViewById(R.id.allEventList);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Interested");

        Query query = postsRef.orderByKey();

        eventList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        eventList.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<EventClass> options = new
                FirebaseRecyclerOptions.Builder<EventClass>()
                .setQuery(query,EventClass.class).build();




        adapter= new FirebaseRecyclerAdapter<EventClass,eventViewHolder>
                (options)
        {




            @Override
            protected void onBindViewHolder(eventViewHolder holder, int position, EventClass model) {
                final String PostKey = getRef(position).getKey();

               holder.setEventTitle(model.getEventTitle());

                holder.setEventDate(model.getEventDate());
                holder.setStartTime(model.getStartTime());
                holder.setEndTime(model.getEndTime());
                holder.setLocation(model.getLocation());
                holder.setEventCaption(model.getEventCaption());
                holder.setEventImage(getContext(),model.getEventImage());

                holder.setLikeButtonStatus(PostKey);

                holder.likesEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likesCheck = true;

                        likesRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(likesCheck.equals(true)){
                                    if(dataSnapshot.child(PostKey).hasChild(currentUserID)){
                                        likesRef.child(PostKey).child(currentUserID).removeValue();
                                        likesCheck=false;

                                    }else{
                                        likesRef.child(PostKey).child(currentUserID).setValue(true);
                                        likesCheck=false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });


            }
            @Override
            public eventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.all_activities_layout, viewGroup, false);
                eventViewHolder ev = new eventViewHolder(view);

                return ev;
            }


        };

        eventList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


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

    private static class eventViewHolder extends RecyclerView.ViewHolder{

        ImageButton likesEventButton;
        DatabaseReference likesRef;
        String currentUserId;
        int countLikes;
        TextView eventLikesCount;

        View mView;
        public eventViewHolder(View itemView){
            super(itemView);
            mView=itemView;
            likesEventButton = (ImageButton)mView.findViewById(R.id.event_likes);
            likesRef = FirebaseDatabase.getInstance().getReference().child("Interested");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            eventLikesCount=(TextView)mView.findViewById(R.id.event_likes_count);
        }
        public void setLikeButtonStatus(final String PostKey){
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(PostKey).hasChild(currentUserId)){
                        countLikes = (int)dataSnapshot.child(PostKey).getChildrenCount();
                        likesEventButton.setImageResource(R.drawable.star);
                        eventLikesCount.setText(Integer.toString(countLikes)+"Interest");
                    }else{
                        countLikes = (int)dataSnapshot.child(PostKey).getChildrenCount();
                        likesEventButton.setImageResource(R.drawable.star_dark);
                        eventLikesCount.setText(Integer.toString(countLikes)+"Interest");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setEventTitle(String eventTitle){
            TextView t = (TextView)mView.findViewById(R.id.eventTitle);

            t.setText(eventTitle);
        }
        public void setLocation(String location){
            TextView t = (TextView)mView.findViewById(R.id.eventLocationLabel);
            t.setText(location);
        }
        public void setStartTime(String startTime){
            TextView t = (TextView)mView.findViewById(R.id.eventSTime);
            t.setText(startTime);
        }
        public void setEndTime(String endTime){
            TextView t = (TextView)mView.findViewById(R.id.eventETime);
            t.setText(endTime);
        }
        public void setEventDate(String eventDate){
            Log.d("yo","no this");
            TextView d = (TextView)mView.findViewById(R.id.eventActualDate);
            d.setText(eventDate);
        }
        public void setEventCaption(String eventCaption){
            Log.d("yo","no this");
            TextView d = (TextView)mView.findViewById(R.id.captionHeyEvent);
            d.setText(eventCaption);
        }

        public void setEventImage(Context ctx1,String postImage){
            Log.d("yo","no this");
            ImageView image=(ImageView)mView.findViewById(R.id.eventImage);
            Picasso.with(ctx1).load(postImage).into(image);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
   /* public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/


    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
