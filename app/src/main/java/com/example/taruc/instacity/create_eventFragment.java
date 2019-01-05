package com.example.taruc.instacity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;



public class create_eventFragment extends Fragment {

    private Spinner locationSpinner;
    private EditText titleEvent;
    private TextView eventDate;
    private TextView eventSTime;
    private TextView eventETime;
    private EditText captionEvent;
    private Button postButton;
    private ImageButton postImage;
    private static final int PLACE_PICKER_REQUEST = 1;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostRef;
    private ProgressDialog loadingBar;
    private static final int Gallery_Pick=1;
    private Uri ImageUri;
    private DatePickerDialog.OnDateSetListener mDateListener;
    private TimePickerDialog.OnTimeSetListener mETimeListerner;
    private TimePickerDialog.OnTimeSetListener mSTimeListerner;
    private StorageReference PostsImagesReference;
    private String saveCurrentDate,saveCurrentTime,post,downloadUrl;
    private String EventLocation;
    private String EventLocationShort;
    private String EventLngLtl;

    String currentUserID;


    private OnFragmentInteractionListener mListener;

    public create_eventFragment() {
        // Required empty public constructor
    }


    public static create_eventFragment newInstance() {
        create_eventFragment fragment = new create_eventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_event, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        PostRef = FirebaseDatabase.getInstance().getReference().child("Events");
        loadingBar = new ProgressDialog(getActivity());
        PostsImagesReference = FirebaseStorage.getInstance().getReference();
        captionEvent = (EditText)view.findViewById(R.id.captionEvent);
        titleEvent = view.findViewById(R.id.eventName);
        eventDate = view.findViewById(R.id.eventDate);
        eventSTime= view.findViewById(R.id.eventStartTime);
        eventETime = view.findViewById(R.id.eventEndTime);
        locationSpinner = view.findViewById(R.id.locationEventSpinner);
        postButton = (Button) view.findViewById(R.id.postButton);
        postImage = (ImageButton) view.findViewById(R.id.createEventImage);
        postButton.setOnClickListener(new View.OnClickListener() {
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
        //Location spinner indicate
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(locationSpinner.getSelectedItem().toString().toLowerCase().equals("others")){
                    Toast.makeText(getContext(), "Choose a location on Map", Toast.LENGTH_LONG).show();
                    // go to maps activity

                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        Intent intent = builder.build(getActivity());
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Date picker intialize
        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dateDialog = new DatePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,mDateListener,year,month,day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();
            }
        });
            mDateListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month+=1;
                    String date = month+"/"+dayOfMonth+"/"+year;
                    eventDate.setText("EventDate: "+date);
                }
            };
//End time intialize
        eventETime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);


                TimePickerDialog timeDialog = new TimePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,mETimeListerner,hour,minute,true);
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timeDialog.show();
            }
        });
        mETimeListerner = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String EndTime = String.format("%02d",hourOfDay)+":"+String.format("%02d",minute);
                eventETime.setText("EndTime:"+EndTime);
            }
        };
        // start time initialize
        eventSTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);


                TimePickerDialog timeDialog = new TimePickerDialog(getContext(),android.R.style.Theme_Holo_Light_Dialog_NoActionBar,mSTimeListerner,hour,minute,true);
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timeDialog.show();
            }
        });
        mSTimeListerner = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String StartTime = String.format("%02d",hourOfDay)+":"+String.format("%02d",minute);
                eventSTime.setText("StartTime :"+StartTime);
            }
        };
        return view;
        // Inflate the layout for this fragment

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void OpenGallery() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(i, Gallery_Pick);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(),data);
                String toastMsg = String.format("Place: %s", place.getName());
                EventLocation = place.getAddress().toString();
                EventLocationShort = place.getName().toString();
                EventLngLtl = place.getLatLng().toString();
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode==Gallery_Pick&&resultCode==RESULT_OK&&data!=null){
            ImageUri = data.getData();
            postImage.setImageURI(ImageUri);
        }
    }
    private void inputFieldValidation() {
        Log.d("hihi","testing 1 2 33");
        String cap = captionEvent.getText().toString();

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

        private void StoringImageToFirebaseStorage(){
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());
            Log.d("hihi","testing 1 2 3 4");
            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = currentTime.format(calFordTime.getTime());

            post = currentUserID+saveCurrentDate + saveCurrentTime;

            final StorageReference filePath = PostsImagesReference.child("Event Images").child(ImageUri.getLastPathSegment() + post + ".jpg");
            filePath.putFile(ImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Image uploaded successfully...", Toast.LENGTH_SHORT).show();
                        Uri downUri = task.getResult();
                        downloadUrl = downUri.toString();
                        Log.d("url", "onComplete: Url: " + downUri.toString());

                        StoringEventToDatabase();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(getActivity(), "Error Occured:" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    private void StoringEventToDatabase() {
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("userName").getValue().toString();
                    Log.d("hihi","testing 1111");
                    String cap = captionEvent.getText().toString();
                    String sTime = eventSTime.getText().toString();
                    String eTime = eventETime.getText().toString();
                    String date = eventDate.getText().toString();
                    String eventTitle = titleEvent.getText().toString();
                    String location = locationSpinner.getSelectedItem().toString();
                    HashMap postMap = new HashMap();
                    postMap.put("uid", currentUserID);
                    postMap.put("userName", userName);
                    postMap.put("uploadDate", saveCurrentDate);
                    postMap.put("uploadTime", saveCurrentTime);
                    postMap.put("eventCaption", cap);
                    postMap.put("eventImage", downloadUrl);
                    postMap.put("eventDate",date);
                    postMap.put("startTime",sTime);
                    postMap.put("endTime",eTime);
                    postMap.put("eventTitle",eventTitle);
                    postMap.put("location",location);

                    Log.d("hihi","testing 222 ");
                    PostRef.child(post).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                               // SendUserToEventActivity();
                                Log.d("hihi","testing 333");
                                Toast.makeText(getActivity(), "Your Posts is created successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(getActivity(), "Error Occured:" + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                } else {
                    loadingBar.dismiss();

                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
            public void onCancelled(DatabaseError databaseError){

            }
        });
        }
   /* private void SendUserToEventActivity() {
        Intent mainIntent = new Intent(getActivity(),feed.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

    }*/

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
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}



