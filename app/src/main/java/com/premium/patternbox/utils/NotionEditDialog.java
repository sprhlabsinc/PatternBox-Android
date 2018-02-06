package com.premium.patternbox.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.NotionSearchActivity;
import com.premium.patternbox.R;
import com.premium.patternbox.app.NotionInfo;

/**
 * Created by Kris on 3/27/2017.
 */

public class NotionEditDialog extends Dialog {

    public Button confirm_but;

    public NotionEditDialog(Context context, final NotionInfo info, final NotionSearchActivity activity, final String category, final boolean status) {
        super(context);
        setContentView(R.layout.notion_edit_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title_txt = (TextView) findViewById(R.id.title_txt);
        final EditText type_txt = (EditText) findViewById(R.id.type_txt);
        final EditText color_txt = (EditText) findViewById(R.id.color_txt);
        final EditText size_txt = (EditText) findViewById(R.id.size_txt);

        Button done_but = (Button) findViewById(R.id.done_but);
        confirm_but = (Button) findViewById(R.id.confirm_but);

        if (!status) {
            title_txt.setText("Edit Notion");
            confirm_but.setText("Edit");
            type_txt.setText(info.type);
            color_txt.setText(info.color);
            size_txt.setText(info.size);
        }
        else {
            title_txt.setText("New Notion");
            confirm_but.setText("Add");
        }

        done_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirm_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotionInfo newItem = new NotionInfo();
                newItem.category = category;
                newItem.type = type_txt.getText().toString();
                newItem.color = color_txt.getText().toString();
                newItem.size = size_txt.getText().toString();
                if (!status)
                    newItem.id = info.id;

                activity.editNotionEvent(newItem, status);
                dismiss();
            }
        });
    }

    public NotionEditDialog(Context context, Bitmap bitmap) {
        super(context);
        setContentView(R.layout.image_dialog);

        ImageView imgPattern = (ImageView) findViewById(R.id.imgPattern);

        imgPattern.setImageBitmap(bitmap);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
