package com.example.myapplication;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Cursor cursor;
    private ViewScreen activity;

    public MyAdapter(ViewScreen activity) {
        this.activity = activity;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteDbHelper dbHelper = new NoteDbHelper(parent.getContext());
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false), dbHelper, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_DESCRIPTION));
            long createdTime = cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_CREATED_TIME));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_DATE));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_TIME));
            byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_IMAGE_DATA));

            holder.titleOutput.setText(title);
            holder.descriptionOutput.setText(description);

            // Format the created time
            String formattedCreatedTime = DateFormat.getDateTimeInstance().format(new Date(createdTime));
            holder.timeOutput.setText(formattedCreatedTime);

            // Format the date and time
            String formattedDate = DateFormat.getDateInstance().format(new Date(date));
            String formattedTime = DateFormat.getTimeInstance().format(new Date(time));
            holder.dateOutput.setText(formattedDate);
            holder.timeOutput.setText(formattedTime);

            if (imageData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                holder.imageView.setImageBitmap(bitmap);
            } else {
                holder.imageView.setImageResource(R.drawable.default_image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleOutput;
        TextView descriptionOutput;
        TextView timeOutput;
        TextView dateOutput;
        ImageView deleteButton;
        ImageView imageView;
        NoteDbHelper dbHelper;
        ViewScreen activity;

        public MyViewHolder(@NonNull View itemView, NoteDbHelper dbHelper, ViewScreen activity) {
            super(itemView);
            this.dbHelper = dbHelper;
            this.activity = activity;
            titleOutput = itemView.findViewById(R.id.titleoutput);
            descriptionOutput = itemView.findViewById(R.id.descriptionoutput);
            timeOutput = itemView.findViewById(R.id.timeoutput);
            dateOutput = itemView.findViewById(R.id.dateoutput);
            deleteButton = itemView.findViewById(R.id.delbtn);
            imageView = itemView.findViewById(R.id.noteImageView);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    cursor.moveToPosition(position);
                    long noteId = cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry._ID));
                    dbHelper.deleteNote(noteId);
                    activity.loadNotes();
                }
            });
        }
    }
}