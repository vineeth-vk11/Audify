package com.audify.CategoryAudiosUI;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.audify.AudioItemsUI.Adapters.AudioItemAdapter;
import com.audify.AudioItemsUI.Models.AudioItemModel;
import com.audify.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategoryAudiosFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<AudioItemModel> audioItemModelArrayList = new ArrayList<>();
    RecyclerView audioItemsRecycler;

    ShimmerFrameLayout shimmerFrameLayout;

    Activity activity;

    String categoryName;
    TextView categoryNameText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_audios, container, false);

        Bundle bundle = getArguments();
        assert bundle != null;
        categoryName = bundle.getString("categoryName");

        audioItemsRecycler = view.findViewById(R.id.audioItemsRecycler);
        audioItemsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        audioItemsRecycler.setHasFixedSize(true);

        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);

        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);

        categoryNameText = view.findViewById(R.id.categoryName);
        categoryNameText.setText(categoryName);

        getAudioItems(categoryName);

        return view;
    }

    public void getAudioItems(String category){

        db.collection(category).orderBy("orderId").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                audioItemModelArrayList.clear();

                for(DocumentSnapshot documentSnapshot: task.getResult()){

                    AudioItemModel audioItemModel = new AudioItemModel();
                    audioItemModel.setCreatorImage(documentSnapshot.getString("creatorImage"));
                    audioItemModel.setCreatorName(documentSnapshot.getString("creatorName"));
                    audioItemModel.setCreatorDescription(documentSnapshot.getString("creatorDescription"));
                    audioItemModel.setQuestion(documentSnapshot.getString("question"));
                    audioItemModel.setCategory(documentSnapshot.getString("category"));
                    audioItemModel.setDuration(documentSnapshot.getString("duration"));
                    audioItemModel.setListeners(documentSnapshot.getString("listeners"));
                    audioItemModel.setAnswer(documentSnapshot.getString("answerAudio"));
                    audioItemModel.setQuestionId(documentSnapshot.getId());
                    audioItemModel.setCreatorId(documentSnapshot.getString("creatorId"));

                    audioItemModel.setPlaying(false);

                    audioItemModelArrayList.add(audioItemModel);
                }

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                audioItemsRecycler.setVisibility(View.VISIBLE);

                activity = getActivity();

                AudioItemAdapter audioItemAdapter = new AudioItemAdapter(getContext(), audioItemModelArrayList, activity);
                audioItemsRecycler.setAdapter(audioItemAdapter);

            }
        });
    }

}