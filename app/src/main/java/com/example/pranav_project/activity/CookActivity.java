package com.example.pranav_project.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.example.pranav_project.R;
import com.example.pranav_project.utils.*;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.EventListener;

import java.util.*;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CookActivity extends AppCompatActivity {

    @BindView(R.id.id_cook_drawer)DrawerLayout drawerLayout;
    @BindView(R.id.id_cook_nav) NavigationView navigationView;
    @BindView(R.id.id_cook_toolbar)Toolbar mToolbar;
    @BindView(R.id.id_sample) Button sampleBtn;
    MySharedPreferences preferences;
    FirebaseFirestore db;
    CollectionReference total_orderRef;

    private FirebaseAuth mAuh;
    private ArrayList<String> mArrayList = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook);
        ButterKnife.bind(this);

        initialization();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.cook_logout:
                        mAuh.signOut();
                        drawerLayout.closeDrawers();
                        preferences.setUserData(MyConstants.COOK,"0");
                        sendUserToLoginActivity();

                }
                return true;
            }
        });

        sampleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printData();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        addAllDocuments_to_list();
    }

    private void addAllDocuments_to_list() {
        total_orderRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e)
                    {

                        if(snapshots.isEmpty())
                            System.out.println("snapshot are empty");
                        else
                            for(DocumentChange doc : snapshots.getDocumentChanges())
                                if(doc.getType()==DocumentChange.Type.ADDED)
                                    mArrayList.add(doc.getDocument().getId());     // adding all the documents to the list
                    }
                });
    }

    private void initialization() {
        setSupportActionBar(mToolbar);
        preferences = MySharedPreferences.getInstance(this);
        mAuh = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        total_orderRef = db.collection(MyConstants.TOTAL_ORDER_REF);
    }

    private void printData() {

        System.out.println("mArrayList contains "+ mArrayList);
          DocumentReference dRef;
          for( int i = 0 ; i<mArrayList.size() ; i++)
          {
                dRef = total_orderRef.document(mArrayList.get(i));
                dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        String name = snapshot.get(MyConstants.NAME).toString();
                        String date = snapshot.get(MyConstants.DATE).toString();
                        String time = snapshot.get(MyConstants.TIME).toString();
                        Map<String , Object> map = (Map<String, Object>) snapshot.get(MyConstants.ITEMS);

                        System.out.println(".......................................");
                        System.out.println(name);
                        System.out.println(date);
                        System.out.println(time);
                        System.out.println(map);

                    }
                });
          }
        System.out.println();
    }


    private void sendUserToLoginActivity() {
        Intent intent = new Intent(CookActivity.this , LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weater_home_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.id_three_line_nav :
                drawerLayout.openDrawer(GravityCompat.END);
                break;
        }
        return true;
    }


}
