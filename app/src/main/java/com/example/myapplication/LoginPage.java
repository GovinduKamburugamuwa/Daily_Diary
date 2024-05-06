package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils;

public class LoginPage extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        passwordEditText = findViewById(R.id.pass2);
        loginButton = findViewById(R.id.logbtn1);
        Button registerButton = findViewById(R.id.regbtn);

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, Login.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString();
            if (checkUserExists(password)) {
                navigateToMainActivity2();
            } else {
                showToast("User is not exist. Please create an account.");
            }
        });
    }

    private boolean checkUserExists(String password) {
        if (sharedPreferences.contains("password")) {
            String savedPassword = sharedPreferences.getString("password", "");
            String encryptedPassword = encryptPassword(password);
            return savedPassword.equals(encryptedPassword);
        }
        return false;
    }

    private String encryptPassword(String password) {
        // Use SHA-256 to encrypt the password
        byte[] hash = DigestUtils.sha256(password);
        return Hex.encodeHexString(hash);
    }

    private void navigateToMainActivity2() {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}