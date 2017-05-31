package com.studio.jarn.backfight.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemWeapon;
import com.studio.jarn.backfight.Player.Player;
import com.studio.jarn.backfight.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ItemsAndStatsFragment extends Fragment {

    private final static String sJsonItemsString = "sJsonItemsString";
    private final static String sNameString = "sNameString";
    private final static String sHpString = "sHpString";
    private final static String sMinDmgString = "sMinDmgString";
    private final static String sMaxDmgString = "sMaxDmgString";
    private OnItemSelectedListener mListener;
    private ArrayList<GameItem> mItemList;
    private String mName;
    private int mHp;
    private int mMinDmg;
    private int mMaxDmg;

    public ItemsAndStatsFragment() {
    }

    public static ItemsAndStatsFragment newInstance(Player player) {
        if (player.PlayerItems != null) {
            ItemsAndStatsFragment fragment = new ItemsAndStatsFragment();
            Bundle args = new Bundle();
            String jsonArray = new Gson().toJson(player.PlayerItems);
            args.putString(sJsonItemsString, jsonArray);
            args.putString(sNameString, player.Name);
            args.putInt(sHpString, player.Health);
            args.putInt(sMinDmgString, player.getMinDmg());
            args.putInt(sMaxDmgString, player.getMaxDmg());
            fragment.setArguments(args);
            return fragment;
        }

        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String jsonItems = getArguments().getString(sJsonItemsString);
            Type listType = new TypeToken<ArrayList<ItemWeapon>>() {
            }.getType();
            mItemList = new Gson().fromJson(jsonItems, listType);
            mName = getArguments().getString(sNameString);
            mHp = getArguments().getInt(sHpString);
            mMinDmg = getArguments().getInt(sMinDmgString);
            mMaxDmg = getArguments().getInt(sMaxDmgString);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_and_stats, container, false);

        final ItemAdaptor adapter = new ItemAdaptor(getActivity(), mItemList);
        ListView mItemListView = (ListView) view.findViewById(R.id.fragment_Item_List);
        mItemListView.setAdapter(adapter);
        mItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onItemSelected(mItemList.get(position));
            }
        });

        TextView mTvName = (TextView) view.findViewById(R.id.fragment_item_tv_name);
        mTvName.setText(mName);

        TextView mTvHp = (TextView) view.findViewById(R.id.fragment_item_tv_hp);
        mTvHp.setText("HP: " + String.valueOf(mHp));

        TextView mTvDmg = (TextView) view.findViewById(R.id.fragment_item_tv_dmg);
        mTvDmg.setText("Dmg: " + String.valueOf(mMinDmg) + " - " + String.valueOf(mMaxDmg));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedListener) {
            mListener = (OnItemSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(GameItem item);
    }
}
