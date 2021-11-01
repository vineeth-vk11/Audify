package com.audify.CategoriesUI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.audify.CategoriesUI.Adapters.CategoriesAdapter;
import com.audify.CategoriesUI.Models.CategoriesModel;
import com.audify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView categoriesRecycler;
    ArrayList<CategoriesModel> categoriesModelArrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        categoriesRecycler = view.findViewById(R.id.categoriesRecycler);
        categoriesRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        categoriesRecycler.setHasFixedSize(true);

        db.collection("Categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 categoriesModelArrayList.clear();

                 for(DocumentSnapshot documentSnapshot: task.getResult()){
                     CategoriesModel categoriesModel = new CategoriesModel();
                     categoriesModel.setCategoryName(documentSnapshot.getString("categoryName"));
                     categoriesModel.setCategoryImage(documentSnapshot.getString("categoryImage"));
                     categoriesModel.setIsActive(documentSnapshot.getString("isActive"));
                     categoriesModel.setCategorySvg(documentSnapshot.getString("svg"));

                     categoriesModelArrayList.add(categoriesModel);
                 }

                CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getContext(), categoriesModelArrayList);
                 categoriesRecycler.setAdapter(categoriesAdapter);
            }
        });

        return view;
    }
}