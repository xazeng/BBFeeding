package com.zeng.bbfeeding;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity {

    private int mPagePosition;
    private Fragment[] mPages;
    private int[] mTabIds;

    private ViewPager mViewPager;
    private BottomBar mBottomBar;
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar)findViewById(R.id.tool_bar);
        mViewPager = (ViewPager)findViewById(R.id.view_pager);
        mBottomBar = (BottomBar)findViewById(R.id.bottom_bar);

        mPagePosition = 0;
        mPages = new Fragment[]{
                HomePage.newInstance(),
                HistoryPage.newInstance(),
                LikePage.newInstance(),
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
            statusView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

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
}
