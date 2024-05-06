package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class Login extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        EditText passwordEditText = findViewById(R.id.pass);
        Button crtbtn = findViewById(R.id.crtbtn);

        crtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                if (savePasswordLocally(password)) {
                    showToast("Account has been created");
                    navigateToMainActivity2();
                } else {
                    showToast("Password already exists");
                }
            }
        });
    }

    private boolean savePasswordLocally(String password) {
        if (sharedPreferences.contains("password")) {
            String savedPassword = sharedPreferences.getString("password", "");
            if (savedPassword.equals(encryptPassword(password))) {
                return false; // Password already exists
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("password", encryptPassword(password));
        editor.commit(); // Use commit() instead of apply()
        return true; // Password saved successfully
    }

    private String encryptPassword(String password) {
        // Use SHA-256 to encrypt the password
        byte[] hash = DigestUtils.sha256(password);
        return Hex.encodeHexString(hash);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity2() {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }
}