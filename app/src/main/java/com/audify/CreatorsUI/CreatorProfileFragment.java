package com.audify.CreatorsUI;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.audify.AskQuestionUI.AskQuestionFragment;
import com.audify.AudioItemsUI.Adapters.AudioItemAdapter;
import com.audify.AudioItemsUI.Models.AudioItemModel;
import com.audify.CreatorAudiosUI.Adapters.CreatorAudioAdapter;
import com.audify.CreatorsUI.Models.CreatorsModel;
import com.audify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

public class CreatorProfileFragment extends Fragment {

    ImageView creatorImage;
    TextView creatorName, creatorDesignation, creatorBytes, creatorFollowers, followOrFollowingText;
    CardView askAQuestion, followOrUnfollowCard;
    RecyclerView creatorAudiosRecycler;

    CreatorsModel creator;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<AudioItemModel> audioItemModelArrayList = new ArrayList<>();

    Activity activity;

    Boolean isFollowingCreator = false;

    MixpanelAPI mixpanel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_creator_profile, container, false);

        mixpanel = MixpanelAPI.getInstance(getContext(), "7ae8227e35f5d23259e2acff044ab43c");
        mixpanel.getPeople().identify(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Bundle bundle = getArguments();
        creator = (CreatorsModel) bundle.getSerializable("creator");

        creatorImage = view.findViewById(R.id.creatorImage);
        creatorName = view.findViewById(R.id.creatorName);
        creatorDesignation = view.findViewById(R.id.creatorDesignation);
        creatorBytes = view.findViewById(R.id.creatorBytes);
        creatorFollowers = view.findViewById(R.id.creatorFollowers);
        askAQuestion = view.findViewById(R.id.askAQuestion);
        creatorAudiosRecycler = view.findViewById(R.id.creatorAudiosRecycler);

        followOrUnfollowCard = view.findViewById(R.id.followOrUnfollow);
        followOrFollowingText = view.findViewById(R.id.followingOrFollowText);

        Picasso.get().load(creator.getCreatorImage()).into(creatorImage);
        creatorName.setText(creator.getCreatorName());
        creatorDesignation.setText(creator.getCreatorDesignation());
        creatorBytes.setText(String.valueOf(creator.getCreatorBytes()));
        creatorFollowers.setText(String.valueOf(creator.getCreatorFollowers()));

        creatorAudiosRecycler = view.findViewById(R.id.creatorAudiosRecycler);
        creatorAudiosRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        creatorAudiosRecycler.setNestedScrollingEnabled(false);
        creatorAudiosRecycler.setHasFixedSize(true);

        askAQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("creatorId", creator.getCreatorId());
                bundle.putString("creatorName", creator.getCreatorName());

                AskQuestionFragment askQuestionFragment = new AskQuestionFragment();

                askQuestionFragment.setArguments(bundle);

                FragmentManager fragmentManager1 = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                fragmentTransaction1.replace(R.id.main_frame,askQuestionFragment);
                fragmentTransaction1.addToBackStack(null);
                fragmentTransaction1.commit();
            }
        });

        db.collection("Creators").document(creator.getCreatorId()).collection("Audios").orderBy("orderId").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    audioItemModel.setOrderId(Integer.parseInt(Objects.requireNonNull(documentSnapshot.getString("orderId"))));

                    audioItemModel.setPlaying(false);

                    audioItemModelArrayList.add(audioItemModel);
                }

                Collections.sort(audioItemModelArrayList, new Comparator<AudioItemModel>() {
                    @Override public int compare(AudioItemModel a1, AudioItemModel a2) {
                        return a1.getOrderId()- a2.getOrderId();
                    }
                });

                activity = getActivity();

                CreatorAudioAdapter creatorAudioAdapter = new CreatorAudioAdapter(getContext(), audioItemModelArrayList, activity);
                creatorAudiosRecycler.setAdapter(creatorAudioAdapter);
            }
        });

        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Following")
                .document(creator.getCreatorId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot documentSnapshot = task.getResult();

                if(!documentSnapshot.exists()){
                    isFollowingCreator = false;
                    followOrFollowingText.setText("Follow");
                }
                else{
                    isFollowingCreator = true;
                    followOrFollowingText.setText("Unfollow");
                }

                followOrUnfollowCard.setVisibility(View.VISIBLE);
            }
        });

        followOrUnfollowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFollowingCreator){

                    JSONObject props = new JSONObject();
                    try {
                        props.put("creatorId", creator.getCreatorId());
                        props.put("creatorName", creator.getCreatorName());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Creator Follow Clicked", props);

                    isFollowingCreator = true;
                    followOrFollowingText.setText("Unfollow");
                    creator.setCreatorFollowers(creator.getCreatorFollowers() + 1);
                    creatorFollowers.setText(String.valueOf(creator.getCreatorFollowers()));

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("creatorName", creator.getCreatorName());
                    data.put("creatorImage", creator.getCreatorImage());

                    db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Following")
                            .document(creator.getCreatorId()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            HashMap<String, Object> data1 = new HashMap<>();
                            data1.put("name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                            data1.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());

                            db.collection("Creators").document(creator.getCreatorId()).collection("Followers")
                                    .document(FirebaseAuth.getInstance().getUid()).set(data1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    HashMap<String, Object> data2 = new HashMap<>();
                                    data2.put("creatorFollowers", String.valueOf(creator.getCreatorFollowers()));

                                    db.collection("Creators").document(creator.getCreatorId()).update(data2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseMessaging.getInstance().subscribeToTopic(creator.getCreatorId());
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else{

                    JSONObject props = new JSONObject();
                    try {
                        props.put("creatorId", creator.getCreatorId());
                        props.put("creatorName", creator.getCreatorName());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Creator Unfollow Clicked", props);

                    isFollowingCreator = false;
                    followOrFollowingText.setText("Follow");
                    creator.setCreatorFollowers(creator.getCreatorFollowers() - 1);
                    creatorFollowers.setText(String.valueOf(creator.getCreatorFollowers()));

                    db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Following")
                            .document(creator.getCreatorId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            db.collection("Creators").document(creator.getCreatorId()).collection("Followers")
                                    .document(FirebaseAuth.getInstance().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    HashMap<String, Object> data2 = new HashMap<>();
                                    data2.put("creatorFollowers", String.valueOf(creator.getCreatorFollowers()));

                                    db.collection("Creators").document(creator.getCreatorId()).update(data2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(creator.getCreatorId());
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

        return view;
    }
}