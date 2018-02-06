package com.premium.patternbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.swipe.SwipeLayout;
import com.premium.patternbox.NotionSearchActivity;
import com.premium.patternbox.R;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.NotionInfo;
import com.premium.patternbox.app.PatternInfo;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class NotionSearchAdapter extends ArrayAdapter<NotionInfo> implements Filterable {
    private final Context mContext;
    private ArrayList<NotionInfo> dataSet;
    private ArrayList<NotionInfo> filterDataSet;
    private NotionSearchActivity mActivity;

    private DataFilter dataFilter;

    public NotionSearchAdapter(ArrayList<NotionInfo> data, Context context, NotionSearchActivity activity) {
        super(context, R.layout.list_adapter_notion_search, data);
        this.dataSet = data;
        this.filterDataSet = data;
        this.mContext=context;
        this.mActivity = activity;
    }
    @Override
    public NotionInfo getItem(int position){
        return filterDataSet.get(position);
    }

    @Override
    public int getCount() {
        return filterDataSet.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater;
        AppInfoHolder holder = null;
        final NotionInfo dataModel = getItem(position);
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.list_adapter_notion_search, parent, false);

            holder = new AppInfoHolder();

            holder.type_txt = (TextView) itemView.findViewById(R.id.type_txt);
            holder.color_txt = (TextView) itemView.findViewById(R.id.color_txt);
            holder.size_txt = (TextView)itemView.findViewById(R.id.size_txt);
            holder.count_txt = (TextView)itemView.findViewById(R.id.count_txt);
            holder.check_img =(ImageView)itemView.findViewById(R.id.check_img);

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
                mActivity.showEditDialog(dataModel, false);
            }
        });
        holder.swipeLayout.findViewById(R.id.delete_but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.showDeleteDialg(dataModel);
            }
        });

        holder.type_txt.setText(dataModel.type);
        holder.color_txt.setText(dataModel.color);
        holder.size_txt.setText(dataModel.size);
        holder.count_txt.setText(String.valueOf(dataModel.count));

        if (dataModel.count == 0) {
            holder.check_img.setVisibility(View.GONE);
        }
        else {
            holder.check_img.setVisibility(View.VISIBLE);
        }
        return itemView;
    }

    @Override
    public Filter getFilter() {

        if (dataFilter == null) {
            dataFilter = new DataFilter();
        }
        return dataFilter;
    }


    static class AppInfoHolder
    {
        TextView type_txt;
        TextView color_txt;
        TextView size_txt;
        TextView count_txt;
        ImageView check_img;

        Button edit_but;
        Button delete_but;
        SwipeLayout swipeLayout;
    }

    private class DataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint!=null && constraint.length()>0) {
                ArrayList<NotionInfo> tempList = new ArrayList<NotionInfo>();

                // search content in friend list
                for (NotionInfo pattern : dataSet) {
                    if (pattern.type.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            pattern.color.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(pattern);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = dataSet.size();
                filterResults.values = dataSet;
            }

            return filterResults;

        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterDataSet = (ArrayList<NotionInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}
