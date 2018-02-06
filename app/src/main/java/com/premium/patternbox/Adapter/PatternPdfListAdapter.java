package com.premium.patternbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.premium.patternbox.R;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class PatternPdfListAdapter extends  ArrayAdapter<String> {
    private final Context mContext;
    private ArrayList<String> dataSet;
    private int selectedPosition = -1;

    public PatternPdfListAdapter(ArrayList<String> data, Context context) {
        super(context, R.layout.pdf_item_adapter, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public String getItem(int position){
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
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.pdf_item_adapter, parent, false);

            holder = new AppInfoHolder();
            holder.title_txt = (TextView) itemView.findViewById(R.id.title_txt);
            itemView.setTag(holder);
        } else {
            holder = (AppInfoHolder)itemView.getTag();
        }
        String url = getItem(position);
        String name = url.substring(url.lastIndexOf('/') + 1);
        holder.title_txt.setText(name);
        if (name.equals("") || name.isEmpty()) {
            holder.title_txt.setText(url);
        }
        return itemView;
    }

    static class AppInfoHolder
    {
        TextView title_txt;
    }
}
