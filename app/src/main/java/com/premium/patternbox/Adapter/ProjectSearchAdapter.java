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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.swipe.SwipeLayout;
import com.premium.patternbox.R;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.FabricInfo;
import com.premium.patternbox.app.PatternInfo;
import com.premium.patternbox.app.ProjectInfo;
import com.premium.patternbox.utils.CircleTransform;

import org.lucasr.twowayview.TwoWayView;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class ProjectSearchAdapter extends ArrayAdapter<ProjectInfo> implements Filterable {
    private final Context mContext;
    private ArrayList<ProjectInfo> dataSet;
    private ArrayList<ProjectInfo> filterDataSet;
    private final int mUserID;

    private DataFilter dataFilter;

    public ProjectSearchAdapter(ArrayList<ProjectInfo> data, Context context, int userId) {
        super(context, R.layout.list_adapter_project, data);
        this.dataSet = data;
        this.filterDataSet = data;
        this.mContext=context;
        this.mUserID = userId;
    }
    @Override
    public ProjectInfo getItem(int position){
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
        ProjectInfo dataModel = getItem(position);
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.list_adapter_project, parent, false);

            holder = new AppInfoHolder();

            holder.project_img = (ImageView) itemView.findViewById(R.id.project_img);
            holder.pattern_img = (ImageView) itemView.findViewById(R.id.pattern_img);
            holder.lbName = (TextView)itemView.findViewById(R.id.lbName);
            holder.lbFor =(TextView)itemView.findViewById(R.id.lbFor);
            holder.lbNote =(TextView)itemView.findViewById(R.id.lbNote);
            holder.gridView = (TwoWayView)itemView.findViewById(R.id.gridView);
            holder.fabric_layout = (LinearLayout)itemView.findViewById(R.id.fabric_layout);

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

        String url =  AppConfig.downloadUrl(this.mUserID, dataModel.image);
        int placeHolder = R.drawable.icon_camera;

        Glide.with(mContext).load(url)
                .crossFade()
                .thumbnail(0.5f)
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.project_img);

        holder.lbName.setText("Name:   " + dataModel.name);
        holder.lbFor.setText("For:   " + dataModel.client);
        holder.lbNote.setText("Note:   "+ dataModel.notes);

        PatternInfo patternInfo = AppConfig.getPatternbyID(dataModel.pattern);
        if (patternInfo != null) {
            String url1 = AppConfig.downloadUrl(this.mUserID, patternInfo.frontImage);
            Glide.with(mContext).load(url1)
                    .crossFade()
                    .placeholder(placeHolder)
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.pattern_img);
        }

        holder.fabric_layout.removeAllViews();
        if (!dataModel.fabrics.equals("")) {
            ArrayList<FabricInfo> newList = new ArrayList<FabricInfo>();
            String[] ids = dataModel.fabrics.split(",");
            for (int i = 0; i < ids.length; i++) {
                FabricInfo fabricInfo = AppConfig.getFabricbyID(Integer.parseInt(ids[i]));
                if (fabricInfo == null) continue;
                newList.add(fabricInfo);

                ImageView fabric_img = new ImageView(mContext);
                LinearLayout.LayoutParams imgvwDimens = new LinearLayout.LayoutParams(100, 100);
                fabric_img.setLayoutParams(imgvwDimens);
                fabric_img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                imgvwDimens.setMargins(0, 10, 20, 0);

                String ur2 =  AppConfig.downloadUrl(this.mUserID, fabricInfo.imageUrl);
                Glide.with(mContext).load(ur2)
                        .crossFade()
                        .thumbnail(0.5f)
                        .placeholder(placeHolder)
                        .bitmapTransform(new CircleTransform(mContext))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(fabric_img);

                holder.fabric_layout.addView(fabric_img, imgvwDimens);
            }
            //FabricRoundAdapter adapter = new FabricRoundAdapter(newList, mContext, this.mUserID);
            //holder.gridView.setAdapter(adapter);
        }
        holder.gridView.setVisibility(View.GONE);
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
        ImageView project_img;
        ImageView pattern_img;
        TwoWayView gridView;
        TextView lbName;
        TextView lbFor;
        TextView lbNote;
        LinearLayout fabric_layout;

        Button delete_but;
        SwipeLayout swipeLayout;
    }

    private class DataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint!=null && constraint.length()>0) {
                ArrayList<ProjectInfo> tempList = new ArrayList<ProjectInfo>();

                // search content in friend list
                for (ProjectInfo pattern : dataSet) {
                    if (pattern.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
            filterDataSet = (ArrayList<ProjectInfo>) results.values;
            notifyDataSetChanged();
        }
    }
}
