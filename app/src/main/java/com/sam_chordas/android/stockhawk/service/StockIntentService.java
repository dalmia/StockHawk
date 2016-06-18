package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

import static com.sam_chordas.android.stockhawk.ui.MyStocksActivity.INTENT_SYMBOL;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {
    private final String TAG = StockIntentService.class.getSimpleName();
    public StockIntentService() {
        super(StockIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Stock Intent Service");
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        String tagValue = intent.getStringExtra(MyStocksActivity.INTENT_TAG);
        if (tagValue.equals(MyStocksActivity.INTENT_ADD)) {
            args.putString(INTENT_SYMBOL, intent.getStringExtra(INTENT_SYMBOL));
        }
        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
        stockTaskService.onRunTask(new TaskParams(tagValue, args));
    }
}
