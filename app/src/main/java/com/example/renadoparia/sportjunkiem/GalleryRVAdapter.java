package com.example.renadoparia.sportjunkiem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Renado_Paria on 4/4/2017 at 5:29 PM for SportJunkieM.
 * Adapter For The Gallery In The Article Detail Activity
 */

class GalleryRVAdapter extends RecyclerView.Adapter<GalleryRVAdapter.GalleryViewHolder>
{
    private List<String> mPhotosList;
    private Context mContext;
//    private Boolean isImageLongPressed = false;
//    private Dialog builder;

    GalleryRVAdapter(List<String> photosList, Context context)
    {
        mPhotosList = photosList;
        mContext = context;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_imageview, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position)
    {
        String imageUrl = mPhotosList.get(position);
        Glide.with(mContext)
                .load(imageUrl)
                .error(R.drawable.lgb2_blur)
                .into(holder.galleryImage);
//
//        builder = new Dialog(mContext);
//        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ImageView imageView = new ImageView(mContext);
//        Picasso.with(mContext)
//                .load(imageUrl)
//                .error(R.drawable.ic_image_black_48dp)
//                .into(imageView);
//
//        builder.addContentView(imageView, new RelativeLayout.LayoutParams
//                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
//        {
//            @Override
//            public boolean onLongClick(View v)
//            {
//                builder.show();
//                isImageLongPressed = true;
//                return true;
//            }
//        });
//
//        holder.itemView.setOnTouchListener(new View.OnTouchListener()
//        {
//            @Override
//            public boolean onTouch(View v, MotionEvent event)
//            {
//                if (event.getAction() == MotionEvent.ACTION_UP)
//                {
//                    if (isImageLongPressed)
//                    {
//                        builder.dismiss();
//                        isImageLongPressed = false;
//                    }
//                }
//                return false;
//            }
//        });


    }

    @Override
    public int getItemCount()
    {
        if ((mPhotosList.size() != 0) && (mPhotosList != null))
        {
            return mPhotosList.size();
        }
        else
        {
            return 0;
        }
    }

    void LoadPhotos(List<String> photoList)
    {
        mPhotosList = photoList;
        notifyDataSetChanged();
    }

    String getPhotoURL(int position)
    {
        return ((mPhotosList != null) && (mPhotosList.size() != 0) ? mPhotosList.get(position) : null);
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder
    {
        ImageView galleryImage;

        GalleryViewHolder(View itemView)
        {
            super(itemView);
            galleryImage = (ImageView) itemView.findViewById(R.id.galleryimageview);
        }
    }
}
