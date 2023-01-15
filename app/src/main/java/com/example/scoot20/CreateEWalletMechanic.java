package com.example.scoot20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateEWalletMechanic extends AppCompatActivity {
    FirebaseAuth authProfile = FirebaseAuth.getInstance();
    String profileID = authProfile.getCurrentUser().getUid();
    DatabaseReference databaseEwallet = FirebaseDatabase.getInstance().getReference("E-Wallet").child(profileID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ewallet_mechanic);

        Button BtnCreateEWallet = findViewById(R.id.BtnCreateEWalletMechanic);
        BtnCreateEWallet.setOnClickListener(v -> {
            Money money = new Money();
            databaseEwallet.setValue(money);
            startActivity(new Intent(this, MechanicEWallet.class));
            finish(); //Close SignIn
        });
    }
    //Check if User is already logged in. In such case, straightaway take the User to Home
    @Override
    protected void onStart(){
        super.onStart();
        databaseEwallet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Toast.makeText(CreateEWalletMechanic.this, "Already Have E-Wallet", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CreateEWalletMechanic.this, MechanicEWallet.class));
                    finish();
                } else {
                    Toast.makeText(CreateEWalletMechanic.this, "Create an E-Wallet Account", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

    }
}