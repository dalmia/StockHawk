package com.passenger.android.stockhawk.utils;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.passenger.android.stockhawk.data.QuoteColumns;
import com.passenger.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.passenger.android.stockhawk.service.StockTaskService.BID;
import static com.passenger.android.stockhawk.service.StockTaskService.CHANGE;
import static com.passenger.android.stockhawk.service.StockTaskService.CHANGE_IN_PERCENT;
import static com.passenger.android.stockhawk.service.StockTaskService.COUNT;
import static com.passenger.android.stockhawk.service.StockTaskService.QUERY;
import static com.passenger.android.stockhawk.service.StockTaskService.QUOTE;
import static com.passenger.android.stockhawk.service.StockTaskService.RESULTS;
import static com.passenger.android.stockhawk.service.StockTaskService.SYMBOL;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utility {

    private static String LOG_TAG = Utility.class.getSimpleName();

    public static boolean showPercent = true;

    public static ArrayList quoteJsonToContentVals(String JSON) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject = null;
        JSONArray resultsArray = null;
        try {
            jsonObject = new JSONObject(JSON);
            if (jsonObject != null && jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject(QUERY);
                int count = Integer.parseInt(jsonObject.getString(COUNT));
                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject(RESULTS)
                            .getJSONObject(QUOTE);
                    batchOperations.add(buildBatchOperation(jsonObject));
                } else {
                    resultsArray = jsonObject.getJSONObject(RESULTS).getJSONArray(QUOTE);

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            batchOperations.add(buildBatchOperation(jsonObject));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "String to JSON failed: " + e);
        }
        return batchOperations;
    }

    public static String truncateBidPrice(String bidPrice) {

        bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));

        return bidPrice;
    }

    public static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format("%.2f", round);
        StringBuffer changeBuffer = new StringBuffer(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }

    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String bid = jsonObject.getString(BID);
            if (!bid.equals("null")) {
                String bidPrice = truncateBidPrice(bid);
                if (bidPrice != null) {
                    String change = jsonObject.getString(CHANGE);
                    builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString(SYMBOL));
                    builder.withValue(QuoteColumns.BIDPRICE, bidPrice);
                    builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                            jsonObject.getString(CHANGE_IN_PERCENT), true));
                    builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
                    builder.withValue(QuoteColumns.ISCURRENT, 1);
                    if (change.charAt(0) == '-') {
                        builder.withValue(QuoteColumns.ISUP, 0);
                    } else {
                        builder.withValue(QuoteColumns.ISUP, 1);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
