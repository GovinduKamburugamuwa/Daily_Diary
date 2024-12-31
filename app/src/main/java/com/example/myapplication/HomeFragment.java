package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class HomeFragment extends Fragment {

    private TextView text;
    private ImageView dailyImageView;
    private static final String UNSPLASH_API_KEY = "wmhhlkg5YGHN1iioZxPoRt0_TaGkFVDldYk9jAZek-s";
    private static final String UNSPLASH_API_URL = "https://api.unsplash.com/photos/random";

    private String loggedInUsername;
    private SharedPreferences sharedPreferences;
    private ShapeableImageView profileImage;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_main2, container, false);

        dailyImageView = view.findViewById(R.id.dailyImageView);
        text = view.findViewById(R.id.textView3);
        profileImage = view.findViewById(R.id.profileImage);

        // Initialize SharedPreferences to retrieve the saved username
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", requireActivity().MODE_PRIVATE);
        loggedInUsername = sharedPreferences.getString("loggedInUser", "Default");


        loadProfileImage();
        loadRandomNatureImage();
        updateWelcomeText();

        return view;
    }
    private void loadProfileImage() {
        if (loggedInUsername != null && !loggedInUsername.equals("Default")) {
            String imagePath = sharedPreferences.getString("image_" + loggedInUsername, null);
            if (imagePath != null) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    if (bitmap != null && profileImage != null) {
                        profileImage.setImageBitmap(bitmap);
                    }
                }
            }
        }
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
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(request);
    }

    private void updateWelcomeText() {
        text.setText(loggedInUsername);
    }


}