package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class EditNoteFragment extends Fragment {
    private EditText titleInput;
    private EditText descriptionInput;
    private NoteDbHelper dbHelper;
    private ImageButton imgbtn;
    private long noteId;
    private long selectedDate = 0;
    private long selectedTime = 0;
    private static final int PICK_IMAGE = 1;

    public static EditNoteFragment newInstance(long noteId, String title, String description, byte[] imageData, long date, long time) {
        EditNoteFragment fragment = new EditNoteFragment();
        Bundle args = new Bundle();
        args.putLong("noteId", noteId);
        args.putString("title", title);
        args.putString("description", description);
        args.putByteArray("imageData", imageData);
        args.putLong("date", date);
        args.putLong("time", time);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_note, container, false);

        dbHelper = new NoteDbHelper(requireContext());
        titleInput = view.findViewById(R.id.titleinput);
        descriptionInput = view.findViewById(R.id.descriptioninput);
        MaterialButton saveBtn = view.findViewById(R.id.savebtn);
        imgbtn = view.findViewById(R.id.imgbtn);
        ImageButton datebtn = view.findViewById(R.id.datebtn);
        ImageButton timebtn = view.findViewById(R.id.timebtn);

        if (getArguments() != null) {
            noteId = getArguments().getLong("noteId");
            titleInput.setText(getArguments().getString("title"));
            descriptionInput.setText(getArguments().getString("description"));
            byte[] imageData = getArguments().getByteArray("imageData");
            if (imageData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                imgbtn.setImageBitmap(bitmap);
            }
            selectedDate = getArguments().getLong("date");
            selectedTime = getArguments().getLong("time");
        }

        imgbtn.setOnClickListener(v -> openGallery());
        datebtn.setOnClickListener(v -> showDatePicker());
        timebtn.setOnClickListener(v -> showTimePicker());
        saveBtn.setOnClickListener(v -> updateNoteAndFinish());

        return view;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri selectedImage = data.getData();
            imgbtn.setImageURI(selectedImage);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year1, monthOfYear, dayOfMonth);
            selectedDate = selectedCalendar.getTimeInMillis();
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute1) -> {
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedCalendar.set(Calendar.MINUTE, minute1);
            selectedTime = selectedCalendar.getTimeInMillis();
        }, hour, minute, false);

        timePickerDialog.show();
    }

    private void updateNoteAndFinish() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        byte[] imageData = getBytesFromImageView(imgbtn);

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_TITLE, title);
        values.put(NoteContract.NoteEntry.COLUMN_DESCRIPTION, description);
        values.put(NoteContract.NoteEntry.COLUMN_IMAGE_DATA, imageData);
        values.put(NoteContract.NoteEntry.COLUMN_DATE, selectedDate);
        values.put(NoteContract.NoteEntry.COLUMN_TIME, selectedTime);

        int updatedRows = database.update(NoteContract.NoteEntry.TABLE_NAME, values,
                NoteContract.NoteEntry._ID + " = ?", new String[]{String.valueOf(noteId)});

        if (updatedRows > 0) {
            Toast.makeText(requireContext(), "Note updated", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        } else {
            Toast.makeText(requireContext(), "Failed to update note", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytesFromImageView(ImageButton imageButton) {
        Bitmap bitmap = ((BitmapDrawable) imageButton.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}