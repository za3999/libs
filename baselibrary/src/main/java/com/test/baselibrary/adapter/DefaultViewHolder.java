package com.test.baselibrary.adapter;

import android.view.View;

public class DefaultViewHolder<T> extends BaseViewHolder<T> {

    public DefaultViewHolder(View view) {
        this(view, false);
    }

    public DefaultViewHolder(View view, boolean itemClickEnable) {
        super(view, itemClickEnable);
    }

    @Override
    public void bindData(int position,T t) {

    }

    @Override
    public void initView(View view) {

    }
}
