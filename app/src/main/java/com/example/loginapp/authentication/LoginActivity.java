package com.example.loginapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginapp.R;
import com.example.loginapp.data_manager.SharedPreferenceHelper;
import com.example.loginapp.dashboard.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText emailId, password;
    private Button signIn;
    private SignInButton googleSignIn;
    TextView signUp, forgotPassword;
    private final String loginActivityTag = "LoginActivity";
    private final int RC_SIGN_IN = 1;
    FirebaseAuth firebaseAuthenticator;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String isLoggedIn = "Logged_In";
    GoogleSignInClient mGoogleSignInClient;
    SharedPreferenceHelper sharedPreferenceHelper;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        Objects.requireNonNull(getSupportActionBar()).hide();

        FirebaseUser user = firebaseAuthenticator.getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViews();
        setUpOnClickListeners();
        setForgotPassword();
    }

    private void findViews() {
        firebaseAuthenticator = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        signIn = findViewById(R.id.button);
        signUp = findViewById(R.id.textView);
        forgotPassword = findViewById(R.id.textView4);
        googleSignIn = findViewById(R.id.googleSignInButton);
        sharedPreferenceHelper = new SharedPreferenceHelper(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setUpOnClickListeners() {
        signIn.setOnClickListener(v -> isLoggingIn());

        signUp.setOnClickListener(v -> {
            Intent intSignUp = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intSignUp);
            finish();
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(loginActivityTag, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(loginActivityTag, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuthenticator.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        if (task.getResult().getAdditionalUserInfo().isNewUser()){
                            String userName = firebaseUser.getDisplayName();
                            String email = firebaseUser.getEmail();
                            Map<String, Object> user = new HashMap<>();
                            user.put("UserName", userName);
                            user.put("Email", email);
                            firebaseFirestore.collection("Users").document(firebaseUser.getUid()).set(user);
                        }
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(loginActivityTag, "signInWithCredential:success");
                        FirebaseUser user = firebaseAuthenticator.getCurrentUser();
                        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(loginActivityTag, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            String personName = account.getDisplayName();
            String personEmail = account.getEmail();
            Toast.makeText(LoginActivity.this,personName + personEmail ,Toast.LENGTH_SHORT).show();
        }
    }

    private void setForgotPassword() {
            forgotPassword.setOnClickListener(v -> {
                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email Address");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Reset", (dialog, which) -> {
                    String mail = resetMail.getText().toString();
                    firebaseAuthenticator.sendPasswordResetEmail(mail).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Please Check your Network Connection Or email Address", Toast.LENGTH_SHORT).show());
                });

                passwordResetDialog.setNegativeButton("Cancel", (dialog, which) -> {
                    // back to login page
                });

                passwordResetDialog.create().show();
            });
        }

        public void isLoggingIn() {
            String email = emailId.getText().toString();
            String pasword = password.getText().toString();
            if (email.isEmpty()) {
                emailId.setError("Please enter email id");
                emailId.requestFocus();
            } else if (pasword.isEmpty()) {
                password.setError("Please enter your password");
                password.requestFocus();
            } else if (!(email.isEmpty() && pasword.isEmpty())) {
                firebaseAuthenticator.signInWithEmailAndPassword(email, pasword).addOnCompleteListener(LoginActivity.this, task -> {
                    if (!task.isSuccessful()) {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            password.setError("Password is Wrong");
                            password.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            emailId.setError("Email Address is already Active in Another Device");
                            emailId.requestFocus();
                        } catch (FirebaseAuthInvalidUserException e) {
                            emailId.setError("Email Address is Not Registered");
                            emailId.requestFocus();
                        } catch (Exception e) {
                            Log.e(loginActivityTag, e.getMessage());
                        }
                    } else {
                        sharedPreferenceHelper.setLoggedIn(true);
                        finish();
                        Intent toHomePage = new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(toHomePage);
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "Error Occurred While logging in", Toast.LENGTH_SHORT).show();
            }
        }
    }



