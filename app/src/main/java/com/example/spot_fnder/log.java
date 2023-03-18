package com.example.spot_fnder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class log extends Activity {
    private static final String Tag = "Log In Activity";
    private EditText editTextemail, editTextlogpass;
    FirebaseAuth authprofile;
    SupportMapFragment smf;
    TextView forgetpass;
    public ProgressDialog loginprogress;
    private Toolbar mtoolbar;
    Button buttonlogin,movetosignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        editTextemail = findViewById(R.id.logemail);
        editTextlogpass = findViewById(R.id.logpassword);
        authprofile = FirebaseAuth.getInstance();
        ImageView showhidepass = findViewById(R.id.showhide);
        showhidepass.setImageResource(R.drawable.ic_baseline_visibility_24);
        forgetpass = findViewById(R.id.forgotpass);
        showhidepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextlogpass.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    editTextlogpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showhidepass.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                } else {
                    editTextlogpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showhidepass.setImageResource(R.drawable.ic_baseline_visibility_24);
                }
            }
        });

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }

            ProgressDialog loadingBar;

            private void showRecoverPasswordDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(log.this);
                builder.setTitle("Recover Password");
                LinearLayout linearLayout = new LinearLayout(log.this);
                final EditText emailet = new EditText(log.this);

                // write the email using which you registered
                emailet.setText("Email");
                emailet.setMinEms(16);
                emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                linearLayout.addView(emailet);
                linearLayout.setPadding(10, 10, 10, 10);
                builder.setView(linearLayout);

                // Click on Recover and a email will be sent to your registered email id
                builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = emailet.getText().toString().trim();
                        beginRecovery(email);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }

            private void beginRecovery(String email) {
                loadingBar = new ProgressDialog(log.this);
                loadingBar.setMessage("Sending Email....");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                // calling sendPasswordResetEmail
                // open your email and write the new
                // password and then you can login
                authprofile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if (task.isSuccessful()) {
                            // if isSuccessful then done message will be shown
                            // and you can change the password
                            Toast.makeText(log.this, "Done sent", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(log.this, "Error Occurred", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(log.this, "Error Failed", Toast.LENGTH_LONG).show();
                    }
                });


                movetosignin = findViewById(R.id.sgin2);
                movetosignin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(log.this, reg.class);
                        startActivity(intent);
                    }
                });


                buttonlogin = findViewById(R.id.loginb);
                buttonlogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String textmail = editTextemail.getText().toString();
                        String enterpass = editTextlogpass.getText().toString();

                        if (TextUtils.isEmpty(textmail)) {
                            Toast.makeText(log.this, "please enter your email", Toast.LENGTH_SHORT).show();
                            editTextemail.setError("email is required");
                            editTextemail.requestFocus();
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(textmail).matches()) {
                            Toast.makeText(log.this, "please re enter your email", Toast.LENGTH_SHORT).show();
                            editTextemail.setError(" valid email is required");
                            editTextemail.requestFocus();
                        } else if (TextUtils.isEmpty(enterpass)) {
                            Toast.makeText(log.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                            editTextlogpass.setError("password is required");
                            editTextlogpass.requestFocus();
                        } else {
                            loginUser(textmail, enterpass);

                        }
                    }
                });

            }

            private void loginUser(String mail, String pass) {
                authprofile.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(log.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            Toast.makeText(log.this, "You are loged in now", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(log.this, Mainpage.class);
                            startActivity(intent);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                editTextemail.setError("user does not exist or no longer valid plz sighn in again");
                                editTextemail.requestFocus();
                            } catch (Exception e) {

                                Log.e(Tag, e.getMessage());
                                Toast.makeText(log.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }


                            Toast.makeText(log.this, "Some thing went wrong try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });
    }
}