package com.audify.DiscoverUI;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.audify.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscoverViewHolder extends RecyclerView.ViewHolder {

    TextView question;
    CardView audioCard;

    TextView audioCategory;
    TextView audioListens;
    CircleImageView creatorImage;
    TextView creatorName;
    TextView creatorDesignation;

    CardView playAndPause;
    ImageButton backRewind, forwardRewind;
    ImageView playAndPauseImage;

    SeekBar seekBar;

    ImageButton share;

    TextView currentPosition, duration;

    public DiscoverViewHolder(@NonNull View itemView) {
        super(itemView);

        question = itemView.findViewById(R.id.question);
        audioCard = itemView.findViewById(R.id.audioCard);

        audioCategory = itemView.findViewById(R.id.audioCategory);
        audioListens = itemView.findViewById(R.id.audioListens);
        creatorImage = itemView.findViewById(R.id.creatorImage);
        creatorName = itemView.findViewById(R.id.creatorName);
        creatorDesignation = itemView.findViewById(R.id.creatorDesignation);

        backRewind = itemView.findViewById(R.id.backwardRewind);
        forwardRewind = itemView.findViewById(R.id.forwardRewind);
        playAndPause = itemView.findViewById(R.id.playAndPause);
        playAndPauseImage = itemView.findViewById(R.id.playAndPauseImage);

        seekBar = itemView.findViewById(R.id.seekBar);

        share = itemView.findViewById(R.id.shareButton);

        currentPosition = itemView.findViewById(R.id.currentTimestamp);
        duration = itemView.findViewById(R.id.duration);

    }
}
