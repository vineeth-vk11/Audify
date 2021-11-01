package com.audify.CategoriesUI.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.audify.R;

public class CategoriesViewHolder extends RecyclerView.ViewHolder {

    public TextView categoryName;
    public ImageView categoryImage;
    public ConstraintLayout category;
    public CardView categoryCard;

    public CategoriesViewHolder(@NonNull View itemView) {
        super(itemView);

        categoryName = itemView.findViewById(R.id.categoryName);
        categoryImage = itemView.findViewById(R.id.categoryImage);
        category = itemView.findViewById(R.id.category);
        categoryCard = itemView.findViewById(R.id.categoryCard1);

    }
}
