package com.audify.CreatorsUI.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.audify.R;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatorsViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView creatorImage;
    public TextView creatorName, creatorDesignation, creatorBytes, creatorFollowers;
    public CardView creator;

    public CreatorsViewHolder(@NonNull View itemView) {
        super(itemView);

        creatorImage = itemView.findViewById(R.id.creatorImage);
        creatorName = itemView.findViewById(R.id.creatorName);
        creatorDesignation = itemView.findViewById(R.id.creatorDesignation);
        creatorBytes = itemView.findViewById(R.id.creatorBytes);
        creatorFollowers = itemView.findViewById(R.id.creatorFollowers);
        creator = itemView.findViewById(R.id.creator);

    }
}
