package com.example.pranav_project.utils;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pranav_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class NavHelper {
   public static CollectionReference orderRef;



    public static void enableNavView(final Activity context , BottomNavigationView view)
    {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case  R.id.id_home :
                        Utils.toast(context ,"home");
                        break;
                    case R.id.id_orders:
                        Utils.toast(context ,"orders");
                        fetchOrdersData();
                        break;

                }
                return false;
            }
        });
    }

    private static void fetchOrdersData() {
        orderRef = FirebaseFirestore.getInstance().collection(MyConstants.FOOD_REF);

        orderRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc : snapshots.getDocumentChanges())
                {
                    if(doc.getType()==DocumentChange.Type.ADDED)
                     {
                        System.out.println("............"+ doc.getDocument().getId());
                      }

                }
                System.out.println();
            }
        });
    }
}
