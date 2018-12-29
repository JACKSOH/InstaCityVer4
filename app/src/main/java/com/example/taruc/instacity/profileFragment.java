package com.example.taruc.instacity;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;



public class profileFragment extends Fragment {
    private RecyclerView userpost;
    private ImageView userProf;
    private FirebaseAuth mAuth;
    private DatabaseReference postsRef;
    private DatabaseReference usersRef;
    private FirebaseRecyclerAdapter<ProfileClass,profileViewHolder> adapter;

    private OnFragmentInteractionListener mListener;
    String current_user_id;
    public profileFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final ImageView userProf = (ImageView) view.findViewById((R.id.user_prof));
        String imgPath="";
        userpost = (RecyclerView)view.findViewById(R.id.recycleview_id);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        usersRef =FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("profileImage");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("grod",dataSnapshot.getValue()+"");
                Picasso.with(getContext()).load(dataSnapshot.getValue().toString()).into(userProf);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d("grod",usersRef+"");

        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        Query query = postsRef.orderByKey();

        userpost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        userpost.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<ProfileClass> options = new
                FirebaseRecyclerOptions.Builder<ProfileClass>()
                .setQuery(query,ProfileClass.class).build();

        adapter= new FirebaseRecyclerAdapter<ProfileClass,profileViewHolder>
                (options)
        {




            @Override
            protected void onBindViewHolder(profileViewHolder holder, int position, ProfileClass model) {

               holder.setpostImage(getContext(),model.getpostImage());

            }
            @Override
            public profileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                Log.d("jack","test");
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.profile_cardview_layout, viewGroup, false);
                return new profileViewHolder(view);
            }


        };
        userpost.setAdapter(adapter);
        Log.d("jack",adapter.getItemCount()+"");
        return view;
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


    public static class profileViewHolder extends RecyclerView.ViewHolder{
        ImageView profile;
        View mView;

        public profileViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        public void setpostImage(Context ctx1,String postImage){
            profile = (ImageView) itemView.findViewById(R.id.profile_post);
            Picasso.with(ctx1).load(postImage).into(profile);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
