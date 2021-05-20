package com.example.loginapp.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.loginapp.R;
import com.example.loginapp.authentication.LoginActivity;
import com.example.loginapp.data_manager.FirebaseUserManager;
import com.example.loginapp.data_manager.SharedPreferenceHelper;
import com.example.loginapp.data_manager.model.FirebaseUserModel;
import com.example.loginapp.fragments.FragmentArchive;
import com.example.loginapp.fragments.FragmentRemainder;
import com.example.loginapp.fragments.notes.FragmentNotes;
import com.example.loginapp.util.CallBack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.Objects;
import static com.example.loginapp.R.id.navigateNotes;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuthenticator;
    private DrawerLayout drawer;
    SharedPreferenceHelper sharedPreferenceHelper;
    private final FirebaseUserManager firebaseUserManager = new FirebaseUserManager();
    ProgressBar pictureProgressbar;
    StorageReference storageReference;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferenceHelper = new SharedPreferenceHelper(this);
        drawer = findViewById(R.id.drawer_layout);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuthenticator = FirebaseAuth.getInstance();
        NavigationView navigationView = findViewById(R.id.design_navigation_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
            new FragmentNotes()).commit();
            navigationView.setCheckedItem(navigateNotes);
        }

        View headerView = navigationView.getHeaderView(0);
        TextView userEmail = headerView.findViewById(R.id.user_email);
        ImageView userDisplayPic = headerView.findViewById(R.id.user_profile);
        FloatingActionButton changePicture = headerView.findViewById(R.id.change_pic_button);
        pictureProgressbar = headerView.findViewById(R.id.progressbar_of_profile_upload);
        firebaseUserManager.getUserDetails(new CallBack<FirebaseUserModel>() {
            @Override
            public void onSuccess(FirebaseUserModel data) {
                userEmail.setText(data.getUserEmail());
                Toast.makeText(HomeActivity.this, "Logged in ",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onSuccess: " );
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(HomeActivity.this,
                        "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        changePicture.setOnClickListener(v -> {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGalleryIntent,1000);
        });
        StorageReference profileRef = storageReference.child("users/"+
        (Objects.requireNonNull(firebaseAuthenticator.getCurrentUser())).getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().
                load(uri).into(userDisplayPic));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.
            Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        firebaseAuthenticator = FirebaseAuth.getInstance();
        final StorageReference fileRef = storageReference.
                child("users/"+ Objects.requireNonNull(firebaseAuthenticator
                        .getCurrentUser()).getUid()+"/profile.jpg");
        pictureProgressbar.setVisibility(View.VISIBLE);
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.
                getDownloadUrl().addOnSuccessListener(uri -> {
                    ImageView userDisplayPic = findViewById(R.id.user_profile);
                    Picasso.get().load(uri).into(userDisplayPic);
                    pictureProgressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(HomeActivity.this, "Picture Updated", Toast.LENGTH_SHORT).show();
                })).addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                "Failed.", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.getItemId();
            if(item.getItemId() == R.id.navigateNotes) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentNotes()).commit();
            } else if(item.getItemId() == R.id.navigateRemainder) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentRemainder()).commit();
            } else if(item.getItemId() == R.id.navigateArchive) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FragmentArchive()).commit();
            } else if(item.getItemId() == R.id.navigateLogout) {
              logout();
            } else if(item.getItemId() == R.id.navigateHelp) {
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sharedPreferenceHelper.setLoggedIn(false);
        finish();
        Intent intToMain = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intToMain);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}