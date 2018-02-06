package com.premium.patternbox.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.premium.patternbox.R;
import com.premium.patternbox.app.NotionEditInfo;
import com.premium.patternbox.app.NotionInfo;

import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Kristaps Kuzmins on 3/6/2017.
 */

public class NotionListAdapter extends  ArrayAdapter<NotionEditInfo> {
    private final Context mContext;
    private ArrayList<NotionEditInfo> dataSet;
    private boolean islock;

    public NotionListAdapter(ArrayList<NotionEditInfo> data, Context context, boolean islock) {
        super(context, R.layout.list_adapter_notion_add, data);
        this.dataSet = data;
        this.mContext=context;
        this.islock = islock;
    }
    @Override
    public NotionEditInfo getItem(int position){
        return dataSet.get(position);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View itemView = convertView;
        LayoutInflater inflater;
        AppInfoHolder holder = null;
        final NotionEditInfo dataModel = getItem(position);
        if(itemView == null) {
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = (View)inflater.inflate(R.layout.list_adapter_notion_add, parent, false);

            holder = new AppInfoHolder();
            holder.category_layout = (RelativeLayout) itemView.findViewById(R.id.category_layout);
            holder.arrow_img = (ImageView) itemView.findViewById(R.id.arrow_img);
            holder.category_txt = (TextView) itemView.findViewById(R.id.category_txt);
            holder.sum_txt = (TextView) itemView.findViewById(R.id.sum_txt);
            holder.add_but = (Button) itemView.findViewById(R.id.add_but);
            holder.notion_layout = (LinearLayout) itemView.findViewById(R.id.notion_layout);

            holder.cell_layout = (RelativeLayout) itemView.findViewById(R.id.cell_layout);
            holder.notion_txt = (TextView) itemView.findViewById(R.id.notion_txt);
            holder.count_txt = (TextView) itemView.findViewById(R.id.count_txt);
            holder.delete_but = (Button) itemView.findViewById(R.id.delete_but);

            itemView.setTag(holder);
        } else {
            holder = (AppInfoHolder)itemView.getTag();
        }

        holder.add_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!islock)
                    ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
            }
        });
        final AppInfoHolder finalHolder = holder;
        holder.category_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalHolder.notion_layout.getVisibility() == View.VISIBLE) {
                    finalHolder.notion_layout.setVisibility(View.GONE);
                    finalHolder.arrow_img.setImageResource(R.drawable.arrow_bottom);
                }
                else {
                    finalHolder.notion_layout.setVisibility(View.VISIBLE);
                    finalHolder.arrow_img.setImageResource(R.drawable.arrow_top);
                }
            }
        });

        holder.notion_layout.setVisibility(View.GONE);
        holder.category_txt.setText(dataModel.name);
        int sum = 0;
        for (int i = 0; i < dataModel.notionInfos.size(); i ++) {
            sum += dataModel.notionInfos.get(i).count;
        }
        holder.sum_txt.setText(String.format("(%d)", sum));

        ViewGroup.LayoutParams cellParams = holder.cell_layout.getLayoutParams();
        ViewGroup.LayoutParams notionParams = holder.notion_txt.getLayoutParams();
        ViewGroup.LayoutParams countParams = holder.count_txt.getLayoutParams();
        ViewGroup.LayoutParams deleteParams = holder.delete_but.getLayoutParams();

        holder.notion_layout.removeAllViews();
        for (int i = 0; i < dataModel.notionInfos.size(); i ++) {
            final NotionInfo notionInfo = dataModel.notionInfos.get(i);
            final RelativeLayout cell_layout = new RelativeLayout(mContext);
            TextView notion_txt = new TextView(mContext);
            TextView count_txt = new TextView(mContext);
            Button delete_but = new Button(mContext);

            notion_txt.setTextSize(16);
            notion_txt.setTextColor(BLACK);
            count_txt.setTextSize(16);
            count_txt.setGravity(Gravity.CENTER);
            count_txt.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            count_txt.setTextColor(BLUE);
            delete_but.setBackgroundResource(R.drawable.delete_icon);
            notion_txt.setText(String.format("%s, %s, %s", notionInfo.type, notionInfo.color, notionInfo.size));
            count_txt.setText(String.format("%d", notionInfo.count));

            cell_layout.setLayoutParams(cellParams);
            notion_txt.setLayoutParams(notionParams);
            count_txt.setLayoutParams(countParams);
            delete_but.setLayoutParams(deleteParams);

            cell_layout.addView(notion_txt);
            cell_layout.addView(count_txt);
            if (!islock)
                cell_layout.addView(delete_but);

            final AppInfoHolder finalHolder1 = holder;
            delete_but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataModel.notionInfos.remove(notionInfo);
                    finalHolder1.notion_layout.removeView(cell_layout);

                    int sum = 0;
                    for (int i = 0; i < dataModel.notionInfos.size(); i ++) {
                        sum += dataModel.notionInfos.get(i).count;
                    }
                    finalHolder1.sum_txt.setText(String.format("(%d)", sum));
                }
            });

            holder.notion_layout.addView(cell_layout);
        }
        holder.cell_layout.setVisibility(View.GONE);
        /*
        adapter = new NotionSubListAdapter(dataModel.notionInfos, mContext);
        holder.listView.setAdapter(adapter);

        final AppInfoHolder finalHolder = holder;

        holder.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long viewId = view.getId();
                NotionInfo notionInfo = (NotionInfo) (finalHolder.listView.getItemAtPosition(position));

                if (viewId == R.id.delete_but) {
                    for (int i = 0; i < dataModel.notionInfos.size(); i++) {
                        NotionInfo temp = dataModel.notionInfos.get(i);
                        if (temp.id == notionInfo.id) {
                            dataModel.notionInfos.remove(notionInfo);
                            break;
                        }
                    }
                    adapter = new NotionSubListAdapter(dataModel.notionInfos, mContext);
                    finalHolder.listView.setAdapter(adapter);
                }
            }
        });
        */
        return itemView;
    }

    static class AppInfoHolder
    {
        RelativeLayout category_layout;
        ImageView arrow_img;
        TextView category_txt;
        TextView sum_txt;
        Button add_but;

        LinearLayout notion_layout;
        RelativeLayout cell_layout;
        TextView notion_txt;
        TextView count_txt;
        Button delete_but;
    }
}
