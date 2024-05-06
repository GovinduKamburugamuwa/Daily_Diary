package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeScreen extends AppCompatActivity {

    Button addrbtn, shwvwbtn, setbtn;
    TextView text;
    ImageView dailyImageView;
    private static final String UNSPLASH_API_KEY = "wmhhlkg5YGHN1iioZxPoRt0_TaGkFVDldYk9jAZek-s";
    private static final String UNSPLASH_API_URL = "https://api.unsplash.com/photos/random";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addrbtn = findViewById(R.id.addrbtn);
        shwvwbtn = findViewById(R.id.shwvwbtn);
        setbtn = findViewById(R.id.setbtn);
        dailyImageView = findViewById(R.id.dailyImageView);
        text = findViewById(R.id.textView3);

        addrbtn.setOnClickListener(v -> navigateToAddNoteActivity());
        shwvwbtn.setOnClickListener(v -> navigateToItemView());
        setbtn.setOnClickListener(v -> navigateToSettings());

        loadRandomNatureImage();
        updateWelcomeText();
    }

    private void navigateToAddNoteActivity() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    private void navigateToItemView() {
        Intent intent = new Intent(this, ViewScreen.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivityForResult(intent, 1);
    }

    private void loadRandomNatureImage() {
        String url = UNSPLASH_API_URL + "?query=nature&client_id=" + UNSPLASH_API_KEY;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONObject urlsObject = response.getJSONObject("urls");
                String imageUrl = urlsObject.getString("regular");
                Glide.with(this)
                        .load(imageUrl)
                        .into(dailyImageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // Handle error
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void updateWelcomeText() {
        String defaultName = "Govindu";
        text.setText("Welcome " + defaultName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String userName = data.getStringExtra("userName");
            text.setText("Welcome " + userName);
        }
    }
}