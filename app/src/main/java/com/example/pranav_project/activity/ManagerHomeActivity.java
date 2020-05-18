package com.example.pranav_project.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.example.pranav_project.R;
import com.example.pranav_project.pojo.MyPojo;
import com.example.pranav_project.utils.*;
import com.firebase.ui.firestore.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ManagerHomeActivity extends AppCompatActivity {

    @BindView(R.id.id_add_floating_btn_manager)FloatingActionButton fButton;
    @BindView(R.id.id_manager_home_toolbar) Toolbar mToolbar;
    @BindView(R.id.id_btn_nav_view) BottomNavigationView bottonMenu;

    FirebaseAuth mAuth;


    MySharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mAuth = FirebaseAuth.getInstance();
        mySharedPreferences=MySharedPreferences.getInstance(this);


        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ManagerHomeActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
        });


        bottonMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case  R.id.id_home :
                        Utils.toast(ManagerHomeActivity.this ,"home");
                        break;
                    case R.id.id_orders:
                        Utils.toast(ManagerHomeActivity.this ,"orders");
                        break;
                    case R.id.id_setting:
                        Intent  intent = new Intent(ManagerHomeActivity.this , AdminProfileActivity.class);
                        startActivity(intent);
                        break;

                }
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manager_home_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_manager_logout:
                System.out.println("hi....");
                       mAuth.signOut();
                       mySharedPreferences.setUserData(MyConstants.MANAGER , "0");
                       startActivity(Utils.sendUserToLoginActivity(getApplicationContext(),LoginActivity.class));  finish();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Exit");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ManagerHomeActivity.super.onBackPressed();
            }
        })
                .setNegativeButton("Cancel",null)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();

    }
}
