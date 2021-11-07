package com.audify.DiscoverUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.audify.AudioItemsUI.Adapters.AudioItemAdapter;
import com.audify.AudioItemsUI.Models.AudioItemModel;
import com.audify.MainActivity;
import com.audify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DiscoverFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<AudioItemModel> audioItemModelArrayList = new ArrayList<>();

    Activity activity;

    CardStackView cardStackView;
    CardStackLayoutManager cardStackLayoutManager;

    DiscoverAdapter discoverAdapter;

    MediaPlayer mediaPlayer;
    int currentPlayingPosition;

    MixpanelAPI mixpanel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        mixpanel = MixpanelAPI.getInstance(getContext(), "7ae8227e35f5d23259e2acff044ab43c");
        mixpanel.getPeople().identify(FirebaseAuth.getInstance().getCurrentUser().getUid());

        activity = getActivity();

        cardStackView = view.findViewById(R.id.discover_stack_view);

        getAudios();

        return view;
    }

    private void setupCards(){
        cardStackLayoutManager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }
            @Override
            public void onCardSwiped(Direction direction) {
                if(direction == Direction.Top){

                }
                else if(direction == Direction.Bottom){

                }
                else if(direction == Direction.Left){

                }
                else if(direction == Direction.Right){

                }

                if(cardStackLayoutManager.getTopPosition() == discoverAdapter.getItemCount()){
                    paginate();
                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

                if(((MainActivity)activity).currentPlayingAudioType.equals("discover") || ((MainActivity)activity).currentPlayingAudioType.equals("None")){
                    mediaPlayer = ((MainActivity)activity).mediaPlayer;
                    SeekBar seekBar = view.findViewById(R.id.seekBar);

                    ((MainActivity)activity).currentPlayingAudio = audioItemModelArrayList.get(position);
                    ((MainActivity)activity).currentPlayingAudioType = "discover";

                    currentPlayingPosition = position;

                    ((MainActivity)activity).handler = new Handler();
                    ((MainActivity)activity).runnable = new Runnable() {
                        @Override
                        public void run() {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            TextView currentPosition = view.findViewById(R.id.currentTimestamp);

                            int currentPositionNumber = mediaPlayer.getCurrentPosition();
                            String currentPositionString = convertFormat(currentPositionNumber);
                            currentPosition.setText(currentPositionString);

                            ((MainActivity)activity).handler.postDelayed(this, 500);
                        }
                    };

                    try {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(audioItemModelArrayList.get(position).getAnswer());
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        TextView duration = view.findViewById(R.id.duration);

                        int durationNumber = mediaPlayer.getDuration();
                        String durationString = convertFormat(durationNumber);
                        duration.setText(durationString);

                        ImageView playAndPauseImage = view.findViewById(R.id.playAndPauseImage);
                        playAndPauseImage.setImageResource(R.drawable.ic_pause);

                        audioItemModelArrayList.get(position).setPlaying(true);

                        seekBar.setMax(mediaPlayer.getDuration());

                        ((MainActivity)activity).handler.postDelayed(((MainActivity)activity).runnable, 0);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(((MainActivity)activity).currentPlayingAudio != null){
                                db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("History")
                                        .document(((MainActivity)activity).currentPlayingAudio.getQuestionId()).set(((MainActivity)activity).currentPlayingAudio)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                HashMap<String, Object> data = new HashMap<>();
                                                data.put("listeners", String.valueOf(Integer.parseInt(((MainActivity)activity).currentPlayingAudio.getListeners()) + 1));

                                                db.collection(((MainActivity)activity).currentPlayingAudio.getCategory())
                                                        .document(((MainActivity)activity).currentPlayingAudio.getQuestionId())
                                                        .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(!((MainActivity)activity).currentPlayingAudio.getCreatorId().equals("Null")){
                                                            db.collection("Creators").document(((MainActivity)activity).currentPlayingAudio.getCreatorId())
                                                                    .collection("Audios").document(((MainActivity)activity).currentPlayingAudio.getQuestionId())
                                                                    .update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    JSONObject props = new JSONObject();
                                                                    try {
                                                                        props.put("audioId", ((MainActivity)activity).currentPlayingAudio.getQuestionId());
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    mixpanel.track("Audio Listened", props);
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        });
                            }
                        }
                    }, 10000);
                }
            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });

        cardStackLayoutManager.setStackFrom(StackFrom.Top);
        cardStackLayoutManager.setVisibleCount(3);
        cardStackLayoutManager.setTranslationInterval(10.0f);
        cardStackLayoutManager.setScaleInterval(0.90f);
        cardStackLayoutManager.setSwipeThreshold(0.3f);
        cardStackLayoutManager.setMaxDegree(20.0f);
        cardStackLayoutManager.setCanScrollHorizontal(true);
        cardStackLayoutManager.setCanScrollVertical(true);
        cardStackLayoutManager.setDirections(Direction.FREEDOM);
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        cardStackLayoutManager.setOverlayInterpolator(new LinearInterpolator());
        discoverAdapter = new DiscoverAdapter(getContext(),audioItemModelArrayList,cardStackView, activity, cardStackLayoutManager);
        cardStackView.setAdapter(discoverAdapter);
        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private void paginate() {
        ArrayList<AudioItemModel> audioItemModelArrayList = discoverAdapter.getAudioItemModelArrayList();
        ArrayList<AudioItemModel> newAudioItemModelArrayList = new ArrayList<>(getAudios());
        CardStackCallBack cardStackCallBack = new CardStackCallBack(audioItemModelArrayList,newAudioItemModelArrayList);
        DiffUtil.DiffResult hasil = DiffUtil.calculateDiff(cardStackCallBack);
        discoverAdapter.setAudioItemModelArrayList(newAudioItemModelArrayList);
        hasil.dispatchUpdatesTo(discoverAdapter);
    }

    private ArrayList<AudioItemModel> getAudios(){
        db.collection("AllAudios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    audioItemModel.setAnswer(documentSnapshot.getString("answerAudio"));
                    audioItemModel.setQuestionId(documentSnapshot.getId());
                    audioItemModel.setCreatorId(documentSnapshot.getString("creatorId"));

                    audioItemModel.setPlaying(false);

                    audioItemModelArrayList.add(audioItemModel);
                }

                Collections.shuffle(audioItemModelArrayList);

                setupCards();
            }
        });
        return audioItemModelArrayList;
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }


}