package com.example.taruc.instacity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class editProfileActivity extends AppCompatActivity{
    private Button econfirm;
    private FirebaseAuth mAuth;
    private EditText Edit_username, Edit_fullname, Edit_ic, Edit_phonenum;
    private DatabaseReference Ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();

        Edit_username = (EditText) findViewById(R.id.edit_username);
        Edit_fullname = (EditText) findViewById(R.id.edit_fullname);
        Edit_ic = (EditText) findViewById(R.id.edit_ic);
        Edit_phonenum = (EditText) findViewById(R.id.edit_phonenum);
        econfirm = (Button) findViewById(R.id.edit_prof_confirm);

        econfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                UpdateUserDetails();
            }
        });
    }

    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //editProfileActivity.this.finish();
        }
    }

    private void UpdateUserDetails(){
        final String Newusername=Edit_username.getText().toString();
        final String Newfullname = Edit_fullname.getText().toString();
        final String Newic = Edit_ic.getText().toString();
        final String Newphonenum = Edit_phonenum.getText().toString();
        Ref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                HashMap userMap=new HashMap();
                if(Newusername.length()!=0) {
                    userMap.put("userName", Newusername);
                }if(Newfullname.length()!=0) {
                    userMap.put("fullName", Newfullname);
                }if(Newic.length()!=0) {
                    userMap.put("icNumber", Newic);
                }if(Newphonenum.length()!=0) {
                    userMap.put("contactNumber", Newphonenum);
                }
                Ref.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            editProfileActivity.this.finish();
                            Toast.makeText(editProfileActivity.this,"Updated Successfully!",Toast.LENGTH_SHORT).show();
                        }else{
                            String message = task.getException().getMessage();
                            editProfileActivity.this.finish();
                        }
                    }
                });
    }

//    private void SendToUserProfile() {
//        Intent setupIntent = new Intent(this,profileFragment.class);
//        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(setupIntent);
//        finish();
//    }

    public void backClick(View view) {
        editProfileActivity.this.finish();
    }


}
