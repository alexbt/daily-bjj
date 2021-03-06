package com.alexbt.bjj.dailybjj.videos;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.alexbt.bjj.dailybjj.R;
import com.alexbt.bjj.dailybjj.model.DailyEntry;
import com.alexbt.bjj.dailybjj.util.DateHelper;
import com.alexbt.bjj.dailybjj.util.RemoteHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;

import jp.wasabeef.picasso.transformations.CropTransformation;

public class SingleImageFragment extends Fragment implements Observer<ImageData>, View.OnClickListener {
    private int page;
    private View root;
    private DailyEntry dailyEntry;
    private LocalDate localDate;

    public SingleImageFragment(DailyEntry dailyEntry, int i) {
        this.dailyEntry = dailyEntry;
        this.page = i;
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        setArguments(args);
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        localDate = DateHelper.getLastWeekPlusDays(page);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_image, container, false);

        AdView mAdView = (AdView) root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (!mAdView.isActivated()) {
            mAdView.loadAd(adRequest);
        }

        TextView notificationDate = root.findViewById(R.id.date);
        notificationDate.setText(localDate.toString());

        ImageView imageView = root.findViewById(R.id.image_view);
        imageView.setOnClickListener(this);

        Picasso.get().load(RemoteHelper.getInstance().getImageUrl(dailyEntry.getYoutubeId()))
                .resize(480, 935)
                .transform(new CropTransformation(480, 700, CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityVertical.CENTER))
                //.transform(new CropTransformation(480,300,  CropTransformation.GravityHorizontal.CENTER, CropTransformation.GravityVertical.TOP))
                .into(imageView);
        TextView desc = root.findViewById(R.id.description);
        desc.setText(dailyEntry.getDescription() + " (" + dailyEntry.getVideoDate() + ")");

        TextView title = root.findViewById(R.id.title);
        title.setText(dailyEntry.getTitle());

        TextView dDay = root.findViewById(R.id.dDay);
        if (page == 7) {
            dDay.setText("");
        } else {
            dDay.setText("D-" + (7 - page));
        }

        TextView master = root.findViewById(R.id.masterName);
        master.setText(dailyEntry.getMaster());

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RemoteHelper.getInstance().getYoutubeVideoUrl(dailyEntry.getYoutubeId())));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(RemoteHelper.getInstance().getWebVideoUrl(dailyEntry.getYoutubeId())));
            startActivity(webIntent);
        }
    }

    @Override
    public void onChanged(ImageData imageData) {
        ImageView listView = root.findViewById(R.id.image_view);
        listView.setImageBitmap(imageData.getImage());
        TextView textView = root.findViewById(R.id.description);
        textView.setText(imageData.getToday().getDescription() + " (" + dailyEntry.getVideoDate() + ")");

        textView = root.findViewById(R.id.title);
        textView.setText(imageData.getToday().getTitle());

        TextView master = root.findViewById(R.id.masterName);
        master.setText(imageData.getToday().getMaster());

        listView.setOnClickListener(this);
    }
}
