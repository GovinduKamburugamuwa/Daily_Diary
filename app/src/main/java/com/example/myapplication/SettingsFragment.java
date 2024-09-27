package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private EditText editTextText;
    private Button subbtn;
    private OnSettingsSubmitListener listener;

    public interface OnSettingsSubmitListener {
        void onSettingsSubmit(String userName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsSubmitListener) {
            listener = (OnSettingsSubmitListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSettingsSubmitListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        editTextText = view.findViewById(R.id.editTextText);
        subbtn = view.findViewById(R.id.subbtn);

        subbtn.setOnClickListener(v -> {
            String userName = editTextText.getText().toString();
            if (!userName.isEmpty() && listener != null) {
                listener.onSettingsSubmit(userName);  // Submit the username to MainActivity
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
