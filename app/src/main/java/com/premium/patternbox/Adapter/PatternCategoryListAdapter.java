package com.premium.patternbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.premium.patternbox.PatternCategoryAddActivity;
import com.premium.patternbox.R;
import com.premium.patternbox.app.PatternCategoryInfo;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class PatternCategoryListAdapter extends  ArrayAdapter<PatternCategoryInfo> {
    private final Context mContext;
    private ArrayList<PatternCategoryInfo> dataSet;
    private int selectedPosition = -1;
    private PatternCategoryAddActivity mActivity;

    public PatternCategoryListAdapter(ArrayList<PatternCategoryInfo> data, Context context, PatternCategoryAddActivity activity) {
        super(context, R.layout.item_adapter, data);
        this.dataSet = data;
        this.mContext=context;
        this.mActivity = activity;
    }
    @Override
    public PatternCategoryInfo getItem(int position){
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
        final PatternCategoryInfo dataModel = getItem(position);
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.item_adapter, parent, false);

            holder = new AppInfoHolder();
            holder.title_txt = (TextView) itemView.findViewById(R.id.title_txt);
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

        holder.title_txt.setText(dataModel.name);
        return itemView;
    }

    static class AppInfoHolder
    {
        TextView title_txt;
        Button edit_but;
        Button delete_but;
        SwipeLayout swipeLayout;
    }
}
