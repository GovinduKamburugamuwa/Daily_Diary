package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewScreenFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private NoteDbHelper dbHelper;

    public void setDbHelper(NoteDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_views, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MyAdapter(requireContext(), dbHelper);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    public void loadNotes() {
        if (dbHelper != null) {
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor cursor = database.query(
                    NoteContract.NoteEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    NoteContract.NoteEntry.COLUMN_CREATED_TIME + " DESC"
            );
            adapter.setCursor(cursor);
        }
    }
}