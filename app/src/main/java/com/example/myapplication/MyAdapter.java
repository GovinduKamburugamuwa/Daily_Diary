package com.example.myapplication;

import android.content.Context;
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
    private Context context;
    private NoteDbHelper dbHelper;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onEditClick(long noteId, String title, String description, byte[] imageData, long date, long time);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public MyAdapter(Context context, NoteDbHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    public void setCursor(Cursor cursor) {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            long noteId = cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_DESCRIPTION));
            long createdTime = cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_CREATED_TIME));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_DATE));
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_TIME));
            byte[] imageData = cursor.getBlob(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_IMAGE_DATA));

            holder.titleOutput.setText(title);
            holder.descriptionOutput.setText(description);

            String formattedCreatedTime = DateFormat.getDateTimeInstance().format(new Date(createdTime));
            holder.timeOutput.setText(formattedCreatedTime);

            String formattedDate = DateFormat.getDateInstance().format(new Date(date));
            String formattedTime = DateFormat.getTimeInstance().format(new Date(time));
            holder.dateOutput.setText(formattedDate + " " + formattedTime);

            if (imageData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                holder.imageView.setImageBitmap(bitmap);
            } else {
                holder.imageView.setImageResource(R.drawable.default_image);
            }

            holder.deleteButton.setOnClickListener(v -> {
                dbHelper.deleteNote(noteId);
                refreshData();
            });

            holder.editButton.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onEditClick(noteId, title, description, imageData, date, time);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void refreshData() {
        Cursor newCursor = dbHelper.getAllNotes();
        setCursor(newCursor);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleOutput;
        TextView descriptionOutput;
        TextView timeOutput;
        TextView dateOutput;
        ImageView deleteButton;
        ImageView editButton;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleOutput = itemView.findViewById(R.id.titleoutput);
            descriptionOutput = itemView.findViewById(R.id.descriptionoutput);
            timeOutput = itemView.findViewById(R.id.timeoutput);
            dateOutput = itemView.findViewById(R.id.dateoutput);
            deleteButton = itemView.findViewById(R.id.delbtn);
            editButton = itemView.findViewById(R.id.pencil);
            imageView = itemView.findViewById(R.id.noteImageView);
        }
    }
}