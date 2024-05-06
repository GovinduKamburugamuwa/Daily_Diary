package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class AddNoteActivity extends AppCompatActivity {
    private EditText titleInput;
    private EditText descriptionInput;
    private NoteDbHelper dbHelper;
    private ImageButton imgbtn;
    private static final int PICK_IMAGE = 1;
    private long selectedDate = 0;
    private long selectedTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);


        dbHelper = new NoteDbHelper(this);
        titleInput = findViewById(R.id.titleinput);
        descriptionInput = findViewById(R.id.descriptioninput);
        MaterialButton saveBtn = findViewById(R.id.savebtn);
        imgbtn = findViewById(R.id.imgbtn);
        ImageButton datebtn = findViewById(R.id.datebtn);
        ImageButton timebtn = findViewById(R.id.timebtn);

        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNoteAndNavigateToItemView();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, monthOfYear, dayOfMonth);
                selectedDate = selectedCalendar.getTimeInMillis();
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedCalendar.set(Calendar.MINUTE, minute);
                selectedTime = selectedCalendar.getTimeInMillis();
            }
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri selectedImage = data.getData();
            imgbtn.setImageURI(selectedImage);
        }
    }

    private void saveNoteAndNavigateToItemView() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        long createdTime = System.currentTimeMillis();
        byte[] imageData;

        if (imgbtn.getDrawable() != null) {
            imageData = getBytesFromImageView(imgbtn);
        } else {
            // Load default image from resources
            BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.default_image);
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageData = stream.toByteArray();
        }

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please enter a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_TITLE, title);
        values.put(NoteContract.NoteEntry.COLUMN_DESCRIPTION, description);
        values.put(NoteContract.NoteEntry.COLUMN_CREATED_TIME, createdTime);
        values.put(NoteContract.NoteEntry.COLUMN_IMAGE_DATA, imageData);
        values.put(NoteContract.NoteEntry.COLUMN_DATE, selectedDate);
        values.put(NoteContract.NoteEntry.COLUMN_TIME, selectedTime);

        Log.d("AddNoteActivity", "Title: " + title);
        Log.d("AddNoteActivity", "Description: " + description);
        Log.d("AddNoteActivity", "Created Time: " + createdTime);
        Log.d("AddNoteActivity", "Image Data Size: " + (imageData != null ? imageData.length : 0));

        long newRowId = database.insert(NoteContract.NoteEntry.TABLE_NAME, null, values);
        if (newRowId == -1) {
            Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            navigateToItemView(title, description, createdTime, imageData);
        }
    }

    private byte[] getBytesFromImageView(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void navigateToItemView(String title, String description, long createdTime, byte[] imageData) {
        Intent intent = new Intent(this, ViewScreen.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        intent.putExtra("createdTime", createdTime);
        intent.putExtra("imageData", imageData);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }




}