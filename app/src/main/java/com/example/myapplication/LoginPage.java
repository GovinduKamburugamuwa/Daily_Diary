package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils;

public class LoginPage extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        usernameEditText = findViewById(R.id.user2);
        passwordEditText = findViewById(R.id.pass2);
        loginButton = findViewById(R.id.logbtn1);
        Button registerButton = findViewById(R.id.regbtn);

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, Login.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Please enter both username and password.");
                return;
            }

            if (checkCredentials(username, password)) {
                saveLoggedInUser(username);  // Save the logged-in user
                navigateToMainActivity2(username);
            } else {
                showToast("Invalid username or password.");
            }
        });
    }

    private boolean checkCredentials(String username, String password) {
        String storedPassword = sharedPreferences.getString("password_" + username, null);
        if (storedPassword != null) {
            String encryptedPassword = encryptPassword(password);
            return storedPassword.equals(encryptedPassword);
        }
        return false;
    }

    private String encryptPassword(String password) {
        byte[] hash = DigestUtils.sha256(password);
        return Hex.encodeHexString(hash);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity2(String username) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish(); // Close the login activity
    }

    // Save the logged-in user in SharedPreferences
    private void saveLoggedInUser(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedInUser", username);
        editor.apply();
    }
}
