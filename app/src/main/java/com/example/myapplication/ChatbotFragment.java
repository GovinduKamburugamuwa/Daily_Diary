package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatbotFragment extends Fragment {

    private RecyclerView chatRecyclerView;
    private TextInputEditText messageInput;
    private ImageButton sendButton;
    private MaterialButton saveToNoteButton;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private GenerativeModelFutures model;
    private ExecutorService executorService;
    private ChatToNoteInterface chatToNoteInterface;

    private static final String API_KEY = "AIzaSyCgiLwTGCQAnLvvpioaS3T4sMeuBXeVYZM"; // Replace with your actual API key

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ChatToNoteInterface) {
            chatToNoteInterface = (ChatToNoteInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ChatToNoteInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);
        saveToNoteButton = view.findViewById(R.id.saveToNoteButton);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(v -> sendMessage());
        saveToNoteButton.setOnClickListener(v -> saveToNote());

        try {
            GenerativeModel gm = new GenerativeModel("gemini-pro", API_KEY);
            model = GenerativeModelFutures.from(gm);
            executorService = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle initialization error
        }
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            chatMessages.add(new ChatMessage(message, true));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            messageInput.setText("");

            // Call Gemini API
            executorService.execute(() -> {
                try {
                    Content content = new Content.Builder()
                            .addText(message)
                            .build();
                    GenerateContentResponse response = model.generateContent(content).get();
                    String botResponse = response.getText();
                    requireActivity().runOnUiThread(() -> sendBotResponse(botResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> sendBotResponse("Sorry, I couldn't process that request."));
                }
            });
        }
    }

    private void sendBotResponse(String response) {
        chatMessages.add(new ChatMessage(response, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
    }

    private void saveToNote() {
        StringBuilder chatContent = new StringBuilder();
        for (ChatMessage message : chatMessages) {
            chatContent.append(message.isUser ? "User: " : "Bot: ")
                    .append(message.message)
                    .append("\n");
        }
        chatToNoteInterface.passChatToNote(chatContent.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    private static class ChatMessage {
        String message;
        boolean isUser;

        ChatMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
        }
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<ChatMessage> messages;

        ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage message = messages.get(position);
            holder.bind(message);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView messageText;

            ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                messageText = itemView.findViewById(R.id.messageText);
            }

            void bind(ChatMessage message) {
                messageText.setText(message.message);
                if (message.isUser) {
                    messageText.setBackgroundResource(R.drawable.user_message_bubble);
                    ((LinearLayout.LayoutParams) messageText.getLayoutParams()).gravity = Gravity.END;
                } else {
                    messageText.setBackgroundResource(R.drawable.bot_message_bubble);
                    ((LinearLayout.LayoutParams) messageText.getLayoutParams()).gravity = Gravity.START;
                }
            }
        }
    }
}