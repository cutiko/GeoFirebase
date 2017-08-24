package cl.cutiko.geopods.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cl.cutiko.geopods.views.CategoryFragment;
import cl.cutiko.geopods.views.FavoriteFragment;
import cl.cutiko.geopods.views.map.MapFragment;

/**
 * Created by cutiko on 24-08-17.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MapFragment.newInstance();
            case 1:
                return FavoriteFragment.newInstance();
            case 2:
                return CategoryFragment.newInstance();
            default:
                return CategoryFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Map";
            case 1:
                return "Favorites";
            case 2:
                return "Category";
        }
        return null;
    }
}
