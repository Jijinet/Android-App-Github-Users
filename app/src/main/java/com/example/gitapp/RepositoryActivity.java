package com.example.gitapp;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gitapp.model.GitRepo;
import com.example.gitapp.service.GitRepoServiceAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RepositoryActivity extends AppCompatActivity {
    List<String> data=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repository_layout);
        Intent intent=getIntent();
        String login=intent.getStringExtra(HomeActivity.USER_LOGIN_PARAM);
        setTitle("Repositories");
        TextView textViewLogin=findViewById(R.id.text_view_login);
        ListView listViewRepositories=findViewById(R.id.list_view_repo);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,R.layout.custom_list_content,R.id.list_content,data);
        listViewRepositories.setAdapter(arrayAdapter);
        textViewLogin.setText(login);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_200)));

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GitRepoServiceAPI gitRepoServiceAPI=retrofit.create(GitRepoServiceAPI.class);
        Call<List<GitRepo>> reposCall=gitRepoServiceAPI.userRepositories(login);
        reposCall.enqueue(new Callback<List<GitRepo>>() {
            @Override
            public void onResponse(Call<List<GitRepo>> call, Response<List<GitRepo>> response) {
                if(!response.isSuccessful()){
                    Log.e("error",String.valueOf(response.code()));
                    return;
                }
                List<GitRepo> gitRepos=response.body();
                for(GitRepo gitRepo:gitRepos){
                    String content="";
                    content+=gitRepo.id+"\n";
                    content+=gitRepo.name+"\n";
                    content+=gitRepo.language+"\n";
                    content+=gitRepo.size+"\n";
                    data.add(content);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<GitRepo>> call, Throwable t) {

            }
        });
    }
}