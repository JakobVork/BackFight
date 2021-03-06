package com.studio.jarn.backfight.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.studio.jarn.backfight.Monster.Monster;
import com.studio.jarn.backfight.R;

public class MonsterDetails extends Fragment {
    private static final String mMonsterString = "mMonsterString";

    private Monster mMonster;


    public MonsterDetails() {
        // Required empty public constructor
    }

    public static MonsterDetails newInstance(Monster monster) {
        MonsterDetails fragment = new MonsterDetails();
        Bundle args = new Bundle();
        String jsonMonster = new Gson().toJson(monster);
        args.putString(mMonsterString, jsonMonster);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMonster = new Gson().fromJson(getArguments().getString(mMonsterString), Monster.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_monster_details, container, false);

        ImageView mIvImage = (ImageView) view.findViewById(R.id.fragment_monster_iv_image);
        TextView mTvName = (TextView) view.findViewById(R.id.fragment_monster_tv_name);
        TextView mTvHp = (TextView) view.findViewById(R.id.fragment_monster_tv_hp);
        TextView mTvDmg = (TextView) view.findViewById(R.id.fragment_monster_tv_dmg);

        mIvImage.setImageResource(mMonster.Figure);
        mTvName.setText(mMonster.Name);
        mTvHp.setText(getString(R.string.hp) + ": " + String.valueOf(mMonster.HitPoints));
        mTvDmg.setText(getString(R.string.dmg) + ": " + String.valueOf(mMonster.AttackPower));

        return view;
    }
}
