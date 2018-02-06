package com.premium.patternbox.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.premium.patternbox.R;
import com.premium.patternbox.app.AppConfig;

import java.io.File;

/**
 * Created by Kris on 3/27/2017.
 */

public class ImageDialog extends Dialog {

    public ImageDialog(Context context, String url) {
        super(context);
        setContentView(R.layout.image_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imgPattern = (ImageView) findViewById(R.id.imgPattern);

        int placeHolder = R.drawable.icon_camera;
        Glide.with(getContext()).load(url)
                .crossFade()
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgPattern);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public ImageDialog(Context context, File file) {
        super(context);
        setContentView(R.layout.image_dialog);

        ImageView imgPattern = (ImageView) findViewById(R.id.imgPattern);

        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        imgPattern.setImageBitmap(myBitmap);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
