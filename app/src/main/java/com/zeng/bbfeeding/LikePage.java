package com.zeng.bbfeeding;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class LikePage extends Page{
    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_like);

        initShare();
    }

    private void initShare(){
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
