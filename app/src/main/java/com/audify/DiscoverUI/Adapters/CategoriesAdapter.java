package com.audify.DiscoverUI.Adapters;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.audify.CategoryAudiosUI.CategoryAudiosFragment;
import com.audify.DiscoverUI.Models.CategoriesModel;
import com.audify.DiscoverUI.ViewHolders.CategoriesViewHolder;
import com.audify.R;
import com.audify.Utils.GlideApp;
import com.bumptech.glide.Glide;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesViewHolder> {

    Context context;
    ArrayList<CategoriesModel> categoriesModelArrayList;

    public CategoriesAdapter(Context context, ArrayList<CategoriesModel> categoriesModelArrayList) {
        this.context = context;
        this.categoriesModelArrayList = categoriesModelArrayList;
    }

    private RequestBuilder<PictureDrawable> requestBuilder;

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_item_discover_categories, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {

        MixpanelAPI mixpanel = MixpanelAPI.getInstance(context, "7ae8227e35f5d23259e2acff044ab43c");
        mixpanel.getPeople().identify(FirebaseAuth.getInstance().getCurrentUser().getUid());

        GlideApp.with(context).load(categoriesModelArrayList.get(holder.getAdapterPosition()).getCategorySvg())
                .apply(RequestOptions.centerCropTransform()).into(holder.categoryImage);

        holder.categoryName.setText(categoriesModelArrayList.get(position).getCategoryName());

        holder.categoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject props = new JSONObject();
                try {
                    props.put("category", categoriesModelArrayList.get(holder.getAdapterPosition()).getCategoryName());
                    props.put("isActive", categoriesModelArrayList.get(holder.getAdapterPosition()).getIsActive());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Category Item Clicked", props);

                if(categoriesModelArrayList.get(position).getIsActive().equals("FALSE")){
                    Toast.makeText(context.getApplicationContext(), "Coming Soon, checkout the other open categories", Toast.LENGTH_SHORT).show();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("categoryName", categoriesModelArrayList.get(position).getCategoryName());

                    CategoryAudiosFragment categoryAudiosFragment = new CategoryAudiosFragment();
                    categoryAudiosFragment.setArguments(bundle);

                    AppCompatActivity appCompatActivity = (AppCompatActivity) v.getContext();
                    FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame,categoryAudiosFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesModelArrayList.size();
    }
}
