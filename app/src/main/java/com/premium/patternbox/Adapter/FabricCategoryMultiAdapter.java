package com.premium.patternbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.premium.patternbox.R;
import com.premium.patternbox.app.FabricCategoryInfo;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class FabricCategoryMultiAdapter extends ArrayAdapter<FabricCategoryInfo> {
    private final Context mContext;
    private ArrayList<FabricCategoryInfo> dataSet;
    private int[] selectedPosition;

    public FabricCategoryMultiAdapter(ArrayList<FabricCategoryInfo> data, Context context, int[] poslist) {
        super(context, R.layout.grid_adapter_category1, data);
        this.dataSet = data;
        this.mContext=context;
        this.selectedPosition = poslist;
    }
    @Override
    public FabricCategoryInfo getItem(int position){
        return dataSet.get(position);
    }

    @Override
    public int getCount() {
        return dataSet.size() + 1;
    }

    public void setSelectedPosition(int[] position) {
        selectedPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater;
        AppInfoHolder holder = null;

        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.grid_adapter_category1, parent, false);

            holder = new AppInfoHolder();

            holder.background_layout = (RelativeLayout) itemView.findViewById(R.id.background_layout);
            holder.icon_img = (ImageView) itemView.findViewById(R.id.icon_img);
            holder.check_img = (ImageView) itemView.findViewById(R.id.check_img);
            holder.name_txt =(TextView)itemView.findViewById(R.id.name_txt);
            holder.title_txt = (TextView) itemView.findViewById(R.id.title_txt);
            itemView.setTag(holder);
        } else {
            holder = (AppInfoHolder)itemView.getTag();
        }
        holder.icon_img.setVisibility(View.GONE);
        holder.name_txt.setVisibility(View.GONE);

        if (selectedPosition[position] == 1) {
            holder.background_layout.setBackgroundResource(R.drawable.round_select);
            holder.check_img.setVisibility(View.VISIBLE);
        } else {
            holder.background_layout.setBackgroundResource(R.drawable.round_unselect);
            holder.check_img.setVisibility(View.GONE);
        }
        if (position == dataSet.size()) {
            holder.name_txt.setText("");
            holder.icon_img.setVisibility(View.VISIBLE);
            holder.icon_img.setImageResource(R.drawable.c_plus);
            holder.background_layout.setBackgroundResource(R.drawable.round_unselect);
            holder.check_img.setVisibility(View.GONE);
            holder.title_txt.setVisibility(View.GONE);
        }
        else {
            FabricCategoryInfo dataModel = getItem(position);
            holder.name_txt.setText(dataModel.name.toUpperCase());

            holder.title_txt.setVisibility(View.VISIBLE);
            holder.icon_img.setVisibility(View.GONE);
            holder.title_txt.setText(dataModel.name.toUpperCase());
        }
        return itemView;
    }

    static class AppInfoHolder
    {
        RelativeLayout background_layout;
        ImageView icon_img;
        ImageView check_img;
        TextView name_txt;
        TextView title_txt;
    }
}
