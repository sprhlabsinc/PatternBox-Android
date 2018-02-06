package com.premium.patternbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.premium.patternbox.NotionMainActivity;
import com.premium.patternbox.R;
import com.premium.patternbox.app.NotionCategoryInfo;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class NotionCategoryListAdapter extends  ArrayAdapter<NotionCategoryInfo> {
    private final Context mContext;
    private ArrayList<NotionCategoryInfo> dataSet;
    private int selectedPosition = -1;
    private NotionMainActivity mActivity;

    public NotionCategoryListAdapter(ArrayList<NotionCategoryInfo> data, Context context, NotionMainActivity activity) {
        super(context, R.layout.item_notion_adapter, data);
        this.dataSet = data;
        this.mContext=context;
        this.mActivity = activity;
    }
    @Override
    public NotionCategoryInfo getItem(int position){
        return dataSet.get(position);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater;
        AppInfoHolder holder = null;
        final NotionCategoryInfo dataModel = getItem(position);
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.item_notion_adapter, parent, false);

            holder = new AppInfoHolder();
            holder.notion_but = (Button) itemView.findViewById(R.id.notion_but);
            holder.edit_but = (Button) itemView.findViewById(R.id.edit_but);
            holder.delete_but = (Button) itemView.findViewById(R.id.delete_but);
            holder.swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipeLayout);
            itemView.setTag(holder);
        } else {
            holder = (AppInfoHolder)itemView.getTag();
        }

        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewWithTag("Bottom4"));

        holder.swipeLayout.findViewById(R.id.edit_but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showEditDialog(dataModel.name, dataModel.id, false);
            }
        });
        holder.swipeLayout.findViewById(R.id.delete_but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showDeleteDialg(dataModel.id);
            }
        });

        holder.notion_but.setText(dataModel.name);
        return itemView;
    }

    static class AppInfoHolder
    {
        Button notion_but;
        Button edit_but;
        Button delete_but;
        SwipeLayout swipeLayout;
    }
}
