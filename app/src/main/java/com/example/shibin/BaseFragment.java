package com.example.shibin;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

public abstract class BaseFragment extends Fragment {
    protected FragmentActivity mFragmentActivity;

    private boolean isViewPagerResume = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mFragmentActivity = (FragmentActivity) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean isViewPagerResume() {
        return isViewPagerResume;
    }

    public void onViewPagerResume() {
        isViewPagerResume = true;
    }

    public void onViewPagerStop() {
        isViewPagerResume = false;
    }

    public boolean onBackPress() {
        return false;
    }

    /**
     * 保证fragment的oncreateView只被初始化一次，避免资源浪费
     */
    protected View onlyOneOnCreateView(View rootView) {
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    public AbsListView getCurrentListView() {
        return null;
    }
}
