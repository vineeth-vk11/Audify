package com.audify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.audify.AskQuestionUI.AskQuestionAllFragment;
import com.audify.AudioItemsUI.Models.AudioItemModel;
import com.audify.CreatorsUI.CreatorsFragment;
import com.audify.CategoriesUI.CategoriesFragment;
import com.audify.DiscoverUI.DiscoverFragment;
import com.audify.HomeUI.HomeFragment;
import com.audify.MoreUI.MoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mixpanel.android.mpmetrics.MixpanelAPI;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    DiscoverFragment discoverFragment = new DiscoverFragment();
    CategoriesFragment categoriesFragment = new CategoriesFragment();
    CreatorsFragment creatorsFragment = new CreatorsFragment();
    MoreFragment moreFragment = new MoreFragment();

    public CardView currentPlayingCard;
    public ImageView speakerImage;
    public TextView question;
    public ImageButton playAndPauseButton;
    public ProgressBar progressBar;

    public MediaPlayer mediaPlayer = new MediaPlayer();

    MixpanelAPI mixpanel;

    public Handler handler;
    public Runnable runnable;

    public AudioItemModel currentPlayingAudio;

//    discoverAudioType = "discover";
//    categoryAudioType = "category";
//    creatorAudioType = "creator";

    public String currentPlayingAudioType = "None";
    public String currentPlayingCreatorId = "None";
    public String currentPlayingCategoryName = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mixpanel = MixpanelAPI.getInstance(getApplicationContext(), "7ae8227e35f5d23259e2acff044ab43c");
        mixpanel.getPeople().identify(FirebaseAuth.getInstance().getCurrentUser().getUid());

        bottomNavigationView = findViewById(R.id.home_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.discover);

        currentPlayingCard = findViewById(R.id.currentPlayingCard);
        speakerImage = findViewById(R.id.speakerImage);
        question = findViewById(R.id.question);
        playAndPauseButton = findViewById(R.id.playAndPauseButton);
        progressBar = findViewById(R.id.audioProgress);

        progressBar.getProgressDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.discover:

                mixpanel.track("Home Button Click");

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,discoverFragment);
                fragmentTransaction.commit();
                return true;
            case R.id.categories:

                mixpanel.track("Categories Button Click");

                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                fragmentTransaction1.replace(R.id.main_frame,categoriesFragment);
                fragmentTransaction1.addToBackStack(null);
                fragmentTransaction1.commit();
                return true;
            case R.id.creators:

                mixpanel.track("Creators Button Click");

                FragmentManager fragmentManager2 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
                fragmentTransaction2.replace(R.id.main_frame,creatorsFragment);
                fragmentTransaction2.addToBackStack(null);
                fragmentTransaction2.commit();
                return true;
            case R.id.more:

                mixpanel.track("More Button Click");

                FragmentManager fragmentManager3 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
                fragmentTransaction3.replace(R.id.main_frame,moreFragment);
                fragmentTransaction3.addToBackStack(null);
                fragmentTransaction3.commit();
                return true;
            case R.id.ask:

                mixpanel.track("Ask Question All Button Click");

                AskQuestionAllFragment askQuestionAllFragment = new AskQuestionAllFragment();

                FragmentManager fragmentManager4 = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction4 = fragmentManager4.beginTransaction();
                fragmentTransaction4.replace(R.id.main_frame,askQuestionAllFragment);
                fragmentTransaction4.addToBackStack(null);
                fragmentTransaction4.commit();
                return true;
        }

        return false;
    }
}