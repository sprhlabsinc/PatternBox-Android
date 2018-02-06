package com.premium.patternbox.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.R;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.PatternCategoryInfo;
import com.premium.patternbox.app.PatternCategoryInfo;
import com.premium.patternbox.app.PatternInfo;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class PatternCategoryMultiAdapter extends ArrayAdapter<PatternCategoryInfo> {
    private final Context mContext;
    private ArrayList<PatternCategoryInfo> dataSet;
    private int[] selectedPosition;

    public PatternCategoryMultiAdapter(ArrayList<PatternCategoryInfo> data, Context context, int[] poslist) {
        super(context, R.layout.grid_adapter_category, data);
        this.dataSet = data;
        this.mContext=context;
        this.selectedPosition = poslist;
    }
    @Override
    public PatternCategoryInfo getItem(int position){
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
            itemView = (View)inflater.inflate(R.layout.grid_adapter_category, parent, false);

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

        if (selectedPosition[position] == 1) {
            holder.background_layout.setBackgroundResource(R.drawable.round_select);
            holder.check_img.setVisibility(View.VISIBLE);
        } else {
            holder.background_layout.setBackgroundResource(R.drawable.round_unselect);
            holder.check_img.setVisibility(View.GONE);
        }
        if (position == dataSet.size()) {
            holder.name_txt.setText("");
            holder.icon_img.setImageResource(R.drawable.c_plus);
            holder.background_layout.setBackgroundResource(R.drawable.round_unselect);
            holder.check_img.setVisibility(View.GONE);
            holder.title_txt.setVisibility(View.GONE);
        }
        else {
            PatternCategoryInfo dataModel = getItem(position);
            holder.name_txt.setText(dataModel.name.toUpperCase());
            if (dataModel.isDefault) {
                holder.icon_img.setImageResource(dataModel.icon);
                holder.icon_img.setVisibility(View.VISIBLE);
                holder.title_txt.setVisibility(View.GONE);
            }
            else {
                holder.title_txt.setVisibility(View.VISIBLE);
                holder.icon_img.setVisibility(View.GONE);
                holder.title_txt.setText(dataModel.name.substring(0, 1).toUpperCase());
            }
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
