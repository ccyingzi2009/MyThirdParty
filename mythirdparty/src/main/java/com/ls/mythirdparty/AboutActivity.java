package com.ls.mythirdparty;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ls.ui.InkPageIndicator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 15-11-23.
 */
public class AboutActivity extends AppCompatActivity {

    @Bind(R.id.pager)
    ViewPager pager;
    @Bind(R.id.indicator)
    InkPageIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        pager.setAdapter(new AboutPagerAdapter(this));

        indicator.setupViewPager(pager);
    }


    static class AboutPagerAdapter extends PagerAdapter {
        private View mView;
        private Context mContext;
        private LayoutInflater mInflater;

        public AboutPagerAdapter(Context mContext) {
            this.mContext = mContext;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = getView(container, position);
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        private View getView(ViewGroup container, int position) {
            if (position == 0) {
            } else if (position == 1) {
            } else if (position == 2) {
            }
            mView = mInflater.inflate(R.layout.activity_about_test, container, false);
            return mView;
        }
    }

}
