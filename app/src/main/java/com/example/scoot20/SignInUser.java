package com.example.scoot20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class SignInUser extends AppCompatActivity {

    private EditText editTxtLoginEmail, editTxtLoginPassword;
    private FirebaseAuth authProfile;
    private static final String TAG = "SignIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_user);

        getSupportActionBar().setTitle("Login");

        editTxtLoginEmail = findViewById(R.id.editTxtLoginEmail);
        editTxtLoginPassword = findViewById(R.id.editTxtLoginPassword);

        authProfile = FirebaseAuth.getInstance();

        Button btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(SignInUser.this, "You can reset your password now", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignInUser.this, ForgotPassword.class));
            }
        });

        Button btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInUser.this, "You can Register now", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignInUser.this, RegisterUser.class));
            }
        });

        //SignIn User
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editTxtLoginEmail.getText().toString();
                String textPassword = editTxtLoginPassword.getText().toString();

                //if Email Address has not been entered
                if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(SignInUser.this, "Please enter your email", Toast.LENGTH_LONG).show();
                    editTxtLoginEmail.setError("Email is required");
                    editTxtLoginEmail.requestFocus();
                }
                //if Email Address is invalid
                else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(SignInUser.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTxtLoginEmail.setError("Valid email is required");
                    editTxtLoginEmail.requestFocus();
                }
                //if Password has not been entered
                else if (TextUtils.isEmpty(textPassword)){
                    Toast.makeText(SignInUser.this, "Please enter your password", Toast.LENGTH_LONG).show();
                    editTxtLoginPassword.setError("Password is required");
                    editTxtLoginPassword.requestFocus();
                }
                else {
                    signInUser(textEmail, textPassword);
                }
            }
        });
    }

    private void signInUser(String email, String password) {
        authProfile.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInUser.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    //Get instance of the current User
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    //Check if email is verified before user can access their profile
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(SignInUser.this, "You are Signed In now", Toast.LENGTH_SHORT).show();

                        //Open user Profile
                        //Start Home
                        startActivity(new Intent(SignInUser.this, MainActivity.class));
                        finish(); //Close the Sign In Activity

                    }
                    else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut(); //Sign out User
                        showAlertDialog();
                    }


                } else {
                    try{
                        throw task.getException();
                    }
                    //if User no longer exist or deleted by admin
                    catch(FirebaseAuthInvalidUserException e){
                        editTxtLoginEmail.setError("User does not exist or is no longer valid. Please register again");
                        editTxtLoginEmail.requestFocus();
                    }
                    //invalid credentials
                    catch(FirebaseAuthInvalidCredentialsException e){ //User input wrong credentials
                        editTxtLoginEmail.setError("Invalid credentials. Kindly, check and re-enter.");
                        editTxtLoginEmail.requestFocus();
                    }
                    catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignInUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showAlertDialog() {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInUser.this);

        //to notify Mechanic that email has not been verified
        builder.setTitle("Email Not Verified");

        //to remind Mechanic to verify his/her email
        builder.setMessage("Please verify your email now. You cannot login without email verification.");

        //Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To direct to external email app in new window
                startActivity(intent);
            }
        });

        //Create the AlertDialog
        AlertDialog alertDialog = builder.create();

        //Show the AlertDialog
        alertDialog.show();
    }

    //Check if User is already logged in. In such case, straightaway take the User to Home
    @Override
    protected void onStart(){
        super.onStart();
        if(authProfile.getCurrentUser() != null) {
            //to notify the Mechanic that he/she is already signed in
            Toast.makeText(SignInUser.this, "Already Signed In", Toast.LENGTH_LONG).show();

            //Start Home
            startActivity(new Intent(SignInUser.this, MainActivity.class));
            finish(); //Close SignIn

        }
        //to notify the Mechanic that he can sign in now
        else{
            Toast.makeText(SignInUser.this, "You can Sign In now", Toast.LENGTH_LONG).show();
        }
    }
}