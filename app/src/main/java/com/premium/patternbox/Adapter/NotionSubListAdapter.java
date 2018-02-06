package com.premium.patternbox.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.premium.patternbox.R;
import com.premium.patternbox.app.NotionInfo;

import java.util.ArrayList;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class NotionSubListAdapter extends  ArrayAdapter<NotionInfo> {
    private final Context mContext;
    private ArrayList<NotionInfo> dataSet;

    public NotionSubListAdapter(ArrayList<NotionInfo> data, Context context) {
        super(context, R.layout.list_adapter_notion_view, data);
        this.dataSet = data;
        this.mContext=context;
    }
    @Override
    public NotionInfo getItem(int position){
        return dataSet.get(position);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater;
        AppInfoHolder holder = null;
        final NotionInfo dataModel = getItem(position);
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.list_adapter_notion_view, parent, false);

            holder = new AppInfoHolder();
            holder.notion_txt = (TextView) itemView.findViewById(R.id.notion_txt);
            holder.count_txt = (TextView) itemView.findViewById(R.id.count_txt);
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

        holder.notion_txt.setText(String.format("%s,%s,%s", dataModel.type, dataModel.color, dataModel.size));
        holder.count_txt.setText(String.valueOf(dataModel.count));
        return itemView;
    }

    static class AppInfoHolder
    {
        TextView notion_txt;
        TextView count_txt;
        Button delete_but;
        SwipeLayout swipeLayout;
    }
}
