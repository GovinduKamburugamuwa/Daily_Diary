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
    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button crtbtn;

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

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        crtbtn = findViewById(R.id.crtbtn);

        crtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    showToast("Please fill in all fields");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    showToast("Passwords do not match");
                    return;
                }

                if (usernameExists(username)) {
                    showToast("Username already exists");
                    return;
                }

                if (passwordExists(password)) {
                    showToast("Password already exists");
                    return;
                }

                if (saveCredentials(username, password)) {
                    showToast("Account has been created");
                    navigateToMainActivity2();
                } else {
                    showToast("Error creating account");
                }
            }
        });
    }

    private boolean usernameExists(String username) {
        return sharedPreferences.contains("username_" + username);
    }

    private boolean passwordExists(String password) {
        String encryptedPassword = encryptPassword(password);
        return sharedPreferences.getAll().containsValue(encryptedPassword);
    }

    private boolean saveCredentials(String username, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username_" + username, username);
        editor.putString("password_" + username, encryptPassword(password));
        return editor.commit();
    }

    private String encryptPassword(String password) {
        byte[] hash = DigestUtils.sha256(password);
        return Hex.encodeHexString(hash);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity2() {
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
        finish();
    }
}