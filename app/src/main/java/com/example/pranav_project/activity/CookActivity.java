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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.example.pranav_project.R;
import com.example.pranav_project.pojo.Pojo_Order_Fetching;
import com.example.pranav_project.utils.*;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
    @BindView(R.id.id_cook_recycler_layout)RecyclerView mRecyclerView;
    MySharedPreferences preferences;
    FirebaseFirestore db;
    CollectionReference total_orderRef;
    FirestoreRecyclerOptions options , optionsDate_filter;


    private FirebaseAuth mAuh;
    private ArrayList<String> mArrayList = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook);
        ButterKnife.bind(this);

        initialization();

        options = new FirestoreRecyclerOptions.Builder<Pojo_Order_Fetching>()
                 .setQuery(total_orderRef.orderBy(MyConstants.TIMESTAMP,Query.Direction.DESCENDING),Pojo_Order_Fetching.class)
                .build();

        optionsDate_filter = new FirestoreRecyclerOptions.Builder<Pojo_Order_Fetching>()
                .setQuery(total_orderRef.whereEqualTo(MyConstants.DATE,Utils.getDate()),Pojo_Order_Fetching.class )
                .build();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {

                    case R.id.id_all_orders_cook:
//                        mArrayList.clear();
//                        addAllDocuments_to_list();
                        printAllOrderToTheScreen(options);
                        drawerLayout.closeDrawers();                   break;

                    case  R.id.todays_order_id_cook:
                        printTodaysOrderToTheScreen(optionsDate_filter);
                        mArrayList.clear();
                      //  addAllDocuments_to_list();
                        drawerLayout.closeDrawers();                    break;

                    case R.id.cook_logout:
                        mAuh.signOut();
                        drawerLayout.closeDrawers();
                        preferences.setUserData(MyConstants.COOK,"0");
                        startActivity(Utils.sendUserToLoginActivity(getApplicationContext() , LoginActivity.class));
                        finish();          break;

                    case R.id.id_cook_home:
                        startActivity(Utils.sendUserToCookActvity(getApplicationContext(),CookActivity.class));
                        finish();
                        break;
                }
                return true;
            }
        });

    }


    private void printTodaysOrderToTheScreen(FirestoreRecyclerOptions optionsDate_filter) {
        FirestoreRecyclerAdapter<Pojo_Order_Fetching ,MyViewHolder> adapter = new
                FirestoreRecyclerAdapter<Pojo_Order_Fetching, MyViewHolder>(optionsDate_filter) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Pojo_Order_Fetching model) {
                        String date = model.getDate();
                        System.out.println("data is "+ date);
                        if(date.equals(Utils.getDate()))
                                printDataToTheScreenUsing_DB_Objects(holder , model);

                    }

                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view =LayoutInflater.from(getApplicationContext())
                                .inflate(R.layout.food_order_fetching_layout,parent,false);
                        return new MyViewHolder(view);

                    }
                };
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }

    private void printAllOrderToTheScreen(FirestoreRecyclerOptions options) {

        FirestoreRecyclerAdapter<Pojo_Order_Fetching,MyViewHolder> adapter = new
                FirestoreRecyclerAdapter<Pojo_Order_Fetching, MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Pojo_Order_Fetching model) {

                        printDataToTheScreenUsing_DB_Objects(holder , model);
                    }

                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getApplicationContext())
                                .inflate(R.layout.food_order_fetching_layout,parent , false);
                        return new MyViewHolder(view);
                    }
                };
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }

    public static void printDataToTheScreenUsing_DB_Objects(MyViewHolder holder, Pojo_Order_Fetching model) {
        holder.nameTV.setText(model.getName());
        holder.dateTV.setText(model.getDate());
        holder.timeTV.setText(model.getTime());

        Map<String , Object> map = model.getItems();

        for(Map.Entry<String ,Object> entry : map.entrySet() )
            holder.listView.append( entry.getKey()+"  "+entry.getValue().toString()+"\n");
        //map.clear();
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        addAllDocuments_to_list();
//    }

//    private void addAllDocuments_to_list() {
//        total_orderRef
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e)
//                    {
//
//                        if(snapshots.isEmpty())
//                            System.out.println("snapshot are empty");
//                        else
//                            for(DocumentChange doc : snapshots.getDocumentChanges())
//                                if(doc.getType()==DocumentChange.Type.ADDED)
//                                    mArrayList.add(doc.getDocument().getId());     // adding all the documents to the list
//                    }
//                });
//    }

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

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
       @BindView(R.id.id_x_date)TextView dateTV;
       @BindView(R.id.id_x_time)TextView timeTV;
       @BindView(R.id.id_x_Name)TextView nameTV;
       @BindView(R.id.id_food_x_lv)TextView listView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Exit");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CookActivity.super.onBackPressed();
            }
        })
                .setNegativeButton("Cancel",null)
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();

    }
}
