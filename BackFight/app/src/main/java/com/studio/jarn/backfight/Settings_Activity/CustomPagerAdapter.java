package com.studio.jarn.backfight.Settings_Activity;

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
    private String[] mResourcesPath;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ImageView mImageView;

    CustomPagerAdapter(Context context) {
        getAvatars();
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    //TODO get real avatars
    private void getAvatars() {
        mResources = new int[]{R.drawable.player32, R.drawable.item_arrow_flaming, R.drawable.monster_mauler, R.drawable.item_arrow_flaming, R.drawable.monster_mauler};
        mResourcesPath = new String[]{"R.drawable.player32", "R.drawable.item_arrow_flaming", "R.drawable.monster_mauler", "R.drawable.item_arrow_flaming", "R.drawable.monster_mauler"};
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

    String getResourcePath(int number) {

        return mResourcesPath[number];

    }
}
