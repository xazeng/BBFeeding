package com.zeng.bbfeeding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;

import java.io.File;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class LikePage extends Page{
    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_like);

        initAd();
        initShare();
    }

    private void initAd(){
        final AdView bannerAd = (AdView)findViewById(R.id.banner_ad_view);
        AdRequest bannerRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        bannerAd.loadAd(bannerRequest);

        NativeExpressAdView nativeAd = (NativeExpressAdView)findViewById(R.id.native_ad_view);

        AdRequest nativeRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        nativeAd.loadAd(nativeRequest);
    }

    private void initShare(){
        final Button shareBtn = (Button)findViewById(R.id.share_test_button);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
            }
        });
    }

    private void shareText(){
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.igg.bzbee.deckheroes");
        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void shareText2(){
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.igg.bzbee.deckheroes");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void shareImage(){

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");

        String imagePath = Environment.getExternalStorageDirectory()
                + "/myImage.jpg";
        File imageFileToShare = new File(imagePath);
        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(share, "Share Image!"));
    }

    private void shareBoth(){
        Intent share = new Intent(Intent.ACTION_SEND);

        share.setType("*/*");

        share.putExtra(Intent.EXTRA_TEXT, "http://www.codeofaninja.com");

        String imagePath = Environment.getExternalStorageDirectory()
                + "/myImage.jpg";
        File imageFileToShare = new File(imagePath);
        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(share, "Share Image!"));

    }
}
