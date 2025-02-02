package com.example.chatbot;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView chatsRV;
    private EditText UserMsg;
    private FloatingActionButton sendMsgFAB;
    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private ArrayList<ChatsModel> chatsModelArrayList;
    private ChatRVAdapter chatRVAdapter;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chatsRV = findViewById(R.id.idRVChats);
        UserMsg = findViewById(R.id.idEdtMessage);
        sendMsgFAB = findViewById(R.id.idFABSend);
        chatsModelArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModelArrayList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(chatRVAdapter);

        sendMsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserMsg.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                    return;

                }
                getResponse(UserMsg.getText().toString());
                UserMsg.setText("");
            }
        });
    }

        private void getResponse (String message){

            chatsModelArrayList.add(new ChatsModel(message, USER_KEY));
            chatRVAdapter.notifyDataSetChanged();
            String url = "http://api.brainshop.ai/get?bid=182265&key=TotVXQLKkBgcs8qt&uid=[uid]&msg=" + message;
            String BASE_URL = "http://api.brainshop.ai/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
            Call<MsgModel> call = retrofitAPI.getMessage(url);
            call.enqueue(new Callback<MsgModel>() {
                @Override
                public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                    if (response.isSuccessful()) {
                        MsgModel model = response.body();
                        chatsModelArrayList.add(new ChatsModel(model.getCnt(), BOT_KEY));
                        chatRVAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<MsgModel> call, Throwable t) {

                    chatsModelArrayList.add(new ChatsModel("Please revert your question", BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
            });
        }

    }
