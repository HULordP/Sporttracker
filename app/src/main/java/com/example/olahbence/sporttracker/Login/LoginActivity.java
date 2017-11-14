package com.example.olahbence.sporttracker.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.olahbence.sporttracker.MainMenu.MainActivity;
import com.example.olahbence.sporttracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra("Login", true);
                startActivity(i);
                finish();
            } else {
                setContentView(R.layout.activity_login);
                Toolbar myToolbar = findViewById(R.id.toolbar);
                setSupportActionBar(myToolbar);
                getSupportActionBar().setTitle("Login");
                mEmail = findViewById(R.id.email);
                mPassword = findViewById(R.id.password);
            }
        } else {
            setContentView(R.layout.activity_login);
            Toolbar myToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle("Login");
            mEmail = findViewById(R.id.email);
            mPassword = findViewById(R.id.password);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void login(View view) {
        mAuth.signInWithEmailAndPassword(
                mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            showText(getString(R.string.auth_failed));
                        } else {
                            if (user.isEmailVerified()) {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra("Login", true);
                                startActivity(i);
                                finish();
                            } else {
                                showText("Please verify your email");
                            }
                        }
                    }
                });
    }

    public void register(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    private void showText(String text) {
        LinearLayout linearLayout = findViewById(R.id.lin_lay);
        Snackbar.make(linearLayout, text, Snackbar.LENGTH_LONG).show();
    }
}
