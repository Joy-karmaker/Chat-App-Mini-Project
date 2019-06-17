package com.example.mini_messenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText pusername, pemail, ppassword;
    String Txt_username, Txt_email, Txt_password;
    Button bregister;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.Rtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pusername = findViewById(R.id.Rusername);
        pemail = findViewById(R.id.Remail);
        ppassword = findViewById(R.id.Rpassword);
        bregister = findViewById(R.id.Rregister);

        auth = FirebaseAuth.getInstance();

        //Txt_username = "joy";
        //Txt_email = "joykarmaker2065@gmail.com";
        //Txt_password = "1234567";


        bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Txt_username = pusername.getText().toString();
                    Txt_email = pemail.getText().toString();
                    Txt_password = ppassword.getText().toString();

                    Toast.makeText(RegisterActivity.this, Txt_username+Txt_email+Txt_password, Toast.LENGTH_LONG).show();

                    if(TextUtils.isEmpty(Txt_username) || TextUtils.isEmpty(Txt_email) || TextUtils.isEmpty(Txt_password)) {
                        Toast.makeText(RegisterActivity.this, "No blank field", Toast.LENGTH_LONG).show();
                    }
                    else {
                        register(Txt_username, Txt_email, Txt_password);
                    }
            }
        });


    }

    private void register(final String username, final String email, final String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", username);
                    hashMap.put("imageURL", "default");
                    hashMap.put("search", username.toLowerCase());

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this, "You can't", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
