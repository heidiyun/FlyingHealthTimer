package xyz.flyinghealthtimer.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SectionPageAdapter  extends FragmentPagerAdapter {

    List<Fragment> fragmentList = new ArrayList<Fragment>();
    List<String> fragmentTitleList = new ArrayList<String>();

    public SectionPageAdapter(FragmentManager fn) {
        super(fn);
    }

    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }



    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
