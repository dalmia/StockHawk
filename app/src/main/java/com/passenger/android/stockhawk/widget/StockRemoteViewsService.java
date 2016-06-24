package com.passenger.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.passenger.android.stockhawk.R;
import com.passenger.android.stockhawk.data.QuoteColumns;
import com.passenger.android.stockhawk.data.QuoteProvider;
import com.passenger.android.stockhawk.ui.MyStocksActivity;

import static com.passenger.android.stockhawk.ui.MyStocksActivity.COL_BID_PRICE;
import static com.passenger.android.stockhawk.ui.MyStocksActivity.COL_CHANGE;
import static com.passenger.android.stockhawk.ui.MyStocksActivity.COL_IS_UP;
import static com.passenger.android.stockhawk.ui.MyStocksActivity.COL_SYMBOL;

/**
 * Created by aman on 24/6/16.
 */
public class StockRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        MyStocksActivity.QUOTE_COLUMNS,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Log.d("Count", String.valueOf(data.getCount()));
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if(data != null){
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                if(data != null){
                    return data.getCount();
                }
                return 0;
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if(position == AdapterView.INVALID_POSITION || !data.moveToPosition(position)){
                    return null;
                }

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.list_item_quote);
                remoteViews.setTextViewText(R.id.stock_symbol, data.getString(COL_SYMBOL));
                remoteViews.setTextViewText(R.id.change, data.getString(COL_CHANGE));
                remoteViews.setTextViewText(R.id.bid_price, data.getString(COL_BID_PRICE));
                if(data.getInt(COL_IS_UP) == 0){
                    remoteViews.setInt(R.id.change, "setBackgroundResource",
                            R.drawable.percent_change_pill_red);
                }else{
                    remoteViews.setInt(R.id.change, "setBackgroundResource",
                            R.drawable.percent_change_pill_green);
                }
                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(data != null){
                    return data.getLong(data.getColumnIndexOrThrow(QuoteColumns._ID));
                }
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }
        };
    }
}
