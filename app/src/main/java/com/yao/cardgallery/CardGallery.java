package com.yao.cardgallery;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yao on 2016/10/29 0029.
 */

public class CardGallery extends RecyclerView {

    //CardGallery的适配器
    private Adapter<?> wrapperAdapter;

    //当前item的索引
    private int currentIndex = 0;

    //改变选中的监听器的集合
    private List<OnPageChangedListener> mOnPageChangedListeners;

    public CardGallery(Context context) {
        super(context);
        init();
    }

    public CardGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        wrapperAdapter = transformCardGalleryAdapter(adapter);
        super.setAdapter(wrapperAdapter);
        initPosition();
    }

    @Override
    public void swapAdapter(RecyclerView.Adapter adapter, boolean removeAndRecycleExistingViews) {
        wrapperAdapter = transformCardGalleryAdapter(adapter);
        super.swapAdapter(wrapperAdapter, removeAndRecycleExistingViews);
        initPosition();
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        if (wrapperAdapter != null) {
            return wrapperAdapter.adapter;
        }
        return null;
    }

    /**
     * 获取包装的Adapter
     *
     * @return
     */
    public Adapter getWrapperAdapter() {
        return wrapperAdapter;
    }

    /**
     * 初始化item位置
     */
    private void initPosition() {
        scrollToPosition(getMiddlePosition());
        new Thread() {
            @Override
            public void run() {
                while (getChildAt(0) == null) ;
                post(new Runnable() {
                    @Override
                    public void run() {
                        alignView(0);
                    }
                });
            }
        }.start();
    }

    /**
     * 转换为CardGallery.Adapter
     *
     * @param adapter
     * @return
     */
    @NonNull
    protected Adapter transformCardGalleryAdapter(RecyclerView.Adapter adapter) {
        return (adapter instanceof Adapter) ? (Adapter) adapter : new Adapter(this, adapter);

    }


    private void init() {

        setHorizontalScrollBarEnabled(false);
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        //添加滚动监听
        addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int newIndex = getItemW();
                if (newState == SCROLL_STATE_IDLE) {//停止滚动时
                    if (mOnPageChangedListeners != null) {
                        for (OnPageChangedListener onPageChangedListener : mOnPageChangedListeners) {
                            if (onPageChangedListener != null) {
                                onPageChangedListener.OnPageChanged(getCurrentItemId(), getChildAt(newIndex).getId());
                            }
                        }
                    }
                    alignView(newIndex);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int centreW = getView4CentreW(CardGallery.this);
                for (int i = 0; i < getChildCount(); i++) {
                    View view = getChildAt(i);
                    int height = view.getHeight();
                    int difference = Math.abs(centreW - getView4CentreW(view));
                    difference = difference > centreW ? centreW : difference;
                    int padding = difference / 5;
                    view.setPadding(padding, padding, padding, padding);
                    if (height != 0) {
                        view.getLayoutParams().height = height;
                        view.setLayoutParams(view.getLayoutParams());
                    }
                    if (difference <= 100) {
                        if (mOnPageChangedListeners != null) {
                            for (OnPageChangedListener onPageChangedListener : mOnPageChangedListeners) {
                                if (onPageChangedListener != null) {
                                    onPageChangedListener.OnPageChanged(getCurrentItemId(), view.getId());
                                }
                            }
                        }
                        currentIndex = i;
                    }
                }
            }
        });
    }

    public void alignView(int newIndex) {
        currentIndex = newIndex;
        alignView(getChildAt(newIndex));
    }

    /**
     * 当前item对齐中线
     *
     * @param view
     */
    public void alignView(View view) {
        int dx = getView4CentreW(CardGallery.this) - getView4CentreW(view);
        smoothScrollBy(-dx, 0);
    }

    public int getCurrentItemId() {
        return getChildAt(currentIndex).getId();
    }

    private int getActualItemCountFromAdapter() {
        return (getWrapperAdapter()).getActualItemCount();
    }

    public int getMiddlePosition() {
        int middlePosition = Integer.MAX_VALUE / 2;
        final int actualItemCount = getActualItemCountFromAdapter();
        if (actualItemCount > 0 && middlePosition % actualItemCount != 0) {
            middlePosition = middlePosition - middlePosition % actualItemCount;
        }
        return middlePosition;
    }

    public int getItemW() {
        int centreW = getView4CentreW(this);
        int min = 10086;
        int item = 0;
        for (int i = 0; i < getChildCount(); i++) {
            int difference = centreW - getView4CentreW(getChildAt(i));
            if (Math.abs(min) > Math.abs(difference)) {
                min = difference;
                item = i;
            }
        }
        return item;
    }

    public int getView4CentreW(View view) {
        int w = view.getWidth();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return w / 2 + location[0];
    }


    public static class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements View.OnClickListener {

        RecyclerView.Adapter<VH> adapter;
        CardGallery viewPager;
        View.OnClickListener onClickListener;
        int selectItemWidth;

        public Adapter(CardGallery viewPager, RecyclerView.Adapter adapter) {
            this.adapter = adapter;
            this.viewPager = viewPager;
            selectItemWidth = (int) (getDisplayWidth((Activity) viewPager.getContext()) / 3f * 2);
        }

        public int getSelectItemWidth() {
            return selectItemWidth;
        }

        public void setSelectItemWidth(int selectItemWidth) {
            this.selectItemWidth = selectItemWidth;
        }

        public OnClickListener getOnClickListener() {
            return onClickListener;
        }

        public Adapter<VH> setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }

        private float getDisplayWidth(Activity activity) {
            DisplayMetrics metric = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
            return metric.widthPixels;     // 屏幕宽度（像素）
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = adapter.onCreateViewHolder(parent, viewType);
            vh.itemView.setOnClickListener(this);
            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            adapter.onBindViewHolder(holder, getActualPosition(position));
            final View itemView = holder.itemView;
            ViewGroup.LayoutParams lp;
            if (itemView.getLayoutParams() == null) {
                lp = new ViewGroup.LayoutParams(selectItemWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            } else {
                lp = itemView.getLayoutParams();
                if (viewPager.getLayoutManager().canScrollHorizontally()) {
                    lp.width = selectItemWidth;
                } else {
                    lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                }
            }
            itemView.setLayoutParams(lp);
            itemView.setId(getActualPosition(position));
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        public int getActualItemCount() {
            return adapter.getItemCount();
        }

        public int getActualPosition(int position) {
            int actualPosition = position;
            if (position >= getActualItemCount()) {
                actualPosition = position % getActualItemCount();
            }
            return actualPosition;
        }

        public int getActualItemViewType(int position) {
            return adapter.getItemViewType(position);
        }

        public long getActualItemId(int position) {
            return adapter.getItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return adapter.getItemViewType(getActualPosition(position));
        }

        @Override
        public long getItemId(int position) {
            return adapter.getItemId(getActualPosition(position));
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == viewPager.getCurrentItemId()) {
                if (onClickListener != null) onClickListener.onClick(v);
            } else {
                viewPager.alignView(v);
            }
        }
    }

    public interface OnPageChangedListener {
        void OnPageChanged(int oldPosition, int newPosition);
    }

    public void addOnPageChangedListener(OnPageChangedListener listener) {
        if (mOnPageChangedListeners == null) {
            mOnPageChangedListeners = new ArrayList<>();
        }
        mOnPageChangedListeners.add(listener);
    }
}
