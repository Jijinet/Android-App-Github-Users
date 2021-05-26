package com.example.gitapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.gitapp.model.GitUser;
import com.example.gitapp.model.GitUsersResponse;
import com.example.gitapp.model.UsersListViewModel;
import com.example.gitapp.service.GitRepoServiceAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    List<GitUser> data=new ArrayList<>();
    public static final String USER_LOGIN_PARAM="user.login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final EditText editTextQuery=findViewById(R.id.input_search);
        Button buttonSearch=findViewById(R.id.search_btn);
        ListView listViewUsers=findViewById(R.id.list_view_users);
        UsersListViewModel listViewModel=new UsersListViewModel(this,R.layout.users_list_view_layout,data);
        listViewUsers.setAdapter(listViewModel);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_200)));

        final Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        buttonSearch.setOnClickListener(v -> {
            String query=editTextQuery.getText().toString();
            Log.i("",query);
            final GitRepoServiceAPI gitRepoServiceAPI=retrofit.create(GitRepoServiceAPI.class);
            Call<GitUsersResponse> callGitUsers=gitRepoServiceAPI.searchUsers(query);
            callGitUsers.enqueue(new Callback<GitUsersResponse>() {
                @Override
                public void onResponse(Call<GitUsersResponse> call, Response<GitUsersResponse> response) {
                    Log.i("info",call.request().url().toString());
                    if(!response.isSuccessful()){
                        Log.i("info",String.valueOf(response.code()));
                        return;
                    }
                    GitUsersResponse gitUsersResponse=response.body();
                    data.clear();
                    for (GitUser user:gitUsersResponse.users){
                        data.add(user);
                    }
                    listViewModel.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<GitUsersResponse> call, Throwable t) {
                    Log.e("error","Error");
                }
            });
        });

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String login=data.get(position).login;
                Log.i("info",login);
                Intent intent=new Intent(getApplicationContext(),RepositoryActivity.class);
                intent.putExtra(USER_LOGIN_PARAM,login);
                startActivity(intent);
            }
        });
    }
}