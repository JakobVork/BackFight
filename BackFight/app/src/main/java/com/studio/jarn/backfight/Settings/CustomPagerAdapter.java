package com.studio.jarn.backfight.Settings;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.studio.jarn.backfight.R;

// http://codetheory.in/android-image-slideshow-using-viewpager-pageradapter/
class CustomPagerAdapter extends PagerAdapter {


    private int[] mResources;
    private int[] mResourcesSelected;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ImageView mImageView;

    CustomPagerAdapter(Context context) {
        getAvatars();
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void getAvatars() {
        mResources = new int[]{
                R.drawable.player_crusader,
                R.drawable.player_girl_blond_knight,
                R.drawable.player_girl_brunette_knight,
                R.drawable.player_golden_knight,
                R.drawable.player_knight,
                R.drawable.player_knight2,
                R.drawable.player_old_man,
                R.drawable.player_squire
        };

        mResourcesSelected = new int[]{
                R.drawable.player_crusader_selected,
                R.drawable.player_girl_blond_knight_selected,
                R.drawable.player_girl_brunette_knight_selected,
                R.drawable.player_golden_knight_selected,
                R.drawable.player_knight_seleted,
                R.drawable.player_knight2_selected,
                R.drawable.player_old_man_selected,
                R.drawable.player_squire_seleted
        };
    }

    int getResourceSelected(int number) {
        return mResourcesSelected[number];
    }

    int getResource(int number) {
        return mResources[number];
    }

    @Override
    public int getCount() {
        return mResources.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void ViewAvatar(int position) {
        mImageView.setImageResource(mResources[position]);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

        mImageView = (ImageView) itemView.findViewById(R.id.pager_item_iw_image);
        mImageView.setImageResource(mResources[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
