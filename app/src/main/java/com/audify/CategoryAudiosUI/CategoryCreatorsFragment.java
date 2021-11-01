package com.audify.CategoryAudiosUI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.audify.CreatorsUI.Adapters.CreatorsAdapter;
import com.audify.CreatorsUI.Models.CreatorsModel;
import com.audify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class CategoryCreatorsFragment extends Fragment {

    RecyclerView creatorsRecycler;
    ArrayList<CreatorsModel> creatorsModelArrayList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ProgressBar progressBar;

    String categoryName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_creators, container, false);

        Bundle bundle = getArguments();
        assert bundle != null;
        categoryName = bundle.getString("categoryName");

        creatorsRecycler = view.findViewById(R.id.creatorsRecycler);
        creatorsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        creatorsRecycler.setHasFixedSize(true);

        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        db.collection("Creators").whereArrayContains("categories", categoryName).orderBy("orderId").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                creatorsModelArrayList.clear();

                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    CreatorsModel creatorsModel = new CreatorsModel();
                    creatorsModel.setCreatorImage(documentSnapshot.getString("creatorImage"));
                    creatorsModel.setCreatorName(documentSnapshot.getString("creatorName"));
                    creatorsModel.setCreatorDesignation(documentSnapshot.getString("creatorDesignation"));
                    creatorsModel.setCreatorBytes(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("creatorBytes"))));
                    creatorsModel.setCreatorFollowers(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("creatorFollowers"))));
                    creatorsModel.setOrderId(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("orderId"))));
                    creatorsModel.setCreatorId(documentSnapshot.getId());

                    creatorsModelArrayList.add(creatorsModel);
                }

                Collections.sort(creatorsModelArrayList, new Comparator<CreatorsModel>() {
                    @Override public int compare(CreatorsModel c1, CreatorsModel c2) {
                        return c1.getOrderId()- c2.getOrderId();
                    }
                });

                CreatorsAdapter creatorsAdapter = new CreatorsAdapter(getContext(), creatorsModelArrayList);
                creatorsRecycler.setAdapter(creatorsAdapter);

                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        return view;
    }
}