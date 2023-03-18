package com.example.spot_fnder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class reg extends AppCompatActivity {
    private EditText emailTextView, passwordTextView,confirmpassedittext,Nametext;
    private Button Btn,movetolog;
    private static final String Tag = "Register In Activity";
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
// ...

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reg);



        movetolog=findViewById(R.id.logi);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        Btn = findViewById(R.id.btnregister);
        confirmpassedittext=findViewById(R.id.cpass);
        Nametext=findViewById(R.id.name);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        movetolog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(reg.this,log.class);
                startActivity(intent);
            }
        });

        ImageView showhidepass1=findViewById(R.id.showhide1);
        showhidepass1.setImageResource(R.drawable.ic_baseline_visibility_24);
        showhidepass1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordTextView.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    passwordTextView.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showhidepass1.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                }else {
                    passwordTextView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showhidepass1.setImageResource(R.drawable.ic_baseline_visibility_24);
                }
            }
        });
        ImageView showhidepass2=findViewById(R.id.showhide2);
        showhidepass2.setImageResource(R.drawable.ic_baseline_visibility_24);
        showhidepass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmpassedittext.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    confirmpassedittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showhidepass2.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                }else {
                    confirmpassedittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showhidepass2.setImageResource(R.drawable.ic_baseline_visibility_24);
                }
            }
        });
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String cpass=confirmpassedittext.getText().toString();
                String email=emailTextView.getText().toString();
                String pass=passwordTextView.getText().toString();
                String name=Nametext.getText().toString();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(reg.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    emailTextView.setError("enter an email");
                    emailTextView.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(reg.this, "Please enter an valid email", Toast.LENGTH_SHORT).show();
                    emailTextView.setError("enter an valid email");
                    emailTextView.requestFocus();
                }else if (TextUtils.isEmpty(pass)){
                    Toast.makeText(reg.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    passwordTextView.setError("enter a password");
                    passwordTextView.requestFocus();
                }else if (TextUtils.isEmpty(name)){
                    Toast.makeText(reg.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    Nametext.setError("please enter your name");
                    Nametext.requestFocus();
                }
                else if (pass.length()<6){
                    Toast.makeText(reg.this, "Please enter a password that has more than 6 digits", Toast.LENGTH_SHORT).show();
                    passwordTextView.setError("enter a password that has more than 6 digits");
                    passwordTextView.requestFocus();
                }else if (TextUtils.isEmpty(cpass)){
                    Toast.makeText(reg.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    confirmpassedittext.setError("enter a password");
                    confirmpassedittext.requestFocus();
                }else if (!pass.equals(cpass)){
                    Toast.makeText(reg.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    confirmpassedittext.setError("passwords does not match");
                    confirmpassedittext.requestFocus();

                    passwordTextView.clearComposingText();
                    confirmpassedittext.clearComposingText();
                }else
                {

                    registerNewUser(pass,email,name);
                }
            }

        });

    }



    private void registerNewUser(String pass, String email, String name)
    {


        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(reg.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser=auth.getCurrentUser();

                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    ReadWriteUserDeatails writeUserDeatails = new  ReadWriteUserDeatails(email,pass,name);
                    DatabaseReference referenceprofile = FirebaseDatabase.getInstance().getReference("register users");
                    referenceprofile.child(firebaseUser.getUid()).setValue(writeUserDeatails).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {




                                Toast.makeText(reg.this, "User has been created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(reg.this,profilepicture.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast.makeText(reg.this," Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        passwordTextView.setError("Your password is too weak");
                        passwordTextView.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emailTextView.setError("this invalid or already in use");
                        emailTextView.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        emailTextView.setError("this email is already signhed in plz enter diffrent email");
                        emailTextView.requestFocus();
                    } catch (Exception e) {
                        Log.e(Tag, e.getMessage());
                        Toast.makeText(reg.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }
}