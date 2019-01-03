package com.example.taruc.instacity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class editProfileOptionActivity extends AppCompatActivity {
    private Button edit_prof, change_pass;
    private FirebaseAuth mAuth;
    private DatabaseReference Ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_option);

        mAuth = FirebaseAuth.getInstance();

        edit_prof = (Button) findViewById(R.id.edit_prof_detail);
        change_pass = (Button) findViewById(R.id.change_pass);


        edit_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToEditUserProfile();
            }
        });

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendToChangePass();
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

    private void SendToEditUserProfile () {
        Intent setupIntent = new Intent(this, editProfileActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendToChangePass () {
        Intent setupIntent = new Intent(this, changePassword.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    public void backClick(View view) {
        editProfileOptionActivity.this.finish();
    }
}

