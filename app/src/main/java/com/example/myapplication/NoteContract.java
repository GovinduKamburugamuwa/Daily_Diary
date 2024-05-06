package com.example.myapplication;

import android.provider.BaseColumns;

public class NoteContract {
    private NoteContract() {}

    public static final class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "work";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CREATED_TIME = "created_time";
        public static final String COLUMN_IMAGE_DATA = "image_data";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
    }
}