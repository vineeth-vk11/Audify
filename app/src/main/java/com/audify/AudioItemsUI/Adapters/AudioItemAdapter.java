package com.audify.AudioItemsUI.Adapters;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.audify.AudioItemsUI.Models.AudioItemModel;
import com.audify.AudioItemsUI.ViewHolders.AudioItemViewHolder;
import com.audify.MainActivity;
import com.audify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AudioItemAdapter extends RecyclerView.Adapter<AudioItemViewHolder> {

    Context context;
    ArrayList<AudioItemModel> audioItemModelArrayList;
    Activity activity;

    MediaPlayer mediaPlayer;

    public AudioItemAdapter(Context context, ArrayList<AudioItemModel> audioItemModelArrayList, Activity activity) {
        this.context = context;
        this.audioItemModelArrayList = audioItemModelArrayList;
        this.activity = activity;
    }

    int currentPlayingPosition;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @NonNull
    @Override
    public AudioItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_audio_item, parent, false);
        return new AudioItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioItemViewHolder holder, int position) {

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, "7ae8227e35f5d23259e2acff044ab43c");
        mixpanel.getPeople().identify(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(holder.getAdapterPosition() != -1){
            Picasso.get()
                    .load(audioItemModelArrayList.get(position).getCreatorImage())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.creatorImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(audioItemModelArrayList.get(position).getCreatorImage())
                                    .into(holder.creatorImage);
                        }
                    });
        }

        holder.question.setText(audioItemModelArrayList.get(holder.getAdapterPosition()).getQuestion());
        holder.category.setText(audioItemModelArrayList.get(holder.getAdapterPosition()).getCategory());
        holder.duration.setText(audioItemModelArrayList.get(holder.getAdapterPosition()).getDuration() + " min");
        holder.listeners.setText(audioItemModelArrayList.get(holder.getAdapterPosition()).getListeners());
        holder.creatorDescription.setText(audioItemModelArrayList.get(holder.getAdapterPosition()).getCreatorName() + ", " + audioItemModelArrayList.get(holder.getAdapterPosition()).getCreatorDescription());

        mediaPlayer = ((MainActivity)activity).mediaPlayer;

        holder.audioCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject props = new JSONObject();
                try {
                    props.put("audioId", audioItemModelArrayList.get(holder.getAdapterPosition()).getQuestionId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Audio Item Clicked", props);

                ((MainActivity)activity).currentPlayingAudio = audioItemModelArrayList.get(holder.getAdapterPosition());

                currentPlayingPosition = holder.getAdapterPosition();

                ((MainActivity)activity).handler = new Handler();
                ((MainActivity)activity).runnable = new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity)activity).progressBar.setProgress(mediaPlayer.getCurrentPosition());
                        ((MainActivity)activity).handler.postDelayed(this, 500);
                    }
                };

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
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
                }, 10000);

                ((MainActivity)activity).currentPlayingCard.setVisibility(View.VISIBLE);
                ((MainActivity)activity).question.setText(audioItemModelArrayList.get(holder.getAdapterPosition()).getQuestion());
                Picasso.get().load(audioItemModelArrayList.get(holder.getAdapterPosition()).getCreatorImage()).into(((MainActivity)activity).speakerImage);

                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(audioItemModelArrayList.get(holder.getAdapterPosition()).getAnswer());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    ((MainActivity)activity).playAndPauseButton.setImageResource(R.drawable.ic_pause);
                    audioItemModelArrayList.get(holder.getAdapterPosition()).setPlaying(true);

                    ((MainActivity)activity).progressBar.setMax(mediaPlayer.getDuration());
                    ((MainActivity)activity).handler.postDelayed(((MainActivity)activity).runnable, 0);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ((MainActivity)activity).playAndPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
//                    ((MainActivity)activity).handler.removeCallbacks(((MainActivity)activity).runnable);
                    ((MainActivity)activity).playAndPauseButton.setImageResource(R.drawable.ic_play);

                    mixpanel.track("Pause Button Clicked");

                }
                else {
                    mediaPlayer.start();
                    ((MainActivity)activity).playAndPauseButton.setImageResource(R.drawable.ic_pause);

                    mixpanel.track("Play Button Clicked");
                }
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

                if(currentPlayingPosition == audioItemModelArrayList.size()){
                    currentPlayingPosition = 0;
                }
                else{
                    currentPlayingPosition += 1;
                }

                ((MainActivity)activity).handler = new Handler();
                ((MainActivity)activity).runnable = new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity)activity).progressBar.setProgress(mediaPlayer.getCurrentPosition());
                        ((MainActivity)activity).handler.postDelayed(this, 500);
                    }
                };

                ((MainActivity)activity).currentPlayingCard.setVisibility(View.VISIBLE);
                ((MainActivity)activity).question.setText(audioItemModelArrayList.get(currentPlayingPosition).getQuestion());
                Picasso.get().load(audioItemModelArrayList.get(currentPlayingPosition).getCreatorImage()).into(((MainActivity)activity).speakerImage);

                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(audioItemModelArrayList.get(currentPlayingPosition).getAnswer());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    ((MainActivity)activity).playAndPauseButton.setImageResource(R.drawable.ic_pause);
                    audioItemModelArrayList.get(currentPlayingPosition).setPlaying(true);

                    ((MainActivity)activity).progressBar.setMax(mediaPlayer.getDuration());
                    ((MainActivity)activity).handler.postDelayed(((MainActivity)activity).runnable, 0);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return audioItemModelArrayList.size();
    }
}
