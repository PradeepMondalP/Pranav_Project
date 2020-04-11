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
    private CollectionReference Waiterreference , foodRef , total_order_ref;
    private DocumentReference dRef;
    private FirestoreRecyclerOptions options , options_my_todays_order , options_my_total_orders;
    private FirebaseFirestore db;
    private String currentUserID ;

    private CircleImageView navProfile;
    private TextView navProfileName;
    private MySharedPreferences preferences;
    int foodItemCount=0;
    static int MY_TOTAL_FOOD_SUM=0;

    private CollectionReference orderRef;

    static Map<String , String> foodMap = new LinkedHashMap<>();


    @BindView(R.id.id_weater_recycler_view)RecyclerView mRecyclerView;
    @BindView(R.id.id_food_ordering_floating_btn) FloatingActionButton foodOrderingFBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weater_home);
        ButterKnife.bind(this);

        initialization();

        fetchUserData();      //fetch user profile , user name etc...

        options = new FirestoreRecyclerOptions.Builder<MyPojo>()
                .setQuery(foodRef.orderBy(MyConstants.TIMESTAMP , Query.Direction.DESCENDING) , MyPojo.class)
                .build();

          // todays order..
        options_my_todays_order = new FirestoreRecyclerOptions.Builder<Pojo_Order_Fetching>()
                .setQuery(total_order_ref.whereEqualTo(MyConstants.DATE ,Utils.getDate())
                                         .whereEqualTo(MyConstants.NAME , preferences.getUserData(MyConstants.CURRENT_USER_NAME))
                           //              .whereEqualTo(MyConstants.TIMESTAMP ,Query.Direction.DESCENDING)
                ,Pojo_Order_Fetching.class)
                .build();

        // total orders......
        options_my_total_orders = new FirestoreRecyclerOptions.Builder<Pojo_Order_Fetching>()
                .setQuery(total_order_ref.whereEqualTo(MyConstants.NAME ,preferences.getUserData(MyConstants.CURRENT_USER_NAME)  )
                                         //.whereEqualTo(MyConstants.TIMESTAMP, Query.Direction.DESCENDING)
                                        // .whereEqualTo(MyConstants.TIMESTAMP, Query.Direction.DESCENDING)
                , Pojo_Order_Fetching.class )
                .build();


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
                          showMy_Todays_Order();
                         drawerLayout.closeDrawers();
                         break;

                    case R.id.weater_total_order:
                        showMy_Total_Order();
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });

        foodOrderingFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MY_TOTAL_FOOD_SUM==0)
                {
                    Utils.toast(WeaterHomeActivity.this ,"you must select some items");
                    return;
                }
                 call_food_order_confirming_layout();
                 Utils.toast(WeaterHomeActivity.this ,MY_TOTAL_FOOD_SUM+"");

            }
        });
    }

    private void showMy_Total_Order() {

        FirestoreRecyclerAdapter<Pojo_Order_Fetching , CookActivity.MyViewHolder> adapter =
                new FirestoreRecyclerAdapter<Pojo_Order_Fetching, CookActivity.MyViewHolder>(options_my_total_orders) {
                    @Override
                    protected void onBindViewHolder(@NonNull CookActivity.MyViewHolder holder, int position, @NonNull Pojo_Order_Fetching model) {

                        CookActivity.printDataToTheScreenUsing_DB_Objects(holder ,model);

                    }

                    @NonNull
                    @Override
                    public CookActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getApplicationContext())
                                .inflate(R.layout.food_order_fetching_layout, parent , false);
                        return new CookActivity.MyViewHolder(view);
                    }
                };
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }

    private void showMy_Todays_Order() {

        FirestoreRecyclerAdapter<Pojo_Order_Fetching,CookActivity.MyViewHolder>adapter =
                new FirestoreRecyclerAdapter<Pojo_Order_Fetching, CookActivity.MyViewHolder>(options_my_todays_order) {
                    @Override
                    protected void onBindViewHolder(@NonNull CookActivity.MyViewHolder holder, int position, @NonNull Pojo_Order_Fetching model) {

                        CookActivity.printDataToTheScreenUsing_DB_Objects(holder ,model);
                    }

                    @NonNull
                    @Override
                    public CookActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getApplicationContext())
                                .inflate(R.layout.food_order_fetching_layout, parent , false);
                        return new CookActivity.MyViewHolder(view);
                    }
                };
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }


    private void call_food_order_confirming_layout() {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle(" Confirm ur Orders");

       builder.setPositiveButton("ORDER", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
                 saveFoodOrder();
           }
       })
               .setNegativeButton("Cancell" ,null)

               .setCancelable(true);

        builder.show();
    }

    private void saveFoodOrder() {

        final AlertDialog mDialog = Utils.getAlertDialog(WeaterHomeActivity.this ,"saving ...");
        mDialog.show();

        Map<String , Object> foodMapFinal = new LinkedHashMap<>();
        foodMapFinal.put(MyConstants.USER_ID , preferences.getUserData(MyConstants.CURRENT_USER_ID));
        foodMapFinal.put(MyConstants.NAME , preferences.getUserData(MyConstants.CURRENT_USER_NAME));
        foodMapFinal.put(MyConstants.DATE , Utils.getDate());
        foodMapFinal.put(MyConstants.TIME , Utils.getTime());
        foodMapFinal.put(MyConstants.TIMESTAMP , Utils.getTimeStamp());
        foodMapFinal.put(MyConstants.ITEMS , foodMap);

        db.collection(MyConstants.TOTAL_ORDER_REF)
                .document(preferences.getUserData(MyConstants.CURRENT_USER_ID) +Utils.getDateAndTime())
                .set(foodMapFinal)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mDialog.dismiss();
                        if(task.isSuccessful())
                        {
                            MY_TOTAL_FOOD_SUM=0;
                            foodMap.clear();
                            Intent intent = new Intent(WeaterHomeActivity.this , WeaterHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveDataFromDatabase();
        MY_TOTAL_FOOD_SUM=0;
    }

    private void fetchUserData() {
        dRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                if(snapshot.exists()){
                    String name = snapshot.getString(MyConstants.NAME.toString());
                    String image_url = snapshot.getString(MyConstants.PROFILE_URL);
                    preferences.setUserData(MyConstants.CURRENT_USER_NAME, name);
                    navProfileName.setText(name);
                    Picasso.with(getApplicationContext()).load(image_url)
                            .into(navProfile);
                }
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
        Waiterreference = FirebaseFirestore.getInstance().collection(MyConstants.WEATER);
        currentUserID = mAuth.getCurrentUser().getUid();
        dRef = Waiterreference.document(currentUserID);
        orderRef = FirebaseFirestore.getInstance().collection(MyConstants.ORDERS);
        foodRef = FirebaseFirestore.getInstance().collection(MyConstants.FOOD_REF);
        db = FirebaseFirestore.getInstance();
        total_order_ref = FirebaseFirestore.getInstance().collection(MyConstants.TOTAL_ORDER_REF);

        preferences = MySharedPreferences.getInstance(this);
        preferences.setUserData(MyConstants.CURRENT_USER_ID , currentUserID);

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

    private void retrieveDataFromDatabase() {

        FirestoreRecyclerAdapter<MyPojo , MyViewHolder>adapter =
                new FirestoreRecyclerAdapter<MyPojo, MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final MyViewHolder holder, int position, @NonNull MyPojo model)
                    {
                        holder.mFoodName.setText(Utils.getDecryptedMessage(model.getFood()));
                        holder.mItem_cost.setText(Utils.getDecryptedMessage(model.getItem_cost()));

                        holder.addItem.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view) {

                                foodItemCount = Integer.parseInt(holder.itemCountShowTV.getText().toString());
                                holder.itemCountShowTV.setText(++foodItemCount +"");

                                float totalSum = Integer.parseInt(holder.mItem_cost.getText().toString().trim()) * foodItemCount;
                                holder.itemTotalCost.setText(totalSum+"");

                                    MY_TOTAL_FOOD_SUM+= Integer.parseInt(holder.mItem_cost.getText().toString().trim());
                                    final  String foodName = holder.mFoodName.getText().toString();

                                foodMap.put(foodName,foodItemCount+"");

                            }
                        });

                        holder.minusItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                foodItemCount = Integer.parseInt(holder.itemCountShowTV.getText().toString());
                                if(foodItemCount>0)
                                {
                                    foodItemCount = Integer.parseInt(holder.itemCountShowTV.getText().toString());
                                    holder.itemCountShowTV.setText(--foodItemCount +"");

                                    int totalSum = Integer.parseInt(holder.mItem_cost.getText().toString().trim()) * foodItemCount;
                                    holder.itemTotalCost.setText(totalSum+"");

                                    MY_TOTAL_FOOD_SUM-= Integer.parseInt(holder.mItem_cost.getText().toString().trim());

                                    final  String foodName = holder.mFoodName.getText().toString();
                                     foodMap.put(foodName,foodItemCount+"");
                                }
                                if(foodItemCount==0)
                                {
                                    holder.itemCountShowTV.setText("0");
                                    holder.itemTotalCost.setText("0");
                                    MY_TOTAL_FOOD_SUM+= 0;
                                }
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.waiter_food_order_taking_layout,
                                parent , false);
                        return new MyViewHolder(view);
                    }
                };
        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder
    {

        @BindView(R.id.yy_id_food_name) TextView mFoodName;
        @BindView(R.id.yy_food_item_price) TextView mItem_cost;
        @BindView(R.id.ff_id_item_remove) ImageButton minusItem;
        @BindView(R.id.ff_id_item_add) ImageButton addItem;
        @BindView(R.id.ff_id_item_count_)TextView itemCountShowTV;
        @BindView(R.id.ff_food_item_price_total_cost)TextView itemTotalCost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }
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
