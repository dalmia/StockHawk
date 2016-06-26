
package com.passenger.android.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.passenger.android.stockhawk.R;
import com.passenger.android.stockhawk.data.QuoteColumns;
import com.passenger.android.stockhawk.data.QuoteProvider;


/**
 * Fragment that loads up the data of the stock over the years
 */
public class StockDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    public StockDetailFragment() {
    }

    public static final String STOCK_SYMBOL = "symbol";
    private static final int STOCK_DETAIL_LOADER_ID = 1;

    private String currency;
    private LineChartView lineChartView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_detail, container, false);
        if (getArguments() != null) {
            currency = getArguments().getString(STOCK_SYMBOL);
            getActivity().setTitle(currency);
            lineChartView = (LineChartView) view.findViewById(R.id.linechart);
        }
        Log.d("Current", currency);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(STOCK_DETAIL_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (currency != null) {
            switch (id) {
                case STOCK_DETAIL_LOADER_ID:
                    return new CursorLoader(getActivity(),
                            QuoteProvider.Quotes.CONTENT_URI,
                            MyStocksActivity.QUOTE_COLUMNS,
                            QuoteColumns.SYMBOL + " = ?",
                            new String[]{currency},
                            null);
            }

        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0)
            makeChart(data);
        else
            Toast.makeText(getActivity(), "No data", Toast.LENGTH_SHORT).show();
    }

    public void makeChart(Cursor data) {
        LineSet lineSet = new LineSet();
        float minimumPrice = Float.MAX_VALUE;
        float maximumPrice = Float.MIN_VALUE;

        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            String label = data.getString(MyStocksActivity.COL_BID_PRICE);
            float price = Float.parseFloat(label);

            lineSet.addPoint(label, price);
            minimumPrice = Math.min(minimumPrice, price);
            maximumPrice = Math.max(maximumPrice, price);
        }

        lineSet.setColor(Color.parseColor("#758cbb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#758cbb"))
                .setThickness(4)
                .setDashed(new float[]{10f, 10f});


        lineChartView.setBorderSpacing(Tools.fromDpToPx(15))
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setXLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(Color.parseColor("#6a84c3"))
                .setXAxis(false)
                .setYAxis(false)
                .setAxisBorderValues(Math.round(Math.max(0f, minimumPrice - 5f)), Math.round(maximumPrice + 5f))
                .addData(lineSet);

        if(lineSet.size() > 0) {
            //Setting the default animation
            Animation anim = new Animation();
            lineChartView.show(anim);
        }else {
            if (getView() != null) {
                Snackbar snackbar = Snackbar
                        .make(getView().findViewById(android.R.id.content),
                                R.string.no_stock_data, Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }else{
                Toast.makeText(getActivity(), getString(R.string.no_stock_data),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}

