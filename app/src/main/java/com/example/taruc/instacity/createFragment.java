package com.example.taruc.instacity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link createFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link createFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class createFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters

    private EditText caption;
    private Button createNewPost;
    private ImageButton postImage;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostRef;
    private ProgressDialog loadingBar;
    private static final int Gallery_Pick=1;
    private Uri ImageUri;

    private StorageReference PostsImagesReference;
    private String saveCurrentDate,saveCurrentTime,post,downloadUrl;
    String currentUserID;
    LocationManager locationManager;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS=1;


    double latitude,longtitude;
    String str,userName,cap,userProfileImage;


    private OnFragmentInteractionListener mListener;

    public createFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment createFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static createFragment newInstance() {
        createFragment fragment = new createFragment();


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

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        loadingBar = new ProgressDialog(getActivity());
        PostsImagesReference = FirebaseStorage.getInstance().getReference();
        View view = inflater.inflate(R.layout.fragment_create, container, false);


        caption = (EditText)view.findViewById(R.id.captionEvent);

        createNewPost = (Button) view.findViewById(R.id.createButton);
        postImage = (ImageButton) view.findViewById(R.id.createImage);

        createNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputFieldValidation();
            }
        });

        postImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                OpenGallery();
            }
        });




        return view;
    }
    private void OpenGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(i, Gallery_Pick);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if(requestCode==Gallery_Pick&&resultCode==RESULT_OK&&data!=null){
            ImageUri = data.getData();
            postImage.setImageURI(ImageUri);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


   /* public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void StoringImageToFirebaseStorage() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate=currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime=currentTime.format(calFordTime.getTime());

        post= currentUserID+saveCurrentDate+saveCurrentTime;

        final StorageReference filePath = PostsImagesReference.child("Post Images").child(ImageUri.getLastPathSegment()+post+".jpg");
        filePath.putFile(ImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }

                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getActivity(),"Image uploaded successfully...",Toast.LENGTH_SHORT).show();
                    Uri downUri = task.getResult();
                    downloadUrl=downUri.toString();
                    Log.d("url", "onComplete: Url: "+ downUri.toString());
                    StoringFeedToDatabase();
                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(getActivity(),"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                }
            }
        });
       /* filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Image uploaded successfully...",Toast.LENGTH_SHORT).show();
                    UploadTask.TaskSnapshot url1 = task.getResult();
                    String url= task.getResult().toString();

                    Log.d("downloadUrl",task.get);
                    StoringFeedToDatabase();



                }else{
                    String message = task.getException().getMessage();
                    Toast.makeText(getActivity(),"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    }
    private void inputFieldValidation() {
        String cap = caption.getText().toString();

        if (TextUtils.isEmpty(cap)) {
            Toast.makeText(getActivity(), "Please enter caption...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating Post");
            loadingBar.setMessage("Please wait....");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();


        }
    }
    private void StoringFeedToDatabase() {



        UsersRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                if(dataSnapshot.exists()){

                    userName = dataSnapshot.child("userName").getValue().toString();
                    userProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    cap = caption.getText().toString();


                    locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }


                    //check the network provider is enabled
                    if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {

                                //get latitude
                                latitude=location.getLatitude();
                                //get longtitude
                                longtitude = location.getLongitude();
                                LatLng latlng = new LatLng(latitude,longtitude);
                                Toast.makeText(getActivity(),latlng.toString(),Toast.LENGTH_SHORT);
                                Geocoder geocoder = new Geocoder(getActivity(),Locale.getDefault());
                                try{
                                    List<Address> addressList = geocoder.getFromLocation(latitude,longtitude,1);
                                    String str = addressList.get(0).getAddressLine(0);
                                    str+=addressList.get(0).getLocality();
                                    HashMap postMap=new HashMap();
                                    postMap.put("uid",currentUserID);
                                    postMap.put("date",saveCurrentDate);
                                    postMap.put("time",saveCurrentTime);
                                    postMap.put("caption",cap);
                                    postMap.put("postImage",downloadUrl);
                                    postMap.put("userName",userName);
                                    postMap.put("profileImage",userProfileImage);
                                    postMap.put("location",str);


                                    PostRef.child(post).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(task.isSuccessful()){
                                                SendUserToMainActivity();

                                                Toast.makeText(getActivity(),"Your Posts is created successfully...",Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();

                                            }else{
                                                String message = task.getException().getMessage();
                                                Toast.makeText(getActivity(),"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });



//                                    Toast.makeText(getActivity(),latitude+longtitude+"",Toast.LENGTH_SHORT).show();
//                                    HashMap locationMap=new HashMap();
//                                    locationMap.put("latitude",latitude);
//                                    locationMap.put("longitude",longtitude);
//                                    locationMap.put("Location Name",str);
//
//                                    PostRef.child(post).child("Location").updateChildren(locationMap).addOnCompleteListener(new OnCompleteListener() {
//                                        @Override
//                                        public void onComplete(@NonNull Task task) {
//                                            if(task.isSuccessful()){
//                                                SendUserToMainActivity();
//                                                Toast.makeText(getActivity(),"Your Posts is created successfully...",Toast.LENGTH_SHORT).show();
//
//                                            }else{
//                                                String message = task.getException().getMessage();
//                                                Toast.makeText(getActivity(),"Error Occured:"+message,Toast.LENGTH_SHORT).show();
//                                                loadingBar.dismiss();
//                                            }
//                                        }
//                                    });


                                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,13F));
                                }catch (IOException e){
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
                    }else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                //get latitude
                                latitude=location.getLatitude();
                                //get longtitude
                                longtitude = location.getLongitude();
                                LatLng latlng = new LatLng(latitude,longtitude);

                                Geocoder geocoder = new Geocoder(getActivity(),Locale.getDefault());
                                try{
                                    List<Address> addressList = geocoder.getFromLocation(latitude,longtitude,1);
                                    str=addressList.get(0).getAddressLine(0)+",";
                                    str += addressList.get(0).getLocality()+",";
                                    str+= addressList.get(0).getCountryName();
                                    Toast.makeText(getActivity(),str,Toast.LENGTH_SHORT).show();

                                    HashMap locationMap=new HashMap();
                                    locationMap.put("latitude",latitude);
                                    locationMap.put("longitude",longtitude);
                                    locationMap.put("locationName",str);

                                    PostRef.child(post).child("Location").updateChildren(locationMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(task.isSuccessful()){
                                                SendUserToMainActivity();
                                                Toast.makeText(getActivity(),"Your Posts is created successfully...",Toast.LENGTH_SHORT).show();

                                            }else{
                                                String message = task.getException().getMessage();
                                                Toast.makeText(getActivity(),"Error Occured:"+message,Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });



                                }catch (IOException e){
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
                    };


                }else{
                    loadingBar.dismiss();

                    Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                }
            }
            public void onCancelled(DatabaseError databaseError){

            }
        });


    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(getActivity(),feed.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

    }

}
