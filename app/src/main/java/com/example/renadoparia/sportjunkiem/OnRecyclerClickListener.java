package com.example.renadoparia.sportjunkiem;

import android.view.View;

/**
 * Created by Renado_Paria on 3/25/2017 at 7:27 AM.
 */

interface OnRecyclerClickListener
{
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);

    void onItemDoubleTap(View view, int position);
}
