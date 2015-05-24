/*
 * Created by Hanks
 * Copyright (c) 2015 Nashangban. All rights reserved
 *
 */
package app.hanks.com.testadvancerecyclerview;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder;

import java.util.List;

/**
 * Created by Hanks on 2015/5/24.
 */
public class MyAdapter
        extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
        implements DraggableItemAdapter<MyAdapter.MyViewHolder>,
        SwipeableItemAdapter<MyAdapter.MyViewHolder> {


    private final List<MyItem>         dataList;

    public MyAdapter( List<MyItem> dataList) {
        this.dataList = dataList;

        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {

        return dataList.get(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_list_swipe, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        MyItem item = dataList.get(position);
        holder.textView.setText(item.getContent());

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();
        final int swipeState = holder.getSwipeStateFlags();

        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0) ||
                ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;
            } else if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_swiping_active_state;
            } else if ((swipeState & RecyclerViewSwipeManager.STATE_FLAG_SWIPING) != 0) {
                bgResId = R.drawable.bg_item_swiping_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.container.setBackgroundResource(bgResId);
        }

        // set swiping properties
        holder.setSwipeItemSlideAmount(
                item.isPinedToSwipeLeft() ? RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_LEFT : 0);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d("zyh", "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    private void moveItem(int fromPosition, int toPosition) {
        if (fromPosition == toPosition) {
            return;
        }

        final MyItem item = dataList.remove(fromPosition);

        dataList.add(toPosition, item);
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        if (onCheckCanStartDrag(holder, position, x, y)) {
            Log.d("zyh", "onCheckCanStartDrag," + position + "," + x + "," + y);
            return RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_BOTH;
        } else {
            Log.d("zyh", "onCheckCanStartDrag---," + position + "," + x + "," + y);
            return RecyclerViewSwipeManager.REACTION_CAN_SWIPE_BOTH;
        }
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        Log.d("zyh", "onSetSwipeBackground---," + position + "," + type);
        int bgRes = 0;
        switch (type) {
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_neutral;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_left;
                break;
            case RecyclerViewSwipeManager.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                bgRes = R.drawable.bg_swipe_item_right;
                break;
        }
        holder.itemView.setBackgroundResource(bgRes);
    }

    @Override
    public int onSwipeItem(MyViewHolder holder, int position, int result) {
        Log.d("zyh", "onSwipeItem(result = " + result + ")");

        switch (result) {
            // swipe right
            case RecyclerViewSwipeManager.RESULT_SWIPED_RIGHT:
                if (dataList.get(position).isPinedToSwipeLeft()) {
                    // pinned --- back to default position
                    return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
                } else {
                    // not pinned --- remove
                    return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM;
                }
                // swipe left -- pin
            case RecyclerViewSwipeManager.RESULT_SWIPED_LEFT:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION;
            // other --- do nothing
            case RecyclerViewSwipeManager.RESULT_CANCELED:
            default:
                return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }
    }

    @Override
    public void onPerformAfterSwipeReaction(MyViewHolder holder, int position, int result, int reaction) {
        Log.d("zyh", "onPerformAfterSwipeReaction(result = " + result + ", reaction = " + reaction + ")");

        final MyItem item = dataList.get(position);

        if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM) {
            dataList.remove(position);
            notifyItemRemoved(position);

        } else if (reaction == RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION) {
            item.setPinedToSwipeLeft(true);
            notifyItemChanged(position);

        } else {
            item.setPinedToSwipeLeft(false);
        }
    }


    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.container;
        final View dragHandleView = holder.dragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }


    public static class MyViewHolder extends AbstractDraggableSwipeableItemViewHolder {

        private final ViewGroup container;
        private final TextView  textView;
        private final View      dragHandle;

        public MyViewHolder(View itemView) {
            super(itemView);
            container = (ViewGroup) itemView.findViewById(R.id.container);
            dragHandle = itemView.findViewById(R.id.drag_handle);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public View getSwipeableContainerView() {
            return container;
        }
    }


    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
        final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }


}
