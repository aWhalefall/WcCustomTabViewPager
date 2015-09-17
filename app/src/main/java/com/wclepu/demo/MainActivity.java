package com.wclepu.demo;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;
import com.wclepu.demo.adapter.SlidingPagerAdapter;
import com.wclepu.demo.fragment.ScrollTabHolder;
import com.wclepu.demo.sliding.PagerSlidingTabStrip;

public class MainActivity extends FragmentActivity implements OnPageChangeListener, ScrollTabHolder {

    private PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    private SlidingPagerAdapter adapter;
    private LinearLayout header;
    private int headerHeight;
    private int headerTranslationDis;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getHeaderHeight();
        findViews();
        setupPager();
        setupTabs();
    }

    private void findViews() {
        tabs = (PagerSlidingTabStrip) findViewById(R.id.show_tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);
        header = (LinearLayout) findViewById(R.id.header);
        relativeLayout = (RelativeLayout) findViewById(R.id.title);
    }

    private void getHeaderHeight() {
        headerHeight = getResources().getDimensionPixelSize(R.dimen.max_header_height);
        headerTranslationDis = -getResources().getDimensionPixelSize(R.dimen.header_offset_dis);
    }

    private void setupPager() {
        adapter = new SlidingPagerAdapter(getSupportFragmentManager(), this, viewPager);
        adapter.setTabHolderScrollingListener(this);
        viewPager.setOffscreenPageLimit(adapter.getCacheCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void setupTabs() {
        tabs.setShouldExpand(true);
        tabs.setIndicatorColorResource(R.color.color_purple_bd6aff);
        tabs.setUnderlineColorResource(R.color.color_purple_bd6aff);
        tabs.setCheckedTextColorResource(R.color.color_purple_bd6aff);
        tabs.setViewPager(viewPager);
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        tabs.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        tabs.onPageSelected(position);
        reLocation = true;
        SparseArrayCompat<ScrollTabHolder> scrollTabHolders = adapter.getScrollTabHolders();
        ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);
        if (NEED_RELAYOUT) {
            currentHolder.adjustScroll((int) (header.getHeight() + headerTop));
        } else {
            currentHolder.adjustScroll((int) (header.getHeight() + ViewHelper.getTranslationY(header)));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        tabs.onPageScrollStateChanged(state);
    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }

    private boolean reLocation = false;

    private int headerScrollSize = 0;

    public static final boolean NEED_RELAYOUT = Integer.valueOf(Build.VERSION.SDK).intValue() < Build.VERSION_CODES.HONEYCOMB;

    private int headerTop = 0;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount,
                         int pagePosition) {
        if (viewPager.getCurrentItem() != pagePosition) {
            return;
        }
        if (headerScrollSize == 0 && reLocation) {
            reLocation = false;
            return;
        }
        reLocation = false;
        int scrollY = Math.max(-getScrollY(view), headerTranslationDis);
        if (NEED_RELAYOUT) {
            headerTop = scrollY;
            header.post(new Runnable() {
                @Override
                public void run() {
                    header.layout(0, headerTop, header.getWidth(), headerTop + header.getHeight());
                }
            });
        } else {
            ViewHelper.setTranslationY(header, scrollY);
            if (scrollY < -400) {
                relativeLayout.setAlpha(1.0f);
            } else {
                relativeLayout.setAlpha(0.1f);
            }
        }
    }


    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int top = c.getTop();
        int firstVisiblePosition = view.getFirstVisiblePosition();
        if (firstVisiblePosition == 0) {
            return -top + headerScrollSize;
        } else if (firstVisiblePosition == 1) {
            return -top;
        } else {
            return -top + (firstVisiblePosition - 2) * c.getHeight() + headerHeight;
        }
    }

    @Override
    public void onHeaderScroll(boolean isRefreashing, int value, int pagePosition) {
        if (viewPager.getCurrentItem() != pagePosition) {
            return;
        }
        headerScrollSize = value;
        if (NEED_RELAYOUT) {
            header.post(new Runnable() {

                @Override
                public void run() {
                    Log.e("Main", "scorry=" + (-headerScrollSize));
                    header.layout(0, -headerScrollSize, header.getWidth(), -headerScrollSize + header.getHeight());
                }
            });
        } else {
            ViewHelper.setTranslationY(header, -value);
        }
    }

}
