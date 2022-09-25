package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
public  static  String LOGE="LOGE";
    private EditText edtUsername,edtPassword,edtEmail;
    private Button btnSubmit;
    private TextView txtLoginInfo;

    private  boolean isSigningUp =true;
DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername= findViewById(R.id.edtUsername);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtLoginInfo = findViewById(R.id.txtLoginInfo);
       if (FirebaseAuth.getInstance().getCurrentUser()!=null){
           startActivity(new Intent(MainActivity.this,FriendsActivity.class));
           Log.e(LOGE,"user currently login");
//           Toast.makeText(MainActivity.this,FirebaseAuth.getInstance().getCurrentUser().getUid()+" ",Toast.LENGTH_SHORT).show();



           finish();
       }else{
           Log.e(LOGE,"no user currently login");
       }



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubmit.setClickable(false);
Log.e(LOGE,"btnSubmit clicked");
               if (edtEmail.getText().toString().trim().isEmpty() || edtPassword.getText().toString().isEmpty()){
                   if (isSigningUp && edtUsername.getText().toString().trim().isEmpty()){
                       Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                       return;
                   }
               }

                if (isSigningUp){
                    handleSignUp();
                }else{
                    handleLogin();
                }
            }
        });

        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (isSigningUp) {
                  isSigningUp = false;
                  edtUsername.setVisibility(View.GONE);
                  btnSubmit.setText("Log in");
                  txtLoginInfo.setText("Don't have an account? Sign up");
              }else {
                  isSigningUp =true;
                  edtUsername.setVisibility(View.VISIBLE);
                  btnSubmit.setText("Sign up");
                  txtLoginInfo.setText("Already have an account? Log in");
              }
            }
        });

    }

    private void handleSignUp(){
        Log.e(LOGE,"btnSubmit handleSignUp");

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString().trim(),edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
public void onComplete(@NonNull Task<AuthResult> task) {
    if (task.isSuccessful()){
//    FirebaseDatabase
//    .getInstance()        .getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid())
//            .setValue(new User(edtUsername.getText().toString().trim(),edtEmail.getText().toString().trim(),""));
        databaseReference=FirebaseDatabase.getInstance()
        .getReference("user/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/");
        databaseReference.setValue(
        new User(edtUsername.getText().toString().trim(),
            edtEmail.getText().toString().trim(),""))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
Toast.makeText(getApplicationContext(),"success",Toast.LENGTH_SHORT).show();
                            Log.e(LOGE,"databaseReference.setValue onSuccess");
                        }
                    })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"failed",Toast.LENGTH_SHORT).show();
                        Log.e(LOGE,"databaseReference.setValue onFailure");
                    }
                })
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"completed",Toast.LENGTH_SHORT).show();
                Log.e(LOGE,"databaseReference.setValue onComplete");
            }
        });

        startActivity(new Intent(MainActivity.this,FriendsActivity.class));
        Toast.makeText(MainActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
        btnSubmit.setClickable(true);
    }else{
        Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

        btnSubmit.setClickable(true); }
}
        });
    }

    private void handleLogin(){

        Log.e(LOGE,"btnSubmit handleLogin");
        Toast.makeText(MainActivity.this, "wait...", Toast.LENGTH_SHORT).show();
      try  {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString().trim(),
                    edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(MainActivity.this, FriendsActivity.class));
                        Toast.makeText(MainActivity.this, "Logged" +
                                " in successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                        Log.e("ajay", edtEmail.getText().toString().trim());
                        btnSubmit.setClickable(true);
                    }


                }

            });
        }catch (Exception e) {
          Log.e(LOGE, edtEmail.toString());
          btnSubmit.setClickable(true);
      }
    }
}