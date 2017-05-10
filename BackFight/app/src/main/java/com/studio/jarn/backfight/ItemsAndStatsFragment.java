package com.studio.jarn.backfight;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemWeapon;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ItemsAndStatsFragment extends Fragment {

    private OnItemSelectedListener mListener;

    private ArrayList<GameItem> mItemList;
    private ListView mItemListView;
    private final static String jsonItemsString = "jsonItemsString";

    public ItemsAndStatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String jsonItems = getArguments().getString(jsonItemsString);
            Type listType = new TypeToken<ArrayList<ItemWeapon>>(){}.getType();
            mItemList = new Gson().fromJson(jsonItems, listType);
        }
    }

    public static ItemsAndStatsFragment newInstance(ArrayList<GameItem> items) {
        if(items == null) {
            return null;
        }

        ItemsAndStatsFragment fragment = new ItemsAndStatsFragment();
        Bundle args = new Bundle();
        String jsonArray = new Gson().toJson(items);
        args.putString(jsonItemsString, jsonArray);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_and_stats, container, false);

        final ItemAdaptor adapter = new ItemAdaptor(getActivity(), mItemList);
        mItemListView = (ListView) view.findViewById(R.id.fragment_Item_List);
        mItemListView.setAdapter(adapter);
        mItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onItemSelected(mItemList.get(position));
            }
        });

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

    interface OnItemSelectedListener {
        void onItemSelected(GameItem item);
    }
}
