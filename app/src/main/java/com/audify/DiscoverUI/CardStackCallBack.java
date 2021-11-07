package com.audify.DiscoverUI;

import androidx.recyclerview.widget.DiffUtil;

import com.audify.AudioItemsUI.Models.AudioItemModel;

import java.util.ArrayList;

public class CardStackCallBack extends DiffUtil.Callback {

    ArrayList<AudioItemModel> oldAudioItemModelArrayList, newAudioItemModelArrayList;


    public CardStackCallBack(ArrayList<AudioItemModel> oldAudioItemModelArrayList, ArrayList<AudioItemModel> newAudioItemModelArrayList) {
        this.oldAudioItemModelArrayList = oldAudioItemModelArrayList;
        this.newAudioItemModelArrayList = newAudioItemModelArrayList;
    }

    @Override
    public int getOldListSize() {
        return oldAudioItemModelArrayList.size();
    }

    @Override
    public int getNewListSize() {
        return newAudioItemModelArrayList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldAudioItemModelArrayList.get(oldItemPosition).getQuestionId() == newAudioItemModelArrayList.get(newItemPosition).getQuestionId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldAudioItemModelArrayList.get(oldItemPosition) == newAudioItemModelArrayList.get(newItemPosition);
    }
}
