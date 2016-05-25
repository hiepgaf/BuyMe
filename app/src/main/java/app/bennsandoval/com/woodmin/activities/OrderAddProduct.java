package app.bennsandoval.com.woodmin.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.models.customers.BillingAddress;
import app.bennsandoval.com.woodmin.models.orders.Item;
import app.bennsandoval.com.woodmin.models.orders.Order;
import app.bennsandoval.com.woodmin.models.products.Product;
import app.bennsandoval.com.woodmin.models.products.Variation;
import app.bennsandoval.com.woodmin.utilities.Utility;

public class OrderAddProduct extends AppCompatActivity {

    private final String LOG_TAG = OrderDetail.class.getSimpleName();

    private int mProductId = -1;
    private int mQuantity = 1;
    private float mPrice = 0;

    private Product mProductSelected;
    private Order mOrder;
    private Gson mGson = new GsonBuilder().create();

    private static final String[] PRODUCT_PROJECTION = {
            WoodminContract.ProductEntry.COLUMN_ID,
            WoodminContract.ProductEntry.COLUMN_JSON,
    };
    private int COLUMN_PRODUCT_COLUMN_JSON = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_add_product);

        if(mProductId < 0 ) {
            mProductId = getIntent().getIntExtra("product", -1);
        }

        String query = WoodminContract.ProductEntry.COLUMN_ID + " == ?" ;
        String[] parametersOrder = new String[]{ String.valueOf(mProductId) };
        Cursor cursor = getContentResolver().query(WoodminContract.ProductEntry.CONTENT_URI,
                PRODUCT_PROJECTION,
                query,
                parametersOrder,
                null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String json = cursor.getString(COLUMN_PRODUCT_COLUMN_JSON);
                    if(json!=null){
                        mProductSelected = mGson.fromJson(json, Product.class);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        String json = Utility.getPreferredShoppingCard(getApplicationContext());
        if(json != null) {
            mOrder = mGson.fromJson(json, Order.class);
        } else {
            mOrder = new Order();

            BillingAddress billingAddress = new BillingAddress();
            billingAddress.setCompany(getString(R.string.default_company));
            billingAddress.setFirstName(getString(R.string.default_first_name));
            billingAddress.setLastName(getString(R.string.default_last_name));
            billingAddress.setAddressOne(getString(R.string.default_line_one));
            billingAddress.setAddressTwo(getString(R.string.default_line_two));
            billingAddress.setCity(getString(R.string.default_city));
            billingAddress.setState(getString(R.string.default_state));
            billingAddress.setPostcode(getString(R.string.default_postal_code));
            billingAddress.setCountry(getString(R.string.default_country));
            billingAddress.setEmail(getString(R.string.default_email));
            billingAddress.setPhone(getString(R.string.default_phone));

            mOrder.setBillingAddress(billingAddress);

            Utility.setPreferredShoppingCard(getApplicationContext(), mGson.toJson(mOrder));
        }

        LinearLayout header = (LinearLayout)findViewById(R.id.header);
        TextView sku = (TextView)findViewById(R.id.sku);
        final TextView price = (TextView)findViewById(R.id.price);
        TextView title = (TextView)findViewById(R.id.title);
        final EditText quantity = (EditText)findViewById(R.id.quantity);

        Button remove = (Button)findViewById(R.id.less);
        Button add = (Button)findViewById(R.id.more);

        Button cancel = (Button)findViewById(R.id.cancel);
        Button ok = (Button)findViewById(R.id.ok);

        ImageView image = (ImageView) findViewById(R.id.item_image_card);

        mPrice = Float.valueOf(mProductSelected.getPrice());

        if(mProductSelected.getStockQuantity() > 0){
            header.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            header.setBackgroundColor(getResources().getColor(R.color.red));
        }

        sku.setText(mProductSelected.getSku());
        price.setText(getString(R.string.price, String.valueOf(mPrice)));
        title.setText(mProductSelected.getTitle());
        quantity.setText(String.valueOf(mQuantity));

        for(Item itemOder :mOrder.getItems()) {
            if(itemOder.getProductId() == mProductId) {
                quantity.setText(String.valueOf(itemOder.getQuantity()));
                mProductSelected.setStockQuantity(mProductSelected.getStockQuantity() + itemOder.getQuantity());
                break;
            }
        }

        Picasso.with(getApplicationContext())
                .load(mProductSelected.getFeaturedSrc())
                .resize(300, 300)
                .centerCrop()
                .placeholder(android.R.color.transparent)
                .error(R.drawable.ic_action_cancel)
                .into(image);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mQuantity = Integer.valueOf(quantity.getText().toString());
                } catch (Exception ex){
                    Log.e(LOG_TAG, "Error getting quantity");
                }
                if (mQuantity > 0) {
                    mQuantity--;
                    quantity.setText(String.valueOf(mQuantity));
                    price.setText(getString(R.string.price, String.valueOf(mQuantity * mPrice)));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_add_quantity), Toast.LENGTH_LONG).show();
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mQuantity = Integer.valueOf(quantity.getText().toString());
                } catch (Exception ex){
                    Log.e(LOG_TAG, "Error getting quantity");
                }
                if (mQuantity < mProductSelected.getStockQuantity()) {
                    mQuantity++;
                    quantity.setText(String.valueOf(mQuantity));
                    price.setText(getString(R.string.price, String.valueOf(mQuantity * mPrice)));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_add_stock_quantity), Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mQuantity = Integer.valueOf(quantity.getText().toString());
                } catch (Exception ex){
                    Log.e(LOG_TAG, "Error getting quantity");
                }
                if (mQuantity <= mProductSelected.getStockQuantity()) {

                    if(mQuantity >= 0) {

                        Item item = new Item();
                        for(Item itemOder :mOrder.getItems()) {
                            if(itemOder.getProductId() == mProductId) {
                                if(mQuantity == 0) {
                                    item.setProductId(itemOder.getId());
                                    mOrder.getItems().remove(itemOder);
                                } else {
                                    itemOder.setQuantity(mQuantity);
                                    itemOder.setTotal(String.valueOf(mQuantity * mPrice));
                                    item = itemOder;
                                }
                                break;
                            }
                        }
                        if(item.getProductId() < 0 && mQuantity > 0) {
                            item.setName(mProductSelected.getTitle());
                            item.setSku(mProductSelected.getSku());
                            item.setPrice(mProductSelected.getPrice());
                            item.setTotal(String.valueOf(mQuantity * mPrice));

                            item.setProductId(mProductId);
                            item.setQuantity(mQuantity);
                            mOrder.getItems().add(item);
                        }

                        updateProduct();

                        Utility.setPreferredShoppingCard(getApplicationContext(), mGson.toJson(mOrder));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_add_quantity), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_add_stock_quantity), Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void updateProduct() {

        mProductSelected.setStockQuantity(mProductSelected.getStockQuantity() - mQuantity);

        ArrayList<ContentValues> productsValues = new ArrayList<ContentValues>();

        ContentValues productValues = new ContentValues();
        productValues.put(WoodminContract.ProductEntry.COLUMN_ID, mProductSelected.getId());
        productValues.put(WoodminContract.ProductEntry.COLUMN_TITLE, mProductSelected.getTitle());
        productValues.put(WoodminContract.ProductEntry.COLUMN_SKU, mProductSelected.getSku());
        productValues.put(WoodminContract.ProductEntry.COLUMN_PRICE, mProductSelected.getPrice());
        productValues.put(WoodminContract.ProductEntry.COLUMN_STOCK, mProductSelected.getStockQuantity());
        productValues.put(WoodminContract.ProductEntry.COLUMN_JSON, mGson.toJson(mProductSelected));
        productValues.put(WoodminContract.ProductEntry.COLUMN_ENABLE, 1);

        productsValues.add(productValues);

        for(Variation variation:mProductSelected.getVariations()) {

            //TODO, CHANGE THIS APPROACH
            mProductSelected.setSku(variation.getSku());
            mProductSelected.setPrice(variation.getPrice());
            mProductSelected.setStockQuantity(variation.getStockQuantity() - mQuantity);

            ContentValues variationValues = new ContentValues();
            variationValues.put(WoodminContract.ProductEntry.COLUMN_ID, variation.getId());
            variationValues.put(WoodminContract.ProductEntry.COLUMN_TITLE, mProductSelected.getTitle());
            variationValues.put(WoodminContract.ProductEntry.COLUMN_SKU, mProductSelected.getSku());
            variationValues.put(WoodminContract.ProductEntry.COLUMN_PRICE, mProductSelected.getPrice());
            variationValues.put(WoodminContract.ProductEntry.COLUMN_STOCK, mProductSelected.getStockQuantity());
            variationValues.put(WoodminContract.ProductEntry.COLUMN_JSON, mGson.toJson(mProductSelected));
            variationValues.put(WoodminContract.ProductEntry.COLUMN_ENABLE, 1);

            productsValues.add(variationValues);

        }

        ContentValues[] productsValuesArray = new ContentValues[productsValues.size()];
        productsValuesArray = productsValues.toArray(productsValuesArray);
        int ordersRowsUpdated = getContentResolver().bulkInsert(WoodminContract.ProductEntry.CONTENT_URI, productsValuesArray);
        Log.v(LOG_TAG, "Products " + ordersRowsUpdated + " updated");

        getContentResolver().notifyChange(WoodminContract.ProductEntry.CONTENT_URI, null, false);

    }
}
