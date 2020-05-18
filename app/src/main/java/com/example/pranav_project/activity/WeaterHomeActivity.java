package com.example.pranav_project.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.example.pranav_project.R;
import com.example.pranav_project.pojo.MyPojo;
import com.example.pranav_project.pojo.Pojo_Order_Fetching;
import com.example.pranav_project.utils.*;
import com.firebase.ui.firestore.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;
import com.squareup.picasso.Picasso;

import java.util.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class WeaterHomeActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;
   private String currentUserID ;

    private CircleImageView navProfile;
    private TextView navProfileName;
    private MySharedPreferences preferences;

    private CollectionReference orderRef;


    @BindView(R.id.id_weater_recycler_view)RecyclerView mRecyclerView;
    @BindView(R.id.id_food_ordering_floating_btn) FloatingActionButton foodOrderingFBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weater_home);
        ButterKnife.bind(this);

        initialization();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.id_whm_home:
                        startActivity(Utils.sendUseroWeaterHomeActcivity(getApplicationContext(),WeaterHomeActivity.class)); finish();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.id_weater_logout:
                        preferences.setUserData(MyConstants.WEATER,"0");
                        mAuth.signOut();
                        startActivity(Utils.sendUserToLoginActivity(getApplicationContext(),LoginActivity.class));
                        finish();
                        break;

                    case R.id.weater_todays_order:
                        //  showMy_Todays_Order();
                         drawerLayout.closeDrawers();
                         break;

                    case R.id.weater_total_order:
                     //   showMy_Total_Order();
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });

        foodOrderingFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("hi..");

            }
        });
    }




    private void initialization() {
        navigationView = (NavigationView)findViewById(R.id.id_weater_home_nav_view);
        View navHeaderView = navigationView.inflateHeaderView(R.layout.weaer_header_layout);
        navProfile = (CircleImageView)navHeaderView.findViewById(R.id.id_weater_header_layout_photo);
        navProfileName = (TextView)navHeaderView.findViewById(R.id.id_weater_header_layout_name);

        drawerLayout = (DrawerLayout)findViewById(R.id.id_weater_home_drawer_layout);
        mToolbar = (Toolbar)findViewById(R.id.id_weater_toolbar);
        setSupportActionBar(mToolbar);

        mAuth = FirebaseAuth.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();

        orderRef = FirebaseFirestore.getInstance().collection(MyConstants.ORDERS);

        preferences = MySharedPreferences.getInstance(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weater_home_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.id_three_line_nav:
                drawerLayout.openDrawer(GravityCompat.END);
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
                WeaterHomeActivity.super.onBackPressed();
            }
        })
                .setNegativeButton("Cancel",null)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();

    }


}
