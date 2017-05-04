package com.studio.jarn.backfight;

import android.content.Context;
import android.graphics.Color;
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

    private ArrayList<gameItem> itemList;
    private ListView itemListView;

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

        itemList = new ArrayList<gameItem>();
        itemList.add(new gameItem("gameItem", "Test sword", R.drawable.item_sword));
        itemList.add(new gameItem("gameItem", "Test sword", R.drawable.item_breastplate));

        final itemAdaptor adapter = new itemAdaptor(getActivity(), itemList);
        itemListView = (ListView) view.findViewById(R.id.fragment_Item_List);
        itemListView.setAdapter(adapter);
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onItemSelected(itemList.get(position));
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
