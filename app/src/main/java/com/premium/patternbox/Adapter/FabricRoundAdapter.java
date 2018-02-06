package com.premium.patternbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.R;
import com.premium.patternbox.app.AppConfig;
import com.premium.patternbox.app.FabricInfo;
import com.premium.patternbox.utils.CircleTransform;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class FabricRoundAdapter extends ArrayAdapter<FabricInfo> {
    private final Context mContext;
    private ArrayList<FabricInfo> dataSet;
    private final int mUserID;

    public FabricRoundAdapter(ArrayList<FabricInfo> data, Context context, int mUserID) {
        super(context, R.layout.list_adapter_fabric_round, data);
        this.dataSet = data;
        this.mContext=context;
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

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater;
        AppInfoHolder holder = null;
        FabricInfo dataModel = getItem(position);

        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.list_adapter_fabric_round, parent, false);

            holder = new AppInfoHolder();

            holder.imgPattern = (ImageView) itemView.findViewById(R.id.imgPattern);
            itemView.setTag(holder);
        } else {
            holder = (AppInfoHolder)itemView.getTag();
        }

        String url =  AppConfig.downloadUrl(this.mUserID,dataModel.imageUrl);
        int placeHolder = R.drawable.icon_camera;
        Glide.with(mContext).load(url)
                .crossFade()
                .thumbnail(0.5f)
                .placeholder(placeHolder)
                .bitmapTransform(new CircleTransform(mContext))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgPattern);

        return itemView;
    }

    static class AppInfoHolder
    {
        ImageView imgPattern;
    }
}
