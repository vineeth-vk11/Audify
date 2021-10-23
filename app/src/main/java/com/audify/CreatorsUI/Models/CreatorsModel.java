package com.audify.CreatorsUI.Models;

import androidx.cardview.widget.CardView;

import java.io.Serializable;
import java.util.Comparator;

public class CreatorsModel implements Serializable {

    String creatorImage, creatorName, creatorDesignation;
    int creatorBytes, creatorFollowers;
    CardView listenToCreator;
    String creatorId;
    int orderId;

    public CreatorsModel() {
    }

    public String getCreatorImage() {
        return creatorImage;
    }

    public void setCreatorImage(String creatorImage) {
        this.creatorImage = creatorImage;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorDesignation() {
        return creatorDesignation;
    }

    public void setCreatorDesignation(String creatorDesignation) {
        this.creatorDesignation = creatorDesignation;
    }

    public int getCreatorBytes() {
        return creatorBytes;
    }

    public void setCreatorBytes(int creatorBytes) {
        this.creatorBytes = creatorBytes;
    }

    public int getCreatorFollowers() {
        return creatorFollowers;
    }

    public void setCreatorFollowers(int creatorFollowers) {
        this.creatorFollowers = creatorFollowers;
    }

    public CardView getListenToCreator() {
        return listenToCreator;
    }

    public void setListenToCreator(CardView listenToCreator) {
        this.listenToCreator = listenToCreator;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
