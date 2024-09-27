package com.example.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoteDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "works.db";
    private static final int DATABASE_VERSION = 1;

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NOTE_TABLE = "CREATE TABLE " + NoteContract.NoteEntry.TABLE_NAME + " (" +
                NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteContract.NoteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                NoteContract.NoteEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                NoteContract.NoteEntry.COLUMN_CREATED_TIME + " INTEGER NOT NULL, " +
                NoteContract.NoteEntry.COLUMN_IMAGE_DATA + " BLOB, " +
                NoteContract.NoteEntry.COLUMN_DATE + " INTEGER, " +
                NoteContract.NoteEntry.COLUMN_TIME + " INTEGER" +
                ");";
        db.execSQL(SQL_CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NoteEntry.TABLE_NAME);
        onCreate(db);
    }

    public void deleteNote(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NoteContract.NoteEntry.TABLE_NAME, NoteContract.NoteEntry._ID + " = ?", new String[]{String.valueOf(noteId)});
    }

    public Cursor getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                NoteContract.NoteEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                NoteContract.NoteEntry.COLUMN_CREATED_TIME + " DESC"
        );
    }

    public void printTableStructure() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "PRAGMA table_info(" + NoteContract.NoteEntry.TABLE_NAME + ")";
        Cursor cursor = db.rawQuery(query, null);

        StringBuilder builder = new StringBuilder();
        builder.append("Table Structure for ").append(NoteContract.NoteEntry.TABLE_NAME).append(":\n");

        if (cursor.moveToFirst()) {
            do {
                int columnIndex = cursor.getColumnIndex("name");
                String columnName = cursor.getString(columnIndex);
                builder.append(columnName).append(", ");
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.d("NoteDbHelper", builder.toString());
    }
}