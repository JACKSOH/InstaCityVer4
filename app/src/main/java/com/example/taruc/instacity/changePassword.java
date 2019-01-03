package com.example.taruc.instacity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Ref;
import java.util.HashMap;

public class changePassword extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button confirm;
    private EditText old_pass, new_pass, retype_pass;
    private DatabaseReference Ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth = FirebaseAuth.getInstance();

        old_pass = (EditText) findViewById(R.id.old_pass);
        new_pass = (EditText) findViewById(R.id.new_pass);
        retype_pass = (EditText) findViewById(R.id.retype_pass);
        confirm = (Button) findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePassword();
            }
        });
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //editProfileActivity.this.finish();
        }
    }

    private void UpdatePassword() {
        final String Oldpass = old_pass.getText().toString();
        final String Newpass = new_pass.getText().toString();
        final String Newretypepass = retype_pass.getText().toString();
//        String current_user = mAuth.getCurrentUser().getUid();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        Ref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        if (Newretypepass.equals(Newpass)) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, Oldpass);

            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(Newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            changePassword.this.finish();
                                            Toast.makeText(changePassword.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            String message = task.getException().getMessage();
                                            changePassword.this.finish();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(changePassword.this, "Error auth failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void backClick(View view) {
        changePassword.this.finish();
    }
}
