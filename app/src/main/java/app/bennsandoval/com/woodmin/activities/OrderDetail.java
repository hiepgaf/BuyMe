package app.bennsandoval.com.woodmin.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.Woodmin;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.interfaces.Woocommerce;
import app.bennsandoval.com.woodmin.models.orders.Item;
import app.bennsandoval.com.woodmin.models.orders.MetaItem;
import app.bennsandoval.com.woodmin.models.orders.Note;
import app.bennsandoval.com.woodmin.models.orders.Notes;
import app.bennsandoval.com.woodmin.models.orders.Order;
import app.bennsandoval.com.woodmin.models.orders.OrderResponse;
import app.bennsandoval.com.woodmin.models.orders.OrderUpdate;
import app.bennsandoval.com.woodmin.models.orders.OrderUpdateValues;
import app.bennsandoval.com.woodmin.models.products.Product;
import app.bennsandoval.com.woodmin.models.products.Variation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = OrderDetail.class.getSimpleName();

    private int mOderId = -1;
    private Order mOrderSelected;
    private Gson mGson = new GsonBuilder().create();

    private LinearLayout mHeader;
    private TextView mOrder;
    private TextView mEmail;
    private TextView mPhone;
    private LinearLayout mLyPhone;
    private LinearLayout mLyEmail;
    private TextView mPrice;
    private TextView mStatus;
    private TextView mCustomer;
    private TextView mItems;
    private TextView mDate;

    private TextView mPayment;
    private TextView mAmount;
    private TextView mTaxes;
    private TextView mTotal;

    private TextView mBilling;
    private TextView mShipping;

    private ProgressDialog mProgress;

    private static final int ORDER_LOADER = 101;
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(mOderId < 0 ) {
            mOderId = getIntent().getIntExtra("order", -1);
            if(mOderId < 0 ) {
                mOderId = prefs.getInt("product_details", -1);
            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("product_details", mOderId);
                editor.apply();
            }
        }

        mProgress = new ProgressDialog(OrderDetail.this);
        mProgress.setTitle(getString(R.string.app_name));

        setContentView(R.layout.activity_order);
        mHeader = (LinearLayout) findViewById(R.id.header);
        mOrder = (TextView) findViewById(R.id.order);
        mPrice = (TextView) findViewById(R.id.price);
        mEmail = (TextView) findViewById(R.id.email);
        mPhone = (TextView) findViewById(R.id.phone);
        mLyPhone = (LinearLayout) findViewById(R.id.call_button);
        mLyEmail = (LinearLayout) findViewById(R.id.email_button);
        mStatus = (TextView) findViewById(R.id.status);
        mCustomer = (TextView) findViewById(R.id.customer);
        mItems = (TextView) findViewById(R.id.items);
        mDate = (TextView) findViewById(R.id.date);

        mPayment = (TextView) findViewById(R.id.payment);
        mAmount = (TextView) findViewById(R.id.amount);
        mTaxes = (TextView) findViewById(R.id.taxes);
        mTotal = (TextView) findViewById(R.id.total);

        mBilling = (TextView) findViewById(R.id.billing);
        mShipping = (TextView) findViewById(R.id.shipping);

        getSupportLoaderManager().initLoader(ORDER_LOADER, null, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetail.this)
                            .setTitle(getString(R.string.order, mOrderSelected.getOrderNumber()))
                            .setMessage(getString(R.string.order_update_confirmation))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finalizeOrder();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    alertDialogBuilder.create().show();

                }
            });
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.d(LOG_TAG, "onCreateLoader");
        String sortOrder = WoodminContract.OrdersEntry._ID + " ASC";
        CursorLoader cursorLoader = null;
        Uri ordersUri = WoodminContract.OrdersEntry.CONTENT_URI;
        switch (id) {
            case ORDER_LOADER:
                if(mOderId > 0){
                    String query = WoodminContract.OrdersEntry.COLUMN_ID + " == ?" ;
                    String[] parameters = new String[]{ String.valueOf(mOderId) };
                    cursorLoader = new CursorLoader(
                            getApplicationContext(),
                            ordersUri,
                            ORDER_PROJECTION,
                            query,
                            parameters,
                            sortOrder);
                }
                break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case ORDER_LOADER:
                mOrderSelected = null;
                if (cursor.moveToFirst()) {
                    do {
                        String json = cursor.getString(COLUMN_ORDER_COLUMN_JSON);
                        if(json!=null){
                            mOrderSelected = mGson.fromJson(json, Order.class);
                        }
                    } while (cursor.moveToNext());
                    fillView();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(LOG_TAG, "onLoaderReset");
        switch (cursorLoader.getId()) {
            case ORDER_LOADER: {
                mOrderSelected = null;
                break;
            }
            default:
                break;
        }
    }

    private void fillView(){

        if(mOrderSelected != null) {

            if(mOrderSelected.getStatus().toUpperCase().equals("COMPLETED")){
                mHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else if(mOrderSelected.getStatus().toUpperCase().equals("CANCELLED") || mOrderSelected.getStatus().toUpperCase().equals("REFUNDED")){
                mHeader.setBackgroundColor(getResources().getColor(R.color.red));
                mStatus.setTextColor(getResources().getColor(R.color.red));
            } else {
                mHeader.setBackgroundColor(getResources().getColor(R.color.orange));
                mStatus.setTextColor(getResources().getColor(R.color.orange));
            }
            mOrder.setText(getString(R.string.order, mOrderSelected.getOrderNumber()));
            mPrice.setText(getString(R.string.price, mOrderSelected.getTotal()));
            mStatus.setText(mOrderSelected.getStatus().toUpperCase());


            if(mOrderSelected.getCustomer() != null &&
                    mOrderSelected.getCustomer().getBillingAddress() != null &&
                    mOrderSelected.getCustomer().getBillingAddress().getEmail() != null &&
                    mOrderSelected.getCustomer().getBillingAddress().getEmail().length() > 1){

                mEmail.setText(mOrderSelected.getCustomer().getBillingAddress().getEmail());
                mLyEmail.setVisibility(View.VISIBLE);
                mLyEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",mOrderSelected.getCustomer().getBillingAddress().getEmail(), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                        startActivity(Intent.createChooser(emailIntent, "Woodmin"));
                    }
                });

            } else {
                mLyEmail.setVisibility(View.GONE);
            }

            if(mOrderSelected.getCustomer() != null &&
                    mOrderSelected.getCustomer().getBillingAddress() != null &&
                    mOrderSelected.getCustomer().getBillingAddress().getPhone() != null &&
                    mOrderSelected.getCustomer().getBillingAddress().getPhone().length() > 1){

                mPhone.setText(mOrderSelected.getCustomer().getBillingAddress().getPhone());
                mLyPhone.setVisibility(View.VISIBLE);
                mLyPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + mOrderSelected.getCustomer().getBillingAddress().getPhone()));
                        startActivity(callIntent);
                    }
                });
            } else {
                mLyPhone.setVisibility(View.GONE);
            }

            if(mOrderSelected.getBillingAddress().getFirstName() != null && mOrderSelected.getBillingAddress().getFirstName().length() > 0){
                mCustomer.setText(mOrderSelected.getBillingAddress().getFirstName() + " " + mOrderSelected.getBillingAddress().getLastName());
            } else {
                mCustomer.setText(getString(R.string.guest));
            }
            int itemsCount = 0;
            for (Item item:mOrderSelected.getItems()) {
                itemsCount += item.getQuantity();
            }
            mItems.setText(getString(R.string.items, itemsCount));

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.getDefault());
            mDate.setText(format.format(mOrderSelected.getCreatedAt()));

            if(mOrderSelected.getPaymentDetails() != null){
                mPayment.setText(mOrderSelected.getPaymentDetails().getMethodTitle());
            }
            mAmount.setText("$"+mOrderSelected.getSubtotal());
            mTaxes.setText("$"+mOrderSelected.getTotalTax());
            mTotal.setText("$"+mOrderSelected.getTotal());

            if(mOrderSelected.getCustomer() != null &&
                    mOrderSelected.getBillingAddress() != null){

                String address = mOrderSelected.getBillingAddress().getAddressOne() + " " +
                        mOrderSelected.getBillingAddress().getAddressTwo() + " " +
                        mOrderSelected.getBillingAddress().getPostcode() + " " +
                        mOrderSelected.getBillingAddress().getState() + " " +
                        mOrderSelected.getBillingAddress().getCity() + " " +
                        mOrderSelected.getBillingAddress().getCountry();
                mBilling.setText(address);

            }

            if(mOrderSelected.getCustomer() != null &&
                    mOrderSelected.getShippingAddress() != null){

                String addres = mOrderSelected.getShippingAddress().getAddressOne() + " " +
                        mOrderSelected.getShippingAddress().getAddressTwo() + " " +
                        mOrderSelected.getShippingAddress().getPostcode() + " " +
                        mOrderSelected.getShippingAddress().getCountry() + " " +
                        mOrderSelected.getShippingAddress().getState() + " " +
                        mOrderSelected.getShippingAddress().getCity();

                mShipping.setText(addres);
            }

            CardView cart = (CardView)findViewById(R.id.shopping_card);
            cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent orderIntent = new Intent(getApplicationContext(), OrderLinesShip.class);
                    orderIntent.putExtra("order", mOderId);
                    startActivityForResult(orderIntent, 100);
                }
            });

            while(cart.getChildCount() > 1) {
                cart.removeViewAt(1);
            }
            LinearLayout cardDetails = (LinearLayout)findViewById(R.id.shopping_card_details);

            List<String> ids = new ArrayList<>();
            List<String> parameters = new ArrayList<>();

            for(Item item:mOrderSelected.getItems()) {
                ids.add(String.valueOf(item.getProductId()));
                parameters.add("?");
            }

            String query = WoodminContract.ProductEntry.COLUMN_ID + " IN (" + TextUtils.join(", ", parameters) + ")";
            Cursor cursor = getContentResolver().query(WoodminContract.ProductEntry.CONTENT_URI,
                    PRODUCT_PROJECTION,
                    query,
                    ids.toArray(new String[ids.size()]),
                    null);

            List<Product> products = new ArrayList<>();
            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        String json = cursor.getString(COLUMN_PRODUCT_COLUMN_JSON);
                        if(json!=null) {
                            Product product = mGson.fromJson(json, Product.class);
                            products.add(product);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            for(Item item:mOrderSelected.getItems()) {

                View child = getLayoutInflater().inflate(R.layout.activity_order_item, null);
                ImageView imageView = (ImageView) child.findViewById(R.id.image);
                TextView quantity = (TextView) child.findViewById(R.id.quantity);
                TextView description = (TextView) child.findViewById(R.id.description);
                TextView price = (TextView) child.findViewById(R.id.price);
                TextView sku = (TextView) child.findViewById(R.id.sku);

                quantity.setText(String.valueOf(item.getQuantity()));
                if(item.getMeta().size()>0){
                    String descriptionWithMeta = item.getName();
                    for(MetaItem itemMeta:item.getMeta()){
                        descriptionWithMeta += "\n" + itemMeta.getLabel() + " " + itemMeta.getValue();
                    }
                    description.setText(descriptionWithMeta);
                } else {
                    description.setText(item.getName());
                }
                price.setText(getString(R.string.price, item.getTotal()));
                sku.setText(item.getSku());

                Product productForItem = null;
                for(Product product: products) {
                    if(product.getId() == item.getProductId()) {
                        productForItem = product;
                        break;
                    }
                    for(Variation variation:product.getVariations()) {
                        if(variation.getId() == item.getProductId()) {
                            productForItem = product;
                            break;
                        }
                    }
                }

                if(productForItem == null) {
                    Log.v(LOG_TAG, "Missing product");
                } else {
                    Picasso.with(getApplicationContext())
                            .load(productForItem.getFeaturedSrc())
                            .resize(50, 50)
                            .centerCrop()
                            .placeholder(android.R.color.transparent)
                            .error(R.drawable.ic_action_cancel)
                            .into(imageView);
                }

                cardDetails.addView(child);
            }

            getNotes();

        } else {
            finish();
        }

    }

    private void getNotes() {

        Woocommerce woocommerceApi = ((Woodmin) getApplication()).getWoocommerceApiHandler();
        HashMap<String, String> options = new HashMap<>();
        Call<Notes> call = woocommerceApi.getOrdersNotes(options, String.valueOf(mOrderSelected.getId()));
        call.enqueue(new Callback<Notes>() {

            @Override
            public void onResponse(Call<Notes> call, Response<Notes> response) {

                int statusCode = response.code();
                if (statusCode == 200) {
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    final LinearLayout notesView = (LinearLayout) findViewById(R.id.notes);
                    if(notesView.getChildCount() == 1) {
                        for (Note note : response.body().getNotes()) {

                            final View child = getLayoutInflater().inflate(R.layout.activity_note_item, null);
                            TextView privateNote = (TextView) child.findViewById(R.id.private_note);
                            TextView noteText = (TextView) child.findViewById(R.id.note_text);
                            TextView noteDate = (TextView) child.findViewById(R.id.note_date);

                            if (note.isCustomerNote()) {
                                privateNote.setText(getString(R.string.public_note));
                            } else {
                                privateNote.setText(getString(R.string.private_note));
                            }

                            noteText.setText(note.getNote());
                            noteDate.setText(format.format(note.getCreatedAt()));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notesView.addView(child);
                                }
                            });

                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<Notes> call, Throwable throwable) {
                Log.e(LOG_TAG, "onFailure ");
            }

        });
    }

    private void finalizeOrder() {

        mProgress.setMessage(getString(R.string.finalize_order));
        mProgress.show();

        OrderUpdate orderUpdate = new OrderUpdate();
        OrderUpdateValues orderUpdateValues = new OrderUpdateValues();
        orderUpdateValues.setStatus("completed");
        orderUpdate.setOrder(orderUpdateValues);

        Woocommerce woocommerceApi = ((Woodmin) getApplication()).getWoocommerceApiHandler();
        Call<OrderResponse> call = woocommerceApi.updateOrder(mOrderSelected.getOrderNumber(), orderUpdate);
        call.enqueue(new Callback<OrderResponse>() {

            @Override
            public void onResponse(final Call<OrderResponse> call, final Response<OrderResponse> response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.dismiss();
                    }
                });

                int statusCode = response.code();
                if (statusCode == 200) {
                    Order order = response.body().getOrder();
                    ContentValues orderValues = new ContentValues();
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_ID, order.getId());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_ORDER_NUMBER, order.getOrderNumber());
                    if(order.getCreatedAt() != null) {
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CREATED_AT, WoodminContract.getDbDateString(order.getCreatedAt()));
                    }
                    if(order.getUpdatedAt() != null) {
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_UPDATED_AT, WoodminContract.getDbDateString(order.getUpdatedAt()));
                    }
                    if(order.getCompletedAt() != null) {
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_COMPLETED_AT, WoodminContract.getDbDateString(order.getCompletedAt()));
                    }
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_STATUS, order.getStatus());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CURRENCY, order.getCurrency());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_TOTAL, order.getTotal());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SUBTOTAL, order.getSubtotal());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_TOTAL_LINE_ITEMS_QUANTITY, order.getTotalLineItemsQuantity());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_TOTAL_TAX, order.getTotalTax());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_TOTAL_SHIPPING, order.getTotalShipping());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CART_TAX, order.getCartTax());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_TAX, order.getShippingTax());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_TOTAL_DISCOUNT, order.getTotalDiscount());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CART_DISCOUNT, order.getCartDiscount());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_ORDER_DISCOUNT, order.getOrderDiscount());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_METHODS, order.getShippingMethods());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_NOTE, order.getNote());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_VIEW_ORDER_URL, order.getViewOrderUrl());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_ID, order.getPaymentDetails().getMethodId());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_TITLE, order.getPaymentDetails().getMethodTitle());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_PAID, order.getPaymentDetails().isPaid() ? "1" : "0");
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME, order.getBillingAddress().getFirstName());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_LAST_NAME , order.getBillingAddress().getLastName());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_COMPANY, order.getBillingAddress().getCompany());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_ADDRESS_1, order.getBillingAddress().getAddressOne());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_ADDRESS_2, order.getBillingAddress().getAddressTwo());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_CITY, order.getBillingAddress().getCity());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_STATE, order.getBillingAddress().getState());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_POSTCODE, order.getBillingAddress().getPostcode());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_COUNTRY, order.getBillingAddress().getCountry());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_EMAIL, order.getBillingAddress().getEmail());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_BILLING_PHONE, order.getBillingAddress().getPhone());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_FIRST_NAME, order.getShippingAddress().getFirstName());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_LAST_NAME, order.getShippingAddress().getLastName());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_COMPANY, order.getShippingAddress().getCompany());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_1, order.getShippingAddress().getAddressOne());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_2, order.getShippingAddress().getAddressTwo());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_CITY, order.getShippingAddress().getCity());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_STATE, order.getShippingAddress().getState());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_POSTCODE, order.getShippingAddress().getPostcode());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_SHIPPING_COUNTRY, order.getShippingAddress().getCountry());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_ID, order.getCustomerId());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_EMAIL, order.getCustomer().getEmail());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME, order.getCustomer().getFirstName());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME, order.getCustomer().getLastName());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_USERNAME, order.getCustomer().getUsername());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_ID, order.getCustomer().getLastOrderId());
                    if(order.getCustomer().getLastOrderDate() != null) {
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_DATE, WoodminContract.getDbDateString(order.getCustomer().getLastOrderDate()));
                    }
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_ORDERS_COUNT, order.getCustomer().getOrdersCount());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_TOTAL_SPEND, order.getCustomer().getTotalSpent());
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_AVATAR_URL, order.getCustomer().getAvatarUrl());
                    if(order.getCustomer().getBillingAddress()!= null){
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_FIRST_NAME, order.getCustomer().getBillingAddress().getFirstName());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_LAST_NAME, order.getCustomer().getBillingAddress().getLastName());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COMPANY, order.getCustomer().getBillingAddress().getCompany());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_1, order.getCustomer().getBillingAddress().getAddressOne());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_2, order.getCustomer().getBillingAddress().getAddressTwo());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_CITY, order.getCustomer().getBillingAddress().getCity());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_STATE, order.getCustomer().getBillingAddress().getState());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_POSTCODE, order.getCustomer().getBillingAddress().getPostcode());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COUNTRY, order.getCustomer().getBillingAddress().getCountry());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_EMAIL, order.getCustomer().getBillingAddress().getEmail());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_PHONE, order.getCustomer().getBillingAddress().getPhone());
                    }
                    if(order.getCustomer().getShippingAddress() != null){
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_FIRST_NAME, order.getCustomer().getShippingAddress().getFirstName());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_LAST_NAME , order.getCustomer().getShippingAddress().getLastName());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COMPANY, order.getCustomer().getShippingAddress().getCompany());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_1, order.getCustomer().getShippingAddress().getAddressOne());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_2, order.getCustomer().getShippingAddress().getAddressTwo());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_CITY, order.getCustomer().getShippingAddress().getCity());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_STATE, order.getCustomer().getShippingAddress().getState());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_POSTCODE, order.getCustomer().getShippingAddress().getPostcode());
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COUNTRY, order.getCustomer().getShippingAddress().getCountry());
                    }
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_JSON, mGson.toJson(order));
                    orderValues.put(WoodminContract.OrdersEntry.COLUMN_ENABLE, 1);

                    Uri insertedOrderUri = getContentResolver().insert(WoodminContract.OrdersEntry.CONTENT_URI, orderValues);
                    long orderId = ContentUris.parseId(insertedOrderUri);
                    Log.d(LOG_TAG, "Orders successful updated ID: " + orderId);

                    getContentResolver().notifyChange(WoodminContract.OrdersEntry.CONTENT_URI, null, false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.success_update), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Log.e(LOG_TAG, "onFailure ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_update), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e(LOG_TAG, "onFailure ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_update), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

}
