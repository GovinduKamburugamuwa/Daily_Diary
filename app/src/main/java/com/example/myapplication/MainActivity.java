package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.databinding.ActivityMainHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements SettingsFragment.OnSettingsSubmitListener {
    private ActivityMainHomeBinding binding;
    private SharedPreferences sharedPreferences;
    private String currentUsername;
    private NoteDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("loggedInUser", "Default");
        dbHelper = new NoteDbHelper(this);

        if (currentUsername != null) {
            HomeFragment homeFragment = new HomeFragment();
            Bundle args = new Bundle();
            args.putString("username", currentUsername);
            homeFragment.setArguments(args);
            replaceFragment(homeFragment);
        } else {
            replaceFragment(new HomeFragment());
        }

        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.views) {
                replaceFragment(new ViewScreenFragment());
            } else if (itemId == R.id.settings) {
                replaceFragment(new SettingsFragment());
            } else if (itemId == R.id.accounts) {
                replaceFragment(new AccountFragment());
            }
            return true;
        });

        FloatingActionButton fabAddNote = findViewById(R.id.fab_add_note);
        fabAddNote.setOnClickListener(v -> {
            replaceFragment(new AddNoteFragment());
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragment instanceof ViewScreenFragment) {
            ((ViewScreenFragment) fragment).setDbHelper(dbHelper);
        }

        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onSettingsSubmit(String userName) {
        Toast.makeText(this, "Settings updated for: " + userName, Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUser", userName);
        editor.apply();
        currentUsername = userName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}