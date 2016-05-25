package app.bennsandoval.com.woodmin.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.models.orders.Item;
import app.bennsandoval.com.woodmin.models.orders.Order;
import app.bennsandoval.com.woodmin.models.products.Product;
import app.bennsandoval.com.woodmin.models.products.Variation;

public class OrderLinesShip extends AppCompatActivity {

    private int mIndexProducts = 0;
    private int mCounterPerItem = 1;
    private Item mItemProcessing = null;
    private Product mProductProcessing = null;

    private List<Product> mProducts = new ArrayList<>();
    private Order mOrderSelected;
    private Gson mGson = new GsonBuilder().create();

    private static final String[] ORDER_PROJECTION = {
            WoodminContract.OrdersEntry.COLUMN_JSON,
    };
    private int COLUMN_ORDER_COLUMN_JSON = 0;

    private static final String[] PRODUCT_PROJECTION = {
            WoodminContract.ProductEntry.COLUMN_ID,
            WoodminContract.ProductEntry.COLUMN_JSON,
    };
    private int COLUMN_PRODUCT_COLUMN_JSON = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orderId = getIntent().getIntExtra("order", -1);
        setContentView(R.layout.activity_order_lines_ship);

        String query = WoodminContract.OrdersEntry.COLUMN_ID + " == ?" ;
        String[] parametersOrder = new String[]{ String.valueOf(orderId) };
        Cursor cursor = getContentResolver().query(WoodminContract.OrdersEntry.CONTENT_URI,
                ORDER_PROJECTION,
                query,
                parametersOrder,
                null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(COLUMN_ORDER_COLUMN_JSON);
                    if(json!=null){
                        mOrderSelected = mGson.fromJson(json, Order.class);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        List<String> ids = new ArrayList<>();
        List<String> parameters = new ArrayList<>();
        for(Item item:mOrderSelected.getItems()) {
            ids.add(String.valueOf(item.getProductId()));
            parameters.add("?");
        }

        query = WoodminContract.ProductEntry.COLUMN_ID + " IN (" + TextUtils.join(", ", parameters) + ")";
        cursor = getContentResolver().query(WoodminContract.ProductEntry.CONTENT_URI,
                PRODUCT_PROJECTION,
                query,
                ids.toArray(new String[ids.size()]),
                null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(COLUMN_PRODUCT_COLUMN_JSON);
                    if(json!=null) {
                        Product product = mGson.fromJson(json, Product.class);
                        mProducts.add(product);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.order, String.valueOf(orderId)));
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCounterPerItem ++;
                if(mItemProcessing.getQuantity() >= mCounterPerItem) {

                    Snackbar.make(view, mProductProcessing.getTitle() + " " + getString(R.string.counter_process, mCounterPerItem, mItemProcessing.getQuantity()), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    refreshDataCurrentItem();

                } else {

                    mIndexProducts++;
                    if(mIndexProducts < mProducts.size()) {

                        mCounterPerItem = 1;
                        searchItem();

                        Snackbar.make(view, mProductProcessing.getTitle() + " " + getString(R.string.counter_process, mCounterPerItem, mItemProcessing.getQuantity()), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        refreshDataCurrentItem();

                    } else {

                        finish();

                    }
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchItem();
        refreshDataCurrentItem();

    }

    private void searchItem() {
        for(Item item: mOrderSelected.getItems()) {
            if(mProducts.get(mIndexProducts).getId() == item.getProductId()) {
                mItemProcessing = item;
                mProductProcessing = mProducts.get(mIndexProducts);
                return;
            }
            for(Variation variation : mProducts.get(mIndexProducts).getVariations()) {
                if(variation.getId() == item.getProductId()) {
                    mItemProcessing = item;
                    mProductProcessing = mProducts.get(mIndexProducts);
                    return;
                }
            }
        }
    }

    private void refreshDataCurrentItem() {

        if(mCounterPerItem > 1) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderLinesShip.this)
                .setTitle(mProductProcessing.getTitle())
                .setMessage(getString(R.string.warning_more_items))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
            alertDialogBuilder.create().show();

        }

        LinearLayout header = (LinearLayout) findViewById(R.id.header);

        TextView txtOrder = (TextView) findViewById(R.id.order);
        TextView txtTitle = (TextView) findViewById(R.id.title);
        TextView txtDescription = (TextView) findViewById(R.id.description);
        TextView txtSku = (TextView) findViewById(R.id.sku);
        TextView txtQuantity = (TextView) findViewById(R.id.quantity);
        TextView txtStock = (TextView) findViewById(R.id.stock);
        TextView txtPrice = (TextView) findViewById(R.id.price);

        ImageView imageView = (ImageView) findViewById(R.id.item_image);

        Picasso.with(getApplicationContext())
                .load(mProductProcessing.getFeaturedSrc())
                .resize(500, 500)
                .centerCrop()
                .placeholder(android.R.color.transparent)
                .error(android.R.color.transparent)
                .into(imageView);

        if(mOrderSelected.getStatus().toUpperCase().equals("COMPLETED")){
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if(mOrderSelected.getStatus().toUpperCase().equals("CANCELLED") || mOrderSelected.getStatus().toUpperCase().equals("REFUNDED")){
            header.setBackgroundColor(getResources().getColor(R.color.red));
        } else {
            header.setBackgroundColor(getResources().getColor(R.color.orange));
        }
        txtSku.setText(mProductProcessing.getSku());
        txtOrder.setText(mOrderSelected.getOrderNumber());
        txtTitle.setText(mProductProcessing.getTitle());
        txtPrice.setText("$" + mProductProcessing.getPrice());
        txtStock.setText(String.valueOf(mProductProcessing.getStockQuantity()));
        txtDescription.setText(Html.fromHtml(mProductProcessing.getDescription()).toString());

        txtQuantity.setText(getString(R.string.counter_process, mCounterPerItem, mItemProcessing.getQuantity()));

    }

}
