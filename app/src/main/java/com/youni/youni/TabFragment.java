package com.youni.youni;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {

    TabLayout tb;
    ViewPager vp;
    private static final int items = 2;
    private static final String[] category = new String[]{"My Courses","Featured Courses"};


    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_tab,container,false);

        tb = (TabLayout) v.findViewById(R.id.tabLayout1);
        vp = (ViewPager) v.findViewById(R.id.viewPager1);

        vp.setAdapter(new MyAdapter(getChildFragmentManager()));

        vp.post(new Runnable() {
            @Override
            public void run() {
                tb.setupWithViewPager(vp);
            }
        });










        return v;
    }

    class MyAdapter extends FragmentPagerAdapter{


        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){

                case 0:

                    MyCourses myCourse = new MyCourses();
                    return myCourse;
                case 1:

                    Featuredcourses featuredcourses = new Featuredcourses();
                    return featuredcourses;

            }

            return null;
        }

        @Override
        public int getCount() {
            return items;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return category[position];
        }
    }

}
