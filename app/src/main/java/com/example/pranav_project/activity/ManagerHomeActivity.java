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

public class ManagerHomeActivity extends AppCompatActivity
 implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.id_add_floating_btn_manager)FloatingActionButton fButton;
    @BindView(R.id.id_manager_home_toolbar) Toolbar mToolbar;
    @BindView(R.id.id_btn_nav_view) BottomNavigationView bottonMenu;
    @BindView(R.id.id_manager_recycler_view)RecyclerView mRecyclerView;

    private CollectionReference cRef;
    private FirestoreRecyclerOptions options;
    private FirebaseAuth mAuth;

    ImageButton  addItemBtn , removeItemBtn;
    TextView itemCountTV , itemPrice , foodName , totalCostOfItem;
    Spinner foodNamesSpinner;
    CircleImageView itemImage;
    int tempPos=0 ;

    public static int ITEMCOUNT=0;
    Items itemsClassObj = new Items();
    MySharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        cRef = FirebaseFirestore.getInstance().collection("food");
        mAuth = FirebaseAuth.getInstance();
        mySharedPreferences = MySharedPreferences.getInstance(this);

        options = new FirestoreRecyclerOptions.Builder<MyPojo>()
                .setQuery(cRef.orderBy(MyConstants.TIMESTAMP,Query.Direction.DESCENDING) , MyPojo.class)
                .build();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  openItemAddingDialog(view);
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

    private void openItemAddingDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ManagerHomeActivity.this);
        builder.setTitle("Add Items");

        final View view1 = LayoutInflater.from(getApplicationContext()).
                inflate(R.layout.manager_food_adding,null , false);

        initialize_food_ordering_widgets(view1);

        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ITEMCOUNT++;
                itemCountTV.setText(ITEMCOUNT+"");
                totalCostOfItem.setText(itemsClassObj.getPriceOfItem(ITEMCOUNT , itemsClassObj.foodItems[tempPos]) +"");
            }
        });

        removeItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ITEMCOUNT>0){
                    ITEMCOUNT--;
                    itemCountTV.setText(ITEMCOUNT+"");
                    totalCostOfItem.setText(itemsClassObj.getPriceOfItem(ITEMCOUNT , itemsClassObj.foodItems[tempPos]) +"");
                }

            }
        });

        itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        foodNamesSpinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this ,android.R.layout.simple_spinner_item ,itemsClassObj.foodItems );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        foodNamesSpinner.setAdapter(arrayAdapter);

        builder.setView(view1);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addItemsToTheDatabase();
            }
        })
                .setNegativeButton("Cancel" , null)

                .setCancelable(false);
        builder.show();
    }

    private void addItemsToTheDatabase() {
       final AlertDialog dialog= Utils.getAlertDialog(this , " data Updating...");
       dialog.show();

        Map<String , Object>map = new HashMap<>();

         map.put(MyConstants.FOOD_NAME ,(foodName.getText().toString() ));
         map.put(MyConstants.FOOD_INDIVIDUAL_COST ,(itemPrice.getText().toString()));
         map.put(MyConstants.ITEM_COUNT , (itemCountTV.getText().toString() ) );
         map.put(MyConstants.FOOD_TOTAL_COST ,  (totalCostOfItem.getText().toString()));
         map.put(MyConstants.DATE ,(Utils.getDate() ));
        map.put(MyConstants.TIME , Utils.getTime());
        map.put(MyConstants.TIMESTAMP , (System.currentTimeMillis()/1000)+"");

        CollectionReference mRef = FirebaseFirestore.getInstance().collection("food");
        mRef.document(Utils.getDateAndTime()+"")
                .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(ManagerHomeActivity.this,
                            "successfull", Toast.LENGTH_SHORT).show();
                }
                else{
                    dialog.dismiss();
                    Toast.makeText(ManagerHomeActivity.this,
                            "fails", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void initialize_food_ordering_widgets(View view1) {
        addItemBtn = (ImageButton) view1.findViewById(R.id.id_item_add);
        removeItemBtn = (ImageButton)view1.findViewById(R.id.id_item_remove);
        itemImage = (CircleImageView)view1.findViewById(R.id.food_pix);
        itemCountTV = (TextView)view1.findViewById(R.id.id_item_count_);
        foodNamesSpinner = (Spinner) view1.findViewById(R.id.id_food_name_spinner);
        itemPrice = (TextView)view1.findViewById(R.id.food_item_price);
        foodName = (TextView)view1.findViewById(R.id.id_food_name);
        totalCostOfItem = (TextView)view1.findViewById(R.id.food_item_price_total_cost);
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
                       mAuth.signOut();
                       mySharedPreferences.setUserData(MyConstants.MANAGER , "0");
                       startActivity(Utils.sendUserToLoginActivity(getApplicationContext(),LoginActivity.class));  finish();
                break;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
          tempPos = pos;

          itemPrice.setText(itemsClassObj.itemCost.get(itemsClassObj.foodItems[pos]) +"");
          foodName.setText(itemsClassObj.foodItems[pos]+"");
        totalCostOfItem.setText(itemsClassObj.getPriceOfItem(ITEMCOUNT , itemsClassObj.foodItems[pos]) +"");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }



    @Override
    protected void onStart() {
        super.onStart();
        fetchAllItemsFromFirestore();
    }

    private void fetchAllItemsFromFirestore() {
        FirestoreRecyclerAdapter<MyPojo , MyViewHolder>adapter =
                new FirestoreRecyclerAdapter<MyPojo, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull MyPojo model) {

                holder.mFoodName.setText((model.getFood()));
                holder.item_cost.setText((model.getItem_cost()));
                //holder.mFoodCount.setText((model.getAdded_item()));
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.manager_added_iem_list , parent , false);
                 return new MyViewHolder(view);
            }
        };
        adapter.startListening();;
        mRecyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.xx_id_food_name)TextView mFoodName;
        @BindView(R.id.xx_food_item_price)TextView item_cost;
        @BindView(R.id.xx_food_item_delete)TextView mFoodCount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this ,itemView );
        }
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
