package com.fries.edoo.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.fries.edoo.R;
import com.fries.edoo.fragment.PostWriterContentFragment;
import com.fries.edoo.fragment.PostWriterTagFragment;

/**
 * Created by tmq on 20/08/2016.
 */
public class PostWriterActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private Button btnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_writer_view_pager);

        initViews();
    }

    private void initViews(){
        toolbar = (Toolbar) findViewById(R.id.tb_post_writer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tl_post_writer);
        viewPager = (ViewPager) findViewById(R.id.vp_post_writer);

        viewPager.setAdapter(new PostAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).setText("Content");
//        tabLayout.getTabAt(1).setText("Tag");

        btnNext = (Button) findViewById(R.id.btn_next_to_post_option);
        btnNext.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0:
                btnNext.setText("Tiếp tục");
                break;
            case 1:
                btnNext.setText("Đăng bài");
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (viewPager.getCurrentItem()){
            case 0:
                viewPager.setCurrentItem(1, true);  break;
            case 1:
                //sdklafkj
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ----------------------------------------- Adapter -------------------------------------------
    private class PostAdapter extends FragmentStatePagerAdapter{
        private PostWriterContentFragment postWriterContent;
        private PostWriterTagFragment postWriterTagFragment;

        public PostAdapter(FragmentManager fm) {
            super(fm);
            postWriterContent = new PostWriterContentFragment();
            postWriterTagFragment = new PostWriterTagFragment();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return postWriterContent;
                default: return postWriterTagFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
