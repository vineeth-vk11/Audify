package com.audify.CreatorsUI.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.audify.CreatorsUI.CreatorProfileFragment;
import com.audify.CreatorsUI.CreatorsFragment;
import com.audify.CreatorsUI.Models.CreatorsModel;
import com.audify.CreatorsUI.ViewHolders.CreatorsViewHolder;
import com.audify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CreatorsAdapter extends RecyclerView.Adapter<CreatorsViewHolder> implements Filterable {

    Context context;
    ArrayList<CreatorsModel> creatorsModelArrayList;
    ArrayList<CreatorsModel> creatorsModelArrayListAll;

    public CreatorsAdapter(Context context, ArrayList<CreatorsModel> creatorsModelArrayList) {
        this.context = context;
        this.creatorsModelArrayList = creatorsModelArrayList;
        this.creatorsModelArrayListAll = new ArrayList<>(creatorsModelArrayList);
    }

    @NonNull
    @Override
    public CreatorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_creator, parent, false);
        return new CreatorsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreatorsViewHolder holder, int position) {

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, "7ae8227e35f5d23259e2acff044ab43c");
        mixpanel.getPeople().identify(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Picasso.get()
                .load(creatorsModelArrayList.get(position).getCreatorImage())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.creatorImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(creatorsModelArrayList.get(position).getCreatorImage())
                                .into(holder.creatorImage);
                    }
                });

        holder.creator.setBackground(context.getDrawable(R.drawable.glass_card_bg));
        holder.creatorName.setText(creatorsModelArrayList.get(position).getCreatorName());
        holder.creatorDesignation.setText(creatorsModelArrayList.get(position).getCreatorDesignation());
        holder.creatorBytes.setText(String.valueOf(creatorsModelArrayList.get(position).getCreatorBytes()));
        holder.creatorFollowers.setText(String.valueOf(creatorsModelArrayList.get(position).getCreatorFollowers()));

        holder.creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject props = new JSONObject();
                try {
                    props.put("creatorId", creatorsModelArrayList.get(holder.getAdapterPosition()).getCreatorId());
                    props.put("creatorName", creatorsModelArrayList.get(holder.getAdapterPosition()).getCreatorName());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Creator Item Clicked", props);

                Bundle bundle = new Bundle();
                bundle.putSerializable("creator", creatorsModelArrayList.get(position));

                CreatorProfileFragment creatorProfileFragment = new CreatorProfileFragment();
                creatorProfileFragment.setArguments(bundle);

                AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();
                FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,creatorProfileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return creatorsModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<CreatorsModel> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(creatorsModelArrayListAll);
            }
            else {
                for(int i = 0; i<creatorsModelArrayListAll.size();i++){
                    if(creatorsModelArrayListAll.get(i).getCreatorName().toLowerCase().contains(constraint.toString().toLowerCase())
                    || creatorsModelArrayListAll.get(i).getCreatorDesignation().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(creatorsModelArrayListAll.get(i));
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            creatorsModelArrayList.clear();
            creatorsModelArrayList.addAll((Collection<? extends CreatorsModel>) results.values);
            notifyDataSetChanged();
        }
    };
}
