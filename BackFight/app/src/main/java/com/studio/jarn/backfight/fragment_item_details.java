package com.studio.jarn.backfight;

import android.os.Bundle;
import android.renderscript.Script;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemWeapon;

public class fragment_item_details extends Fragment {

    public fragment_item_details() {
        // Required empty public constructor
    }

    private String mTitle;
    private String mDescription;
    private int mImage;
    private int mDmgMin;
    private int mDmgMax;


    private static final String mTitleString = "TitleString";
    private static final String mDescriptionString = "DescriptionString";
    private static final String mImageString = "ImageString";
    private static final String mDmgMinString = "DmgMinString";
    private static final String mDmgMaxString = "DmgMaxString";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTitle = getArguments().getString(mTitleString);
            mDescription = getArguments().getString(mDescriptionString);
            mImage = getArguments().getInt(mImageString, 0);
            mDmgMin= getArguments().getInt(mDmgMinString, 0);
            mDmgMax= getArguments().getInt(mDmgMaxString, 0);
        }
    }

    public static fragment_item_details newInstance(ItemWeapon weapon) {
        if(weapon == null) {
            return null;
        }

        fragment_item_details fragment = new fragment_item_details();
        Bundle args = new Bundle();
        args.putString(mTitleString, weapon.Title);
        args.putString(mDescriptionString, weapon.Description);
        args.putInt(mImageString, weapon.Image);
        args.putInt(mDmgMinString, weapon.DmgMin);
        args.putInt(mDmgMaxString, weapon.DmgMax);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);

        ImageView img = (ImageView) view.findViewById(R.id.fragment_item_details_img);
        TextView tvTitle = (TextView) view.findViewById(R.id.fragment_item_details_title);
        TextView tvDescription = (TextView) view.findViewById(R.id.fragment_item_details_description);
        TextView tvStats = (TextView) view.findViewById(R.id.fragment_item_details_stats);

        img.setImageResource(mImage);
        tvTitle.setText(mTitle);
        tvDescription.setText(mDescription);
        tvStats.setText(mDmgMin + "-" + mDmgMax);

        ImageButton ibBack = (ImageButton) view.findViewById(R.id.imageButton);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
