package com.zeng.bbfeeding;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zeng.bbfeeding.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private int mCurPageIndex = 0;
    private int mPrePageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Data.getInstance().init(this);

        initStatusBar();
        initToolbar();
        initPager();
        initNavigation();
    }

    @TargetApi(19)
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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

    private void initToolbar(){
        mBinding.toolbar.shareButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, getString(R.string.apk_web_url));
                startActivity(Intent.createChooser(share, getString(R.string.share_title)));
            }
        });
    }

    private void initPager(){

        final Page[] pages = new Page[]{
                new HomePage(),
                new HistoryPage(),
                new CouponPage()
        };

        mBinding.viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pages[position];
            }

            @Override
            public int getCount() {
                return  pages.length;
            }
        });

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position != mPrePageIndex) {
                    pages[mPrePageIndex].onHidePage();
                    pages[position].onShowPage();
                    mPrePageIndex = position;
                }
                if (position != mCurPageIndex) {
                    mCurPageIndex = position;

                    int resId = R.id.navigation_home;
                    if (position == 1) {resId = R.id.navigation_history;}
                    else if (position == 2) {resId = R.id.navigation_coupon;}
                    mBinding.navigation.setSelectedItemId(resId);
                }
            }
        });
    }

    private void initNavigation(){
        mBinding.navigation.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int idx = -1;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        idx = 0;
                        break;
                    case R.id.navigation_history:
                        idx = 1;
                        break;
                    case R.id.navigation_coupon:
                        idx = 2;
                        break;
                }
                if (idx == -1) return false;

                if (mCurPageIndex != idx) {
                    mCurPageIndex = idx;
                    mBinding.viewPager.setCurrentItem(idx, true);
                }

                return true;
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();
        Data.getInstance().save();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
