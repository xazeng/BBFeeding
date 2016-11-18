package com.zeng.bbfeeding;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;

/**
 * Created by xianganzeng on 2016/10/27.
 */

public class LikePage extends Page{
    @Override
    protected void onCreatePage(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.page_like);

        initBannerAd();
    }

    private void initBannerAd(){
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
}
