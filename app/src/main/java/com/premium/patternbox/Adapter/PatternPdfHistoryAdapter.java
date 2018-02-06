package com.premium.patternbox.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.premium.patternbox.R;
import com.premium.patternbox.app.AppConfig;
import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class PatternPdfHistoryAdapter extends ArrayAdapter<String> implements Filterable {
    private final Context mContext;
    private ArrayList<String> dataSet;
    private ArrayList<String> filterDataSet;
    private final int mUserID;

    private DataFilter dataFilter;

    public PatternPdfHistoryAdapter(ArrayList<String> data, Context context, int userId) {
        super(context, R.layout.list_adapter_history, data);
        this.dataSet = data;
        this.filterDataSet = data;
        this.mContext=context;
        this.mUserID = userId;
    }
    @Override
    public String getItem(int position){
        return filterDataSet.get(position);
    }

    @Override
    public int getCount() {
        return filterDataSet.size();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater;
        AppInfoHolder holder = null;
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.list_adapter_history, parent, false);

            holder = new AppInfoHolder();

            holder.title_txt = (TextView) itemView.findViewById(R.id.title_txt);
            itemView.setTag(holder);
        } else {
            holder = (AppInfoHolder)itemView.getTag();
        }

        holder.title_txt.setText(getItem(position));

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
        TextView title_txt;
    }

    private class DataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint!=null && constraint.length()>0) {
                ArrayList<String> tempList = new ArrayList<String>();

                // search content in friend list
                for (String pattern : dataSet) {
                    if (pattern.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            filterDataSet = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }
}
