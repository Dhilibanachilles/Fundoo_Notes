package com.example.loginapp.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.loginapp.R;
import com.example.loginapp.SharedPreference;
import com.example.loginapp.authentication.LoginActivity;
import com.example.loginapp.fragments.FragmentArchive;
import com.example.loginapp.fragments.FragmentNotes;
import com.example.loginapp.fragments.FragmentRemainder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.loginapp.R.id.navigateNotes;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuthenticator;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private DrawerLayout drawer;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreference = new SharedPreference(this);
        drawer = findViewById(R.id.drawer_layout);
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
            } else if(item.getItemId() == R.id.navigateDelete) {
                Toast.makeText(this, "Select the notes", Toast.LENGTH_SHORT).show();
            } else if(item.getItemId() == R.id.navigateHelp) {
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sharedPreference.setLoggedIn(false);
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