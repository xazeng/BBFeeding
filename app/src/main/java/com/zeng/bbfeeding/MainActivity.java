package com.zeng.bbfeeding;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity {

    private int mPagePosition;
    private int mPrePagePosition;
    private Page[] mPages;
    private int[] mTabIds;

    private ViewPager mViewPager;
    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Data.getInstance().init(this);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-6858874865593788~1172352152");

        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mBottomBar = (BottomBar)findViewById(R.id.bottom_bar);

        mPagePosition = 0;
        mPrePagePosition = 0;
        mPages = new Page[]{
                new HomePage(),
                new HistoryPage(),
                new LikePage(),
        };
        mTabIds = new int[]{
                R.id.tab_home,
                R.id.tab_history,
                R.id.tab_like,
        };

        initStatusBar();
        initToolBar();
        initViewPager();
        initBottomBar();

        return;
    }

    @TargetApi(19)
    private void initStatusBar(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // 生成一个状态栏大小的矩形
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            View statusView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    statusBarHeight);
            statusView.setLayoutParams(params);
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            statusView.setBackgroundColor(typedValue.data);

            // 添加 statusView 到布局中
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            decorView.addView(statusView);

            // 设置根布局的参数
            ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    private void initToolBar(){
        findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName();
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, getString(R.string.apk_web_url)+appPackageName);
                startActivity(Intent.createChooser(share, getString(R.string.share_title)));
            }
        });

        findViewById(R.id.star_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.apk_market_url) + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.apk_web_url) + appPackageName)));
                }
            }
        });
    }

    private void initViewPager(){
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mPages[position];
            }

            @Override
            public int getCount() {
                return mPages.length;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position != mPrePagePosition) {
                    mPages[mPrePagePosition].onHidePage();
                    mPages[position].onShowPage();
                    mPrePagePosition = position;
                }

                if (position != mPagePosition){
                    mPagePosition = position;
                    mBottomBar.selectTabAtPosition(position);
                }
            }
        });
    }

    private void initBottomBar() {
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                int position = 0;
                for (; position < mTabIds.length; ++position){
                    if (mTabIds[position] == tabId) break;
                }

                if (position != mPagePosition) {
                    mPagePosition = position;
                    mViewPager.setCurrentItem(position, true);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Data.getInstance().save();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), R.string.exit_toast, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
