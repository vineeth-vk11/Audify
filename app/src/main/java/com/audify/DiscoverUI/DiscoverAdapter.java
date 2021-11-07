package com.audify.DiscoverUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.audify.AudioItemsUI.Models.AudioItemModel;
import com.audify.MainActivity;
import com.audify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverViewHolder> {

    Context context;
    ArrayList<AudioItemModel> audioItemModelArrayList = new ArrayList<>();
    CardStackView cardStackView;
    Activity activity;
    CardStackLayoutManager cardStackLayoutManager;

    public DiscoverAdapter(Context context, ArrayList<AudioItemModel> audioItemModelArrayList, CardStackView cardStackView, Activity activity, CardStackLayoutManager cardStackLayoutManager) {
        this.context = context;
        this.audioItemModelArrayList = audioItemModelArrayList;
        this.cardStackView = cardStackView;
        this.activity = activity;
        this.cardStackLayoutManager = cardStackLayoutManager;
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    MediaPlayer mediaPlayer;
    int currentPlayingPosition;

    MixpanelAPI mixpanel;

    @NonNull
    @Override
    public DiscoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_discover, parent, false);
        return new DiscoverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiscoverViewHolder holder, int position) {

        mixpanel = MixpanelAPI.getInstance(context, "7ae8227e35f5d23259e2acff044ab43c");
        mixpanel.getPeople().identify(FirebaseAuth.getInstance().getCurrentUser().getUid());

        holder.audioCard.setBackground(context.getDrawable(R.drawable.glass_bg_discover));
        holder.question.setText(audioItemModelArrayList.get(holder.getLayoutPosition()).getQuestion());

        holder.audioCategory.setText(audioItemModelArrayList.get(holder.getLayoutPosition()).getCategory());
        holder.audioListens.setText(String.format("%s Listens", audioItemModelArrayList.get(holder.getLayoutPosition()).getListeners()));
        holder.creatorDesignation.setText(audioItemModelArrayList.get(holder.getLayoutPosition()).getCreatorDescription());
        holder.creatorName.setText(audioItemModelArrayList.get(holder.getLayoutPosition()).getCreatorName());

        if(holder.getLayoutPosition() != -1){
            Picasso.get()
                    .load(audioItemModelArrayList.get(holder.getLayoutPosition()).getCreatorImage())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.creatorImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(audioItemModelArrayList.get(holder.getLayoutPosition()).getCreatorImage())
                                    .into(holder.creatorImage);
                        }
                    });
        }

        db.collection(audioItemModelArrayList.get(holder.getLayoutPosition()).getCategory()).document(audioItemModelArrayList.get(holder.getLayoutPosition()).getQuestionId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot documentSnapshot = task.getResult();

                assert documentSnapshot != null;
                if(documentSnapshot.exists()){
                    holder.audioListens.setText(String.valueOf(Objects.requireNonNull(documentSnapshot.getString("listeners"))));
                    audioItemModelArrayList.get(holder.getLayoutPosition()).setListeners(documentSnapshot.getString("listeners"));
                }
                else{
                    holder.audioListens.setText("0 Listens");
                    audioItemModelArrayList.get(holder.getLayoutPosition()).setListeners("0");
                }
            }
        });

        mediaPlayer = ((MainActivity)activity).mediaPlayer;

        holder.playAndPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayAndPause(holder);
            }
        });

        holder.audioCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayAndPause(holder);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                JSONObject props = new JSONObject();
                try {
                    props.put("audioId", audioItemModelArrayList.get(holder.getAdapterPosition()).getQuestionId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mixpanel.track("Audio Played Completely", props);

                if(((MainActivity) activity).currentPlayingAudioType.equals("discover")){
                    cardStackView.swipe();
                }
                else{
                    ((MainActivity) activity).currentPlayingCard.setVisibility(View.GONE);
                }
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Checkout the audio byte for '" + audioItemModelArrayList.get(holder.getAdapterPosition()).getQuestion() + "' by " + audioItemModelArrayList.get(holder.getAdapterPosition()).getCreatorName() + " (" + audioItemModelArrayList.get(holder.getAdapterPosition()).getCreatorDescription() + ")"  + "\n\nInstall the Audify.club app at https://play.google.com/store/apps/details?id=com.audify" );
                context.startActivity(Intent.createChooser(intent, "Share"));

                JSONObject props = new JSONObject();
                try {
                    props.put("audioId", audioItemModelArrayList.get(holder.getAdapterPosition()).getQuestionId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mixpanel.track("Share Audio Clicked", props);
            }
        });

        holder.forwardRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                if(mediaPlayer.isPlaying() && duration!=currentPosition){
                    currentPosition = currentPosition + 10000;
                    holder.currentPosition.setText(convertFormat(currentPosition));
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });

        holder.backRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                if(mediaPlayer.isPlaying() && currentPosition > 10000){
                    currentPosition = currentPosition - 10000;
                    holder.currentPosition.setText(convertFormat(currentPosition));
                    mediaPlayer.seekTo(currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return audioItemModelArrayList.size();
    }

    public ArrayList<AudioItemModel> getAudioItemModelArrayList() {
        return audioItemModelArrayList;
    }

    public void setAudioItemModelArrayList(ArrayList<AudioItemModel> audioItemModelArrayList) {
        this.audioItemModelArrayList = audioItemModelArrayList;
    }

    public void handlePlayAndPause(DiscoverViewHolder holder){
        if(!((MainActivity)activity).currentPlayingAudioType.equals("discover")){
            mediaPlayer.stop();
            mediaPlayer.reset();

            ((MainActivity)activity).currentPlayingAudioType = "discover";
            ((MainActivity)activity).currentPlayingCard.setVisibility(View.GONE);

            ((MainActivity)activity).currentPlayingAudio = audioItemModelArrayList.get(holder.getAdapterPosition());
            currentPlayingPosition = holder.getAdapterPosition();

            ((MainActivity)activity).handler = new Handler();
            ((MainActivity)activity).runnable = new Runnable() {
                @Override
                public void run() {
                    holder.seekBar.setProgress(mediaPlayer.getCurrentPosition());

                    int currentPositionNumber = mediaPlayer.getCurrentPosition();
                    String currentPositionString = convertFormat(currentPositionNumber);
                    holder.currentPosition.setText(currentPositionString);

                    ((MainActivity)activity).handler.postDelayed(this, 500);
                }
            };

            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(audioItemModelArrayList.get(holder.getAdapterPosition()).getAnswer());
                mediaPlayer.prepare();
                mediaPlayer.start();

                int durationNumber = mediaPlayer.getDuration();
                String durationString = convertFormat(durationNumber);
                holder.duration.setText(durationString);

                holder.playAndPauseImage.setImageResource(R.drawable.ic_pause);

                audioItemModelArrayList.get(holder.getAdapterPosition()).setPlaying(true);

                holder.seekBar.setMax(mediaPlayer.getDuration());

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
        else{
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                holder.playAndPauseImage.setImageResource(R.drawable.ic_play);

                mixpanel.track("Pause Button Clicked");
            }
            else {
                mediaPlayer.start();
                holder.playAndPauseImage.setImageResource(R.drawable.ic_pause);

                mixpanel.track("Play Button Clicked");
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}
