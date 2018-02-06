package com.premium.patternbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.R;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.FabricInfo;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class FabricSingleAdapter extends ArrayAdapter<FabricInfo> {
    private final Context mContext;
    private ArrayList<FabricInfo> dataSet;
    private int selectedPosition;
    private final int mUserID;

    public FabricSingleAdapter(ArrayList<FabricInfo> data, Context context, int poslist, int mUserID) {
        super(context, R.layout.list_adapter_fabric, data);
        this.dataSet = data;
        this.mContext=context;
        this.selectedPosition = poslist;
        this.mUserID = mUserID;
    }
    @Override
    public FabricInfo getItem(int position){
        return dataSet.get(position);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater;
        AppInfoHolder holder = null;
        FabricInfo dataModel = getItem(position);

        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.list_adapter_fabric, parent, false);

            holder = new AppInfoHolder();

            holder.imgPattern = (ImageView) itemView.findViewById(R.id.imgPattern);
            holder.check_img = (ImageView) itemView.findViewById(R.id.check_img);
            holder.imgBookMark =(ImageView)itemView.findViewById(R.id.imgBookMark);
            holder.lbName = (TextView) itemView.findViewById(R.id.lbName);
            itemView.setTag(holder);
        } else {
            holder = (AppInfoHolder)itemView.getTag();
        }

        if (selectedPosition == position) {
            holder.imgPattern.setBackgroundResource(R.drawable.bg_thumbnail);
            holder.check_img.setVisibility(View.GONE);
        } else {
            holder.imgPattern.setBackground(null);
            holder.check_img.setVisibility(View.GONE);
        }

        String url =  AppConfig.downloadUrl(this.mUserID,dataModel.imageUrl);
        int placeHolder = R.drawable.icon_camera;
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
        holder.lbName.setText(dataModel.name);

        return itemView;
    }

    static class AppInfoHolder
    {
        ImageView imgPattern;
        ImageView imgBookMark;
        ImageView check_img;
        TextView lbName;
    }
}
