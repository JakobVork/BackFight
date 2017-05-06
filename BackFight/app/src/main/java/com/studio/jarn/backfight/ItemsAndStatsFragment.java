package com.studio.jarn.backfight;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.studio.jarn.backfight.Items.gameItem;

import java.util.ArrayList;

public class ItemsAndStatsFragment extends Fragment {

    private OnItemSelectedListener mListener;

    private ArrayList<gameItem> mItemList;
    private ListView mItemListView;

    public ItemsAndStatsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_items_and_stats, container, false);

        mItemList = new ArrayList<gameItem>();
        mItemList.add(new gameItem("gameItem", "Test sword", R.drawable.item_sword));
        mItemList.add(new gameItem("gameItem", "Test sword", R.drawable.item_breastplate));

        final ItemAdaptor adapter = new ItemAdaptor(getActivity(), mItemList);
        mItemListView = (ListView) view.findViewById(R.id.fragment_Item_List);
        mItemListView.setAdapter(adapter);
        mItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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

    public interface OnItemSelectedListener {
        void onItemSelected(gameItem item);
    }
}
