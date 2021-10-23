package com.audify.HomeUI;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
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

public class HomeFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<AudioItemModel> audioItemModelArrayList = new ArrayList<>();
    RecyclerView audioItemsRecycler;

    CardView categoryCard, categoryCard1, categoryCard2, categoryCard3;

    ShimmerFrameLayout shimmerFrameLayout;

    Activity activity;

    LottieAnimationView comingsoon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        comingsoon = view.findViewById(R.id.animationView);

        audioItemsRecycler = view.findViewById(R.id.audioItemsRecycler);
        audioItemsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        audioItemsRecycler.setHasFixedSize(true);

        categoryCard = view.findViewById(R.id.categoryCard);
        categoryCard1 = view.findViewById(R.id.categoryCard1);
        categoryCard2 = view.findViewById(R.id.categoryCard2);
        categoryCard3 = view.findViewById(R.id.categoryCard3);

        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);

        shimmerFrameLayout.startShimmer();
        shimmerFrameLayout.setVisibility(View.VISIBLE);

        getAudioItems("Startups");

        categoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comingsoon.setVisibility(View.GONE);
                audioItemsRecycler.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();

                categoryCard.setCardBackgroundColor(getResources().getColor(R.color.light_green_color));
                categoryCard1.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));
                categoryCard2.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));
                categoryCard3.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));

                getAudioItems("Startups");
            }
        });

        categoryCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comingsoon.setVisibility(View.GONE);

                audioItemsRecycler.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();

                categoryCard.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));
                categoryCard1.setCardBackgroundColor(getResources().getColor(R.color.light_green_color));
                categoryCard2.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));
                categoryCard3.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));

                getAudioItems("Product");
            }
        });

        categoryCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comingsoon.setVisibility(View.GONE);

                audioItemsRecycler.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();

                categoryCard.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));
                categoryCard1.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));
                categoryCard2.setCardBackgroundColor(getResources().getColor(R.color.light_green_color));
                categoryCard3.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));

                getAudioItems("Design");
            }
        });

        categoryCard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comingsoon.setVisibility(View.GONE);

                audioItemsRecycler.setVisibility(View.GONE);
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();

                categoryCard.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));
                categoryCard1.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));
                categoryCard2.setCardBackgroundColor(getResources().getColor(R.color.cards_bg));
                categoryCard3.setCardBackgroundColor(getResources().getColor(R.color.light_green_color));

                getAudioItems("Software");
            }
        });


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

                if(audioItemModelArrayList.isEmpty()){
                    comingsoon.setVisibility(View.VISIBLE);
                }
                else {
                    comingsoon.setVisibility(View.GONE);
                }

            }
        });
    }

}