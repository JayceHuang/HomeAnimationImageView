package com.example.shibin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.shibin.AnimateViewPager;
import com.example.shibin.BaseFragment;
import com.example.shibin.R;
import com.example.shibin.view.AppFragment;
import com.example.shibin.view.GameFragment;
import com.example.shibin.view.HomeFragment;
import com.example.shibin.view.ImageHomeScrollTabStrip;
import com.example.shibin.view.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeView extends LinearLayout {

    private FragmentActivity mFragmentActivity;
    private final LayoutInflater inflater;
    private AnimateViewPager mViewPager;
    private ImageHomeScrollTabStrip mHomeScrollTabStrip;
    private UIFragmentPageAdapter mUIFragmentPageAdapter;
    private List<BaseFragment> mFragmentsList = new ArrayList<BaseFragment>();

    private ViewPager.PageTransformer mTransformer = new DepthPageTransformer();
    public HomeView(FragmentActivity context, Bundle savedInstanceState) {
        super(context);
        this.mFragmentActivity = context;
        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.activity_main, this);
        mHomeScrollTabStrip = (ImageHomeScrollTabStrip) findViewById(R.id.nav_tabs);

        mViewPager = (AnimateViewPager) findViewById(R.id.detail_viewPager);
        mViewPager.setPageTransformer(true, mTransformer);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mUIFragmentPageAdapter = new UIFragmentPageAdapter(mFragmentActivity.getSupportFragmentManager());
//        mViewPager.setAdapter(mUIFragmentPageAdapter);
//        mHomeScrollTabStrip.setViewPager(mViewPager);


        mFragmentsList.add(new HomeFragment());
        mFragmentsList.add(new VideoFragment());
        mFragmentsList.add(new GameFragment());
        mFragmentsList.add(new AppFragment());
        mHomeScrollTabStrip.setOnPageChangeListener(mUIFragmentPageAdapter);

        mHomeScrollTabStrip.setOnTabClickListener(new ImageHomeScrollTabStrip.OnTabInteractListener() {

            @Override
            public void onclick(final int pisition) {
//                if (mViewPager.getCurrentItem() == pisition) {
//                    return;
//                }
//                mViewPager.changePageimmediately(pisition);

            }

            @Override
            public void onSwip(boolean isUp) {

            }
        });
    }

    private class UIFragmentPageAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
        private final String[] mHomeTasTitles;
        private int mLastSelectPager = 0;

        public UIFragmentPageAdapter(FragmentManager fm) {
            super(fm);
            mHomeTasTitles = getResources().getStringArray(R.array.home_tabs);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mHomeTasTitles[position];
        }

        @Override
        public BaseFragment getItem(int positon) {
            return mFragmentsList.get(positon);
        }

        @Override
        public int getCount() {
            return mFragmentsList.size();
        }

        @Override
        public void onPageScrollStateChanged(int newState) {
            mViewPager.setScrollState(newState);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mLastSelectPager >= 0) {
                mUIFragmentPageAdapter.getItem(mLastSelectPager).onViewPagerStop();
            }
//			if(mLastSelectPager == 0 && position > 0){//从首页切换到其他页面

//			}else if(position == 0 && mLastSelectPager > 0){
//				mPtrFrameLayout.open(true);
//			}
            mLastSelectPager = position;
            mUIFragmentPageAdapter.getItem(mLastSelectPager).onViewPagerResume();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mViewPager.setObjectForPosition(fragment, position);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //重载该方法，防止其它视图被销毁，防止加载视图卡顿
            super.destroyItem(container, position, object);
        }
    }
}
