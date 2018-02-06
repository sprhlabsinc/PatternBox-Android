package com.premium.patternbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.R;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternInfo;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class PatternAdapter extends ArrayAdapter<PatternInfo> implements Filterable {
    private final Context mContext;
    private ArrayList<PatternInfo> dataSet;
    private ArrayList<PatternInfo> filterDataSet;
    private final int mUserID;

    private DataFilter dataFilter;
    private int mSelID;

    public PatternAdapter(ArrayList<PatternInfo> data, Context context, int userId, int selID) {
        super(context, R.layout.list_adapter_pattern, data);
        this.dataSet = data;
        this.filterDataSet = data;
        this.mContext=context;
        this.mUserID = userId;
        this.mSelID = selID;
    }
    @Override
    public PatternInfo getItem(int position){
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
        PatternInfo dataModel = getItem(position);
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.list_adapter_pattern, parent, false);

            holder = new AppInfoHolder();

            holder.imgPattern = (ImageView) itemView.findViewById(R.id.imgPattern);
            holder.imgBookMark = (ImageView)itemView.findViewById(R.id.imgBookMark);
            holder.lbName =(TextView)itemView.findViewById(R.id.lbName);
            holder.lbId =(TextView)itemView.findViewById(R.id.lbId);
            itemView.setTag(holder);
        } else {
            holder = (AppInfoHolder)itemView.getTag();
        }
        if (dataModel.id == mSelID) {
            holder.imgPattern.setBackgroundResource(R.drawable.bg_thumbnail);
        }
        else {
            holder.imgPattern.setBackground(null);
        }

        String url =  AppConfig.downloadUrl(this.mUserID,dataModel.frontImage);
        int placeHolder;
        if(dataModel.isPdf == 0){
            placeHolder = R.drawable.icon_camera;
        }else{
            placeHolder = R.drawable.icon_pdfdoc;
        }
        Glide.with(mContext).load(url)
                .crossFade()
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgPattern);
        if (dataModel.isBookmark > 0) {
            holder.imgBookMark.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgBookMark.setVisibility(View.GONE);
        }

        holder.lbName.setText("NAME:   " + dataModel.name);
        holder.lbId.setText("ID:   " + String.valueOf(dataModel.key));
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
        ImageView imgPattern;
        ImageView imgBookMark;
        TextView lbName;
        TextView lbId;
    }

    private class DataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint!=null && constraint.length()>0) {
                ArrayList<PatternInfo> tempList = new ArrayList<PatternInfo>();

                // search content in friend list
                for (PatternInfo pattern : dataSet) {
                    if (pattern.name.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            pattern.key.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            filterDataSet = (ArrayList<PatternInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}
