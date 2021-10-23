package com.audify.CreatorAudiosUI.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.audify.R;

public class CreatorAudioViewHolder extends RecyclerView.ViewHolder {

    public TextView question, category, duration, listeners;
    public CardView audioCard;

    public CreatorAudioViewHolder(@NonNull View itemView) {
        super(itemView);
        question = itemView.findViewById(R.id.question);
        category = itemView.findViewById(R.id.category);
        duration = itemView.findViewById(R.id.duration);
        listeners = itemView.findViewById(R.id.listeners);
        audioCard = itemView.findViewById(R.id.audioCard);
    }
}
