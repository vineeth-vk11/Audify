package com.audify.AudioItemsUI.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.audify.R;

public class AudioItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView creatorImage;
    public TextView question, category, duration, listeners, creatorDescription;
    public CardView audioCard;

    public AudioItemViewHolder(@NonNull View itemView) {
        super(itemView);

        creatorImage = itemView.findViewById(R.id.speakerImage);
        creatorDescription = itemView.findViewById(R.id.creatorDesignation);
        question = itemView.findViewById(R.id.question);
        category = itemView.findViewById(R.id.category);
        duration = itemView.findViewById(R.id.duration);
        listeners = itemView.findViewById(R.id.listeners);
        audioCard = itemView.findViewById(R.id.audioCard);

    }
}
