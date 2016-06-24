package com.passenger.android.stockhawk.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.passenger.android.stockhawk.R;
import com.passenger.android.stockhawk.data.QuoteColumns;
import com.passenger.android.stockhawk.data.QuoteProvider;
import com.passenger.android.stockhawk.interfaces.ItemTouchHelperAdapter;
import com.passenger.android.stockhawk.interfaces.ItemTouchHelperViewHolder;
import com.passenger.android.stockhawk.utils.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.passenger.android.stockhawk.ui.MyStocksActivity.COL_BID_PRICE;
import static com.passenger.android.stockhawk.ui.MyStocksActivity.COL_CHANGE;
import static com.passenger.android.stockhawk.ui.MyStocksActivity.COL_IS_UP;
import static com.passenger.android.stockhawk.ui.MyStocksActivity.COL_PERCENT_CHANGE;
import static com.passenger.android.stockhawk.ui.MyStocksActivity.COL_SYMBOL;

/**
 * Created by sam_chordas on 10/6/15.
 * Credit to skyfishjy gist:
 * https://gist.github.com/skyfishjy/443b7448f59be978bc59
 * for the code structure
 */
public class QuoteCursorAdapter extends CursorRecyclerViewAdapter<QuoteCursorAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static Context mContext;
    private static Typeface robotoLight;
    private boolean isPercent;

    public QuoteCursorAdapter(Context context, Cursor cursor, View mEmptyView) {
        super(context, cursor, mEmptyView);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        robotoLight = Typeface.createFromAsset(mContext.getAssets(), "fonts/Roboto-Light.ttf");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_quote, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        holder.symbol.setText(cursor.getString(COL_SYMBOL));
        holder.symbol.setContentDescription(mContext.getString(R.string.a11y_stock_symbol,
                holder.symbol.getText()));
        holder.itemView.setTag(cursor.getString(COL_SYMBOL));
        holder.bidPrice.setText(cursor.getString(COL_BID_PRICE));
        holder.bidPrice.setContentDescription(mContext.getString(R.string.a11y_price,
                holder.bidPrice.getText()));

        if (cursor.getInt(COL_IS_UP) == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.change.setBackground(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
            } else {
                holder.change.setBackgroundDrawable(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_green));
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.change.setBackground(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
            } else {
                holder.change.setBackgroundDrawable(
                        mContext.getResources().getDrawable(R.drawable.percent_change_pill_red));
            }
        }

        if (Utility.showPercent) {
            holder.change.setText(cursor.getString(COL_PERCENT_CHANGE));
        } else {
            holder.change.setText(cursor.getString(COL_CHANGE));
        }
        holder.change.setContentDescription(mContext.getString(R.string.a11y_change,
                holder.change.getText()));
    }

    @Override
    public void onItemDismiss(int position) {
        Cursor c = getCursor();
        c.moveToPosition(position);
        String symbol = c.getString(c.getColumnIndex(QuoteColumns.SYMBOL));
        mContext.getContentResolver().delete(QuoteProvider.Quotes.withSymbol(symbol), null, null);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder, View.OnClickListener {
        @BindView(R.id.stock_symbol)
        public TextView symbol;
        @BindView(R.id.bid_price)
        public TextView bidPrice;
        @BindView(R.id.change)
        public TextView change;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            symbol.setTypeface(robotoLight);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
