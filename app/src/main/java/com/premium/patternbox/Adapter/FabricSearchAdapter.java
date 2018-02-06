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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.swipe.SwipeLayout;
import com.premium.patternbox.R;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.FabricInfo;
import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class FabricSearchAdapter extends ArrayAdapter<FabricInfo> implements Filterable {
    private final Context mContext;
    private ArrayList<FabricInfo> dataSet;
    private ArrayList<FabricInfo> filterDataSet;
    private final int mUserID;

    private DataFilter dataFilter;

    public FabricSearchAdapter(ArrayList<FabricInfo> data, Context context, int userId) {
        super(context, R.layout.list_adapter_search, data);
        this.dataSet = data;
        this.filterDataSet = data;
        this.mContext=context;
        this.mUserID = userId;
    }
    @Override
    public FabricInfo getItem(int position){
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
        final FabricInfo dataModel = getItem(position);
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.list_adapter_search, parent, false);

            holder = new AppInfoHolder();

            holder.imageView = (ImageView) itemView.findViewById(R.id.imgPattern);
            holder.imgBookMark = (ImageView) itemView.findViewById(R.id.imgBookMark);
            holder.patternName = (TextView)itemView.findViewById(R.id.lbName);
            holder.patternID =(TextView)itemView.findViewById(R.id.lbId);
            holder.categories =(TextView)itemView.findViewById(R.id.lbCategories);

            holder.delete_but = (Button) itemView.findViewById(R.id.delete_but);
            holder.swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipeLayout);
            itemView.setTag(holder);
        } else {
            holder = (AppInfoHolder)itemView.getTag();
        }

        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewWithTag("Bottom4"));

        holder.swipeLayout.findViewById(R.id.delete_but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
            }
        });

        String url =  AppConfig.downloadUrl(this.mUserID,dataModel.imageUrl);
        int placeHolder = R.drawable.icon_camera;

        if (dataModel.isBookmark > 0) {
            holder.imgBookMark.setVisibility(View.VISIBLE);
        }
        else {
            holder.imgBookMark.setVisibility(View.GONE);
        }
        Glide.with(mContext).load(url)
                .crossFade()
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

        holder.patternName.setText("NAME:   " + dataModel.name);
        holder.patternID.setText("TYPE:   " + String.valueOf(dataModel.type));
        holder.categories.setText("CATEGORIES:   "+dataModel.getCategories());
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
        ImageView imageView;
        ImageView imgBookMark;
        TextView patternName;
        TextView patternID;
        TextView categories;

        Button delete_but;
        SwipeLayout swipeLayout;
    }

    private class DataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint!=null && constraint.length()>0) {
                ArrayList<FabricInfo> tempList = new ArrayList<FabricInfo>();

                // search content in friend list
                for (FabricInfo pattern : dataSet) {
                    if (pattern.name.toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            pattern.type.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            filterDataSet = (ArrayList<FabricInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}
