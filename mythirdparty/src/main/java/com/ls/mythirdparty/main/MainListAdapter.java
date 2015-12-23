package com.ls.mythirdparty.main;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ls.mythirdparty.OnItemClickListener;
import com.ls.mythirdparty.R;
import com.ls.util.PaletteTransformation;
import com.ls.util.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by user on 15-12-21.
 */
public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ImagesViewHolder> {

    private Context mContext;
    private OnItemClickListener onItemClickListener;

    private int mScreenWidth;

    private int mDefaultTextColor;
    private int mDefaultBackgroundColor;
    String[] mUrls;

    public MainListAdapter() {

    }


    public MainListAdapter(Context context) {
        this.mContext = context;
    }

    public void setImages(String[] imgs) {
        mUrls = imgs;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View rowView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_item_image, viewGroup, false);

        //set the mContext
        this.mContext = viewGroup.getContext();

        //get the colors
        mDefaultTextColor = mContext.getResources().getColor(R.color.text_without_palette);
        mDefaultBackgroundColor = mContext.getResources().getColor(R.color.image_without_palette);

        //get the screenWidth :D optimize everything :D
        mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        return new ImagesViewHolder(rowView, onItemClickListener);
    }

    /**
     // 有活力的颜色
     Palette.Swatch vibrant = palette.getVibrantSwatch();
     // 有活力的暗色
     Palette.Swatch darkVibrant = palette.getDarkVibrantSwatch();
     // 有活力的亮色
     Palette.Swatch lightVibrant = palette.getLightVibrantSwatch();
     // 柔和的颜色
     Palette.Swatch muted = palette.getMutedSwatch();
     // 柔和的暗色
     Palette.Swatch darkMuted = palette.getDarkMutedSwatch();
     // 柔和的亮色
     Palette.Swatch lightMuted = palette.getLightMutedSwatch();

     ============================================================================
         Vibrant and Dark Vibrant are the ones that developers will use mostly though
     */

    @Override
    public void onBindViewHolder(final ImagesViewHolder holder, int position) {

        holder.imageTextContainer.setBackgroundColor(mDefaultBackgroundColor);

        DisplayMetrics displaymetrics = mContext.getResources().getDisplayMetrics();
        int width = displaymetrics.widthPixels;
        holder.imageView.setMinimumHeight(width);
        String url = mUrls[position];
        //取消正在下载的图片
        Picasso.with(mContext).cancelRequest(holder.imageView);
        Picasso.with(mContext)
                .load(url)
                .resize(width, width)
                .centerCrop()
                .transform(PaletteTransformation.instance())
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable)holder.imageView.getDrawable()).getBitmap();
                        if (bitmap != null && !bitmap.isRecycled()) {
                            Palette palette = PaletteTransformation.getPalette(bitmap);

                            Palette.Swatch s = palette.getVibrantSwatch();
                            if (s != null) {
                                int endColor = s.getRgb();
                                Utils.animateViewBackGroundColor(holder.imageTextContainer, mDefaultBackgroundColor, endColor);
                            }
                        }

                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mUrls.length;
    }


    class ImagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected final FrameLayout imageTextContainer;
        protected final ImageView imageView;
        protected final TextView imageAuthor;
        protected final TextView imageDate;
        private final OnItemClickListener onItemClickListener;

        public ImagesViewHolder(View itemView, OnItemClickListener onItemClickListener) {

            super(itemView);
            this.onItemClickListener = onItemClickListener;

            imageTextContainer = (FrameLayout) itemView.findViewById(R.id.item_image_text_container);
            imageView = (ImageView) itemView.findViewById(R.id.item_image_img);
            imageAuthor = (TextView) itemView.findViewById(R.id.item_image_author);
            imageDate = (TextView) itemView.findViewById(R.id.item_image_date);

            imageView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onClick(v, getPosition());
        }
    }
}
