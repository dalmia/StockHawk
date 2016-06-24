package com.passenger.android.stockhawk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.passenger.android.stockhawk.R;

import static com.passenger.android.stockhawk.ui.StockDetailFragment.STOCK_SYMBOL;

public class StockDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        StockDetailFragment fragment = new StockDetailFragment();
        Bundle args = new Bundle();
        args.putString(STOCK_SYMBOL, getIntent().getStringExtra(STOCK_SYMBOL));
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.stock_detail_fragment, fragment).commit();

    }
}
