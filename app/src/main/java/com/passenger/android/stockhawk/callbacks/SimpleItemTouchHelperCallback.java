package com.passenger.android.stockhawk.callbacks;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.passenger.android.stockhawk.interfaces.ItemTouchHelperAdapter;
import com.passenger.android.stockhawk.interfaces.ItemTouchHelperViewHolder;

/**
 * Created by sam_chordas on 10/6/15.
 * credit to Paul Burke (ipaulpro)
 * this class enables swipe to delete in RecyclerView
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback{
  private final ItemTouchHelperAdapter mAdapterHelper;
  public static final float ALPHA_FULL = 1.0f;

  public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapterHelper){
    mAdapterHelper = adapterHelper;
  }

  @Override
  public boolean isItemViewSwipeEnabled(){
    return true;
  }

  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
    final int dragFlags = 0;
    final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
    return makeMovementFlags(dragFlags, swipeFlags);
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder sourceViewHolder, RecyclerView.ViewHolder targetViewHolder){
    return true;
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int i){
    mAdapterHelper.onItemDismiss(viewHolder.getAdapterPosition());
  }


  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState){
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE){
      ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
      itemViewHolder.onItemSelected();
    }

    super.onSelectedChanged(viewHolder, actionState);
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);

    ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
    itemViewHolder.onItemClear();
  }
}
