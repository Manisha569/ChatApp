package com.example.chatapp;

import static com.example.chatapp.MainActivity.LOGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
private RecyclerView recyclerView;
private ArrayList<User> users;
private ProgressBar progressBar;
private UsersAdapter usersAdapter;
UsersAdapter.OnUserClickListener onUserClickListener;

 private SwipeRefreshLayout swipeRefreshLayout;
 String myImageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        progressBar = findViewById(R.id.progressBar);
        users = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        onUserClickListener = new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClicked(int position) {
                startActivity(new Intent(FriendsActivity.this,MessageActivity.class)
                        .putExtra("username_of_roommate",users.get(position).getUsername())
                        .putExtra("email_of_roommate",users.get(position).getEmail())
                        .putExtra("img_of_roommate",users.get(position).getProfilePicture())
                        .putExtra("my_img",myImageUrl)
                );


            }
        };

        getUsers();

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_item_profile) {
            startActivity(new Intent(FriendsActivity.this, Profile.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUsers(){
        Log.e(LOGE,"get users");
        users.clear();
        FirebaseDatabase.getInstance().getReference("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(LOGE,"onDataChange");
                Log.e(LOGE," DataSnapshot loop start");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Log.e(LOGE," DataSnapshot loop");
                    users.add(dataSnapshot.getValue(User.class));
                }
                Log.e(LOGE," DataSnapshot loop end");
                Log.e(LOGE," setting usersAdapter");

                usersAdapter = new UsersAdapter(users,FriendsActivity.this,onUserClickListener, users);
                Log.e(LOGE,"new UsersAdapter Constructor call");
                Log.e(LOGE,"setLayoutManager");

                recyclerView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
                recyclerView.setAdapter(usersAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                for (User user: users){
                    if(user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                        myImageUrl = user.getProfilePicture();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(LOGE," users onCancelled");
            }
        });

        Log.e(LOGE,"get users finished");

    }

}