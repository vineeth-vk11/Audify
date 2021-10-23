package com.audify.MoreUI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.audify.LoginUI.LoginMainActivity;
import com.audify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreFragment extends Fragment {

    CircleImageView userImage;
    TextView userName, userListened, userFollowing;
    ConstraintLayout inviteFriends, talkToFounder, becomeCreator, logout;

    FirebaseUser firebaseUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    MixpanelAPI mixpanel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        mixpanel = MixpanelAPI.getInstance(getContext(), "7ae8227e35f5d23259e2acff044ab43c");
        mixpanel.getPeople().identify(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userImage = view.findViewById(R.id.userProfileImage);
        userName = view.findViewById(R.id.userName);
        userListened = view.findViewById(R.id.userListened);
        userFollowing = view.findViewById(R.id.userFollowing);
        inviteFriends = view.findViewById(R.id.inviteFriends);
        talkToFounder = view.findViewById(R.id.talkWithFounder);
        becomeCreator = view.findViewById(R.id.becomeCreator);
        logout = view.findViewById(R.id.logout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Picasso.get()
                .load(firebaseUser.getPhotoUrl())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(userImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(firebaseUser.getPhotoUrl())
                                .into(userImage);
                    }
                });

        userName.setText(firebaseUser.getDisplayName());

        db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("Following").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    userFollowing.setText("0");
                }
                else{
                    userFollowing.setText(String.valueOf(task.getResult().size()));
                }
            }
        });

        db.collection("Users").document(FirebaseAuth.getInstance().getUid()).collection("History").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    userListened.setText("0");
                }
                else{
                    userListened.setText(String.valueOf(task.getResult().size()));
                }
            }
        });


        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mixpanel.track("Invite Click");

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "I found a way to learn something new in less than 5 minutes through byte-sized audio on Audify. On Audify you can listen to answers by experts in 10+ categories for Free. Have a question? Ask experts and get personalized audio answers. I recommend you checkout Audify - https://play.google.com/store/apps/details?id=com.audify");
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });

        talkToFounder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mixpanel.track("TalkToFounder Button Click");

                String url = "https://api.whatsapp.com/send/?phone=916265104906&text=Hey,%20I%20want%20to%20share%20something%20about%20Audify";
                Intent i = new
                        Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        becomeCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mixpanel.track("Become Creator Button Click");

                String url = "https://www.audify.club/creator";
                Intent i = new
                        Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mixpanel.track("Logout Button Click");

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginMainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}