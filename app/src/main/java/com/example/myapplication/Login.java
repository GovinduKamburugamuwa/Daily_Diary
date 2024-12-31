package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Hex;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Login extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private Button crtbtn;
    private ShapeableImageView profileImage;
    private FloatingActionButton fabAddPhoto;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    profileImage.setImageURI(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmPassword);
        crtbtn = findViewById(R.id.crtbtn);
        profileImage = findViewById(R.id.profileImage);
        fabAddPhoto = findViewById(R.id.fabAddPhoto);
    }

    private void setupListeners() {
        fabAddPhoto.setOnClickListener(v -> pickImage.launch("image/*"));

        crtbtn.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (validateInputs(username, password, confirmPassword)) {
                if (saveCredentialsAndImage(username, password)) {
                    showToast("Account has been created");
                    navigateToLoginPage();
                } else {
                    showToast("Error creating account");
                }
            }
        });
    }

    private boolean validateInputs(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill in all fields");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return false;
        }

        if (usernameExists(username)) {
            showToast("Username already exists");
            return false;
        }

        if (passwordExists(password)) {
            showToast("Password already exists");
            return false;
        }

        if (selectedImageUri == null) {
            showToast("Please select a profile image");
            return false;
        }

        return true;
    }

    private boolean saveCredentialsAndImage(String username, String password) {
        try {
            // Save the image
            String imagePath = saveImageToInternalStorage(username);

            // Save credentials and image path
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username_" + username, username);
            editor.putString("password_" + username, encryptPassword(password));
            editor.putString("image_" + username, imagePath);
            return editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String saveImageToInternalStorage(String username) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
        File directory = new File(getFilesDir(), "profile_images");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, username + "_profile.jpg");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.close();

        return file.getAbsolutePath();
    }

    private boolean usernameExists(String username) {
        return sharedPreferences.contains("username_" + username);
    }

    private boolean passwordExists(String password) {
        String encryptedPassword = encryptPassword(password);
        return sharedPreferences.getAll().containsValue(encryptedPassword);
    }

    private String encryptPassword(String password) {
        byte[] hash = DigestUtils.sha256(password);
        return Hex.encodeHexString(hash);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToLoginPage() {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
        finish();
    }
}