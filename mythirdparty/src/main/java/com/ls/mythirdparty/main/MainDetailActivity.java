package com.ls.mythirdparty.main;

import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.ls.mythirdparty.R;
import com.ls.ui.BaseActivity;
import com.ls.util.PaletteTransformation;
import com.squareup.picasso.Picasso;

/**
 * Created by liu_shuai on 15/12/23.
 */
public class MainDetailActivity extends BaseActivity {
    private int mPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            mPosition = args.getInt("position");
        }

        setContentView(R.layout.activity_detail);
        ImageView view = (ImageView) findViewById(R.id.img);
        if (Build.VERSION.SDK_INT >= 21) { //>=L
            view.setTransitionName("cover");
        }

        Picasso.with(this)
                .load(Data.URLS[mPosition])
                .transform(PaletteTransformation.instance())
                .into(view);

    }
}
