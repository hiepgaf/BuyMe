package app.bennsandoval.com.woodmin.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.interfaces.Woocommerce;
import app.bennsandoval.com.woodmin.models.customers.Customers;
import app.bennsandoval.com.woodmin.models.orders.Count;
import app.bennsandoval.com.woodmin.models.customers.Customer;
import app.bennsandoval.com.woodmin.models.orders.Order;
import app.bennsandoval.com.woodmin.models.orders.Orders;
import app.bennsandoval.com.woodmin.models.products.Product;
import app.bennsandoval.com.woodmin.models.products.Products;
import app.bennsandoval.com.woodmin.models.products.Variation;
import app.bennsandoval.com.woodmin.models.shop.Shop;
import app.bennsandoval.com.woodmin.utilities.Utility;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WoodminSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = WoodminSyncAdapter.class.getSimpleName();
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private Woocommerce woocommerceApi;

    private int sizePageOrders = 50;
    private int sizeOrders = 0;
    private int pageOrder = 0;

    private int sizePageProduct = 50;
    private int sizeProducts = 0;
    private int pageProduct= 0;

    private int sizePageCustomer = 50;
    private int sizeCustomers = 0;
    private int pageCustomer= 0;

    private Gson gson = new GsonBuilder().create();

    public WoodminSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        Long lastSyncTimeStamp =  Utility.getPreferredLastSync(getContext());
        Log.d(LOG_TAG, "Last sync " + lastSyncTimeStamp);

        String user = Utility.getPreferredUser(getContext());
        AccountManager accountManager = (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
        final String authenticationHeader = "Basic " + Base64.encodeToString(
                (user + ":" + accountManager.getPassword(account)).getBytes(),
                Base64.NO_WRAP);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .cache(null);

        //TODO Remove this if you don't have a self cert
        /*
        if(Utility.getSSLSocketFactory() != null){
            clientBuilder
                    .sslSocketFactory(Utility.getSSLSocketFactory())
                    .hostnameVerifier(Utility.getHostnameVerifier());
        }
        */

        Interceptor basicAuthenticatorInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request authenticateRequest = request.newBuilder()
                        .addHeader("Authorization", authenticationHeader)
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(authenticateRequest);
            }
        };

        //OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(key, secret);
        //consumer.setSigningStrategy(new QueryStringSigningStrategy());
        //clientBuilder.addInterceptor(new SigningInterceptor(consumer));
        clientBuilder.addInterceptor(basicAuthenticatorInterceptor);

        String server = Utility.getPreferredServer(getContext());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();

        woocommerceApi = retrofit.create(Woocommerce.class);

        boolean syncAll = false;
        if(lastSyncTimeStamp != 0) {
            long diff = System.currentTimeMillis() - lastSyncTimeStamp;
            long hours = diff / (60 * 60 * 1000);
            if (hours > 1) {
                syncAll = true;
            }
        } else {
            syncAll = true;
        }

        //Shop
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnableShop = new Runnable() {
            public void run() {
                synchronizeShop();
            }
        };
        handler.post(runnableShop);

        //Products
        Runnable runnableProducts = new Runnable() {
            public void run() {
                synchronizeProducts(null);
            }
        };
        handler.post(runnableProducts);

        if(syncAll) {
            Date lastSync = new Date(lastSyncTimeStamp);
            if(lastSyncTimeStamp == 0) {
                lastSync = null;
            }
            final Date finalLastSync = lastSync;

            //Customers
            Runnable runnableCustomers = new Runnable() {
                public void run() {
                    synchronizeCustomers(finalLastSync);
                }
            };
            handler.post(runnableCustomers);

            //Orders
            Runnable runnableOrders = new Runnable() {
                public void run() {
                    synchronizeOrders(finalLastSync);
                }
            };
            handler.post(runnableOrders);

        } else {
            Log.d(LOG_TAG, "Synchronization to early");
            final Date lastSync = new Date(lastSyncTimeStamp);

            //Customers
            Runnable runnableCustomers = new Runnable() {
                public void run() {
                    synchronizeCustomers(lastSync);
                }
            };
            handler.post(runnableCustomers);

            //Orders
            Runnable runnableOrders = new Runnable() {
                public void run() {
                    synchronizeOrders(lastSync);
                }
            };
            handler.post(runnableOrders);
        }
    }

    private void synchronizeCustomers(final Date date) {
        Log.v(LOG_TAG, "Customers sync start");

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_media_play);
        Notification notification = new NotificationCompat.Builder(getContext())
            .setProgress(100, 0, false)
            .setContentTitle(getContext().getString(R.string.sync_customers))
            .setContentText(getContext().getString(R.string.sync_count, 0, 0))
            /*
            .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(getContext().getString(R.string.sync_customers)))
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setVibrate(new long[]{0, 400})
             */
            .setSmallIcon(R.drawable.ic_media_play)
            .setLargeIcon(bm)
            .setWhen(System.currentTimeMillis())
            .setSound(uri)
            .setAutoCancel(true)
            .build();
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notification);

        if(date == null) {

            Call<Count> call = woocommerceApi.countCustomers();
            call.enqueue(new Callback<Count>() {
                @Override
                public void onResponse(Call<Count> call, retrofit2.Response<Count> response) {

                    int statusCode = response.code();
                    if (statusCode == 200) {
                        try {
                            sizeCustomers = Integer.valueOf(response.body().getCount());
                        } catch (NumberFormatException exception) {
                            Log.e(LOG_TAG, "NumberFormatException " + exception.getMessage());
                        }

                        /*
                        ContentValues values = new ContentValues();
                        values.put(WoodminContract.CustomerEntry.COLUMN_ENABLE, 0);
                        int ordersRowsDisabled = getContext().getContentResolver().update(WoodminContract.CustomerEntry.CONTENT_URI, values, null, null);
                        Log.v(LOG_TAG, "Customers " + ordersRowsDisabled + " disabled");
                        Log.v(LOG_TAG, "Customers " + sizeOrders + " to sync");
                        */

                        synchronizeBatchCustomers(date);
                    }

                }

                @Override
                public void onFailure(Call<Count> call, Throwable t) {

                }
            });

        } else {
            synchronizeBatchCustomers(date);
        }
    }

    private void synchronizeBatchCustomers(final Date date) {

        Log.v(LOG_TAG, "Customers Total:" + sizeCustomers + " Current: " + pageCustomer * sizePageCustomer + " Page : " + pageCustomer);

        HashMap<String, String> options = new HashMap<>();
        options.put("filter[limit]", String.valueOf(sizePageCustomer));
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            options.put("filter[updated_at_min]", dateFormat.format(date));
        }
        options.put("page", String.valueOf(pageCustomer));

        Call<Customers> call = woocommerceApi.getCustomers(options);
        call.enqueue(new Callback<Customers>() {
            @Override
            public void onResponse(Call<Customers> call, retrofit2.Response<Customers> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    Log.v(LOG_TAG, "Success page Customer " + pageCustomer);

                    //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_media_play);
                    Notification notification = new NotificationCompat.Builder(getContext())
                        .setProgress(sizeCustomers, (sizePageCustomer * pageCustomer), false)
                        .setContentTitle(getContext().getString(R.string.sync_customers))
                        .setContentText(getContext().getString(R.string.sync_count, (sizePageCustomer * pageCustomer), sizeCustomers))
                        /*
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getContext().getString(R.string.sync_customers)))
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setVibrate(new long[]{0, 400})
                        .setSound(uri)
                         */
                        .setSmallIcon(R.drawable.ic_media_play)
                        .setLargeIcon(bm)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .build();
                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(2, notification);

                    ArrayList<ContentValues> customersValues = new ArrayList<ContentValues>();
                    for (Customer customer : response.body().getCustomers()) {

                        ContentValues customerValues = new ContentValues();
                        customerValues.put(WoodminContract.CustomerEntry.COLUMN_ID, customer.getId());
                        customerValues.put(WoodminContract.CustomerEntry.COLUMN_EMAIL, customer.getEmail());
                        customerValues.put(WoodminContract.CustomerEntry.COLUMN_FIRST_NAME, customer.getFirstName());
                        customerValues.put(WoodminContract.CustomerEntry.COLUMN_LAST_NAME, customer.getLastName());
                        customerValues.put(WoodminContract.CustomerEntry.COLUMN_USERNAME, customer.getUsername());
                        customerValues.put(WoodminContract.CustomerEntry.COLUMN_LAST_ORDER_ID, customer.getLastOrderId());

                        if (customer.getBillingAddress() != null) {
                            customerValues.put(WoodminContract.CustomerEntry.COLUMN_BILLING_FIRST_NAME, customer.getBillingAddress().getFirstName());
                            customerValues.put(WoodminContract.CustomerEntry.COLUMN_BILLING_LAST_NAME, customer.getBillingAddress().getLastName());
                            if (customer.getBillingAddress().getPhone() != null) {
                                customerValues.put(WoodminContract.CustomerEntry.COLUMN_BILLING_PHONE, customer.getBillingAddress().getPhone());
                            }
                        }
                        if (customer.getShippingAddress() != null) {
                            customerValues.put(WoodminContract.CustomerEntry.COLUMN_SHIPPING_FIRST_NAME, customer.getShippingAddress().getFirstName());
                            customerValues.put(WoodminContract.CustomerEntry.COLUMN_SHIPPING_LAST_NAME, customer.getShippingAddress().getLastName());
                            if (customer.getShippingAddress().getPhone() != null) {
                                customerValues.put(WoodminContract.CustomerEntry.COLUMN_SHIPPING_PHONE, customer.getShippingAddress().getPhone());
                            }
                        }
                        customerValues.put(WoodminContract.CustomerEntry.COLUMN_JSON, gson.toJson(customer));
                        customerValues.put(WoodminContract.CustomerEntry.COLUMN_ENABLE, 1);

                        customersValues.add(customerValues);
                    }

                    ContentValues[] customersValuesArray = new ContentValues[customersValues.size()];
                    customersValuesArray = customersValues.toArray(customersValuesArray);
                    int customersRowsUpdated = getContext().getContentResolver().bulkInsert(WoodminContract.CustomerEntry.CONTENT_URI, customersValuesArray);
                    Log.v(LOG_TAG, "Customers " + customersRowsUpdated + " updated");
                }

                if (pageCustomer == 0 || (sizePageCustomer * pageCustomer) < sizeCustomers) {
                    pageCustomer++;
                    synchronizeBatchCustomers(date);
                } else {
                    finalizeSyncCustomers();
                }
            }

            @Override
            public void onFailure(Call<Customers> call, Throwable t) {
                if (pageCustomer == 0 || (sizePageCustomer * pageCustomer) < sizeCustomers) {
                    pageCustomer++;
                    synchronizeBatchCustomers(date);
                } else {
                    finalizeSyncCustomers();
                }
            }
        });

    }

    private void finalizeSyncCustomers() {

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);

        /*
        String query = WoodminContract.CustomerEntry.COLUMN_ENABLE + " = ?" ;
        String[] parameters = new String[]{ String.valueOf("0") };
        int rowsDeleted = getContext().getContentResolver().delete(WoodminContract.CustomerEntry.CONTENT_URI,
                query,
                parameters);
        Log.d(LOG_TAG, "Customers: " + rowsDeleted + " old records deleted.");
        */

        getContext().getContentResolver().notifyChange(WoodminContract.CustomerEntry.CONTENT_URI, null, false);
        pageCustomer = 0;
    }

    private void synchronizeProducts(final Date date) {
        Log.v(LOG_TAG, "Products sync start");

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_media_play);
        Notification notification = new NotificationCompat.Builder(getContext())
            .setProgress(100, 0, false)
            .setContentTitle(getContext().getString(R.string.sync_products))
            .setContentText(getContext().getString(R.string.sync_count, 0, 0))
            /*
            .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(getContext().getString(R.string.sync_products)))
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setVibrate(new long[]{0, 400})
             */
            .setSmallIcon(R.drawable.ic_media_play)
            .setLargeIcon(bm)
            .setWhen(System.currentTimeMillis())
            .setSound(uri)
            .setAutoCancel(true)
            .build();
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

        if(date == null) {

            Call<Count> call = woocommerceApi.countProducts();
            call.enqueue(new Callback<Count>() {
                @Override
                public void onResponse(Call<Count> call, retrofit2.Response<Count> response) {
                    int statusCode = response.code();
                    if (statusCode == 200) {
                        try {
                            sizeProducts = Integer.valueOf(response.body().getCount());
                        } catch (NumberFormatException exception) {
                            Log.e(LOG_TAG, "NumberFormatException " + exception.getMessage());
                        }

                        /*
                        ContentValues values = new ContentValues();
                        values.put(WoodminContract.ProductEntry.COLUMN_ENABLE, 0);
                        int ordersRowsDisabled = getContext().getContentResolver().update(WoodminContract.ProductEntry.CONTENT_URI, values, null, null);
                        Log.v(LOG_TAG, "Products " + ordersRowsDisabled + " disabled");
                        Log.v(LOG_TAG, "Products " + sizeOrders + " to sync");
                        */

                        synchronizeBatchProducts(date);
                    }
                }

                @Override
                public void onFailure(Call<Count> call, Throwable t) {

                }
            });

        } else {
            synchronizeBatchProducts(date);
        }
    }

    private void synchronizeBatchProducts(final Date date) {
        Log.v(LOG_TAG, "Products Total:" + sizeProducts + " Current: " + pageProduct * sizePageProduct + " Page : " + pageProduct);

        HashMap<String, String> options = new HashMap<>();
        options.put("filter[limit]", String.valueOf(sizePageProduct));
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            options.put("filter[updated_at_min]", dateFormat.format(date));
        }
        options.put("page", String.valueOf(pageProduct));
        options.put("filter[post_status]", "any");

        Call<Products> call = woocommerceApi.getProducts(options);
        call.enqueue(new Callback<Products>() {
            @Override
            public void onResponse(Call<Products> call, retrofit2.Response<Products> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    Log.v(LOG_TAG, "Success page Product " + pageProduct);

                    //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_media_play);
                    Notification notification = new NotificationCompat.Builder(getContext())
                        .setProgress(sizeProducts, (sizePageProduct * pageProduct), false)
                        .setContentTitle(getContext().getString(R.string.sync_products))
                        .setContentText(getContext().getString(R.string.sync_count, (sizePageProduct * pageProduct), sizeProducts))
                        /*
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getContext().getString(R.string.sync_products)))
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setVibrate(new long[]{0, 400})
                        .setSound(uri)
                         */
                        .setSmallIcon(R.drawable.ic_media_play)
                        .setLargeIcon(bm)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .build();
                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notification);

                    ArrayList<ContentValues> productsValues = new ArrayList<ContentValues>();
                    for (Product product : response.body().getProducts()) {

                        ContentValues productValues = new ContentValues();
                        productValues.put(WoodminContract.ProductEntry.COLUMN_ID, product.getId());
                        productValues.put(WoodminContract.ProductEntry.COLUMN_TITLE, product.getTitle());
                        productValues.put(WoodminContract.ProductEntry.COLUMN_SKU, product.getSku());
                        productValues.put(WoodminContract.ProductEntry.COLUMN_PRICE, product.getPrice());
                        productValues.put(WoodminContract.ProductEntry.COLUMN_STOCK, product.getStockQuantity());
                        productValues.put(WoodminContract.ProductEntry.COLUMN_JSON, gson.toJson(product));
                        productValues.put(WoodminContract.ProductEntry.COLUMN_ENABLE, 1);

                        productsValues.add(productValues);

                        for(Variation variation:product.getVariations()) {

                            //TODO, CHANGE THIS APPROACH
                            product.setSku(variation.getSku());
                            product.setPrice(variation.getPrice());
                            product.setStockQuantity(variation.getStockQuantity());

                            ContentValues variationValues = new ContentValues();
                            variationValues.put(WoodminContract.ProductEntry.COLUMN_ID, variation.getId());
                            variationValues.put(WoodminContract.ProductEntry.COLUMN_TITLE, product.getTitle());
                            variationValues.put(WoodminContract.ProductEntry.COLUMN_SKU, product.getSku());
                            variationValues.put(WoodminContract.ProductEntry.COLUMN_PRICE, product.getPrice());
                            variationValues.put(WoodminContract.ProductEntry.COLUMN_STOCK, product.getStockQuantity());
                            variationValues.put(WoodminContract.ProductEntry.COLUMN_JSON, gson.toJson(product));
                            variationValues.put(WoodminContract.ProductEntry.COLUMN_ENABLE, 1);

                            productsValues.add(variationValues);

                        }



                    }

                    ContentValues[] productsValuesArray = new ContentValues[productsValues.size()];
                    productsValuesArray = productsValues.toArray(productsValuesArray);
                    int ordersRowsUpdated = getContext().getContentResolver().bulkInsert(WoodminContract.ProductEntry.CONTENT_URI, productsValuesArray);
                    Log.v(LOG_TAG, "Products " + ordersRowsUpdated + " updated");
                }

                if (pageProduct == 0 || (sizePageProduct * pageProduct) < sizeProducts) {
                    pageProduct++;
                    synchronizeBatchProducts(date);
                } else {
                    finalizeSyncProducts();
                }
            }

            @Override
            public void onFailure(Call<Products> call, Throwable t) {
                if (pageProduct == 0 || (sizePageProduct * pageProduct) < sizeProducts) {
                    pageProduct++;
                    synchronizeBatchProducts(date);
                } else {
                    finalizeSyncProducts();
                }
            }
        });

    }

    private void finalizeSyncProducts() {

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        /*
        String query = WoodminContract.ProductEntry.COLUMN_ENABLE + " = ?" ;
        String[] parameters = new String[]{ String.valueOf("0") };
        int rowsDeleted = getContext().getContentResolver().delete(WoodminContract.ProductEntry.CONTENT_URI,
                query,
                parameters);
        Log.d(LOG_TAG, "Products: " + rowsDeleted + " old records deleted.");
        */

        getContext().getContentResolver().notifyChange(WoodminContract.ProductEntry.CONTENT_URI, null, false);
        pageProduct = 0;
    }

    private void synchronizeOrders(final Date date) {
        Log.v(LOG_TAG, "Orders sync start");

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_media_play);
        Notification notification = new NotificationCompat.Builder(getContext())
            .setProgress(100, 0, false)
            .setContentTitle(getContext().getString(R.string.sync_orders))
            .setContentText(getContext().getString(R.string.sync_count, 0, 0))
            /*
            .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(getContext().getString(R.string.sync_orders)))
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setVibrate(new long[]{0, 400})
             */
            .setSound(uri)
            .setSmallIcon(R.drawable.ic_media_play)
            .setLargeIcon(bm)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .build();
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        if(date == null) {
            Call<Count> call = woocommerceApi.countOrders();
            call.enqueue(new Callback<Count>() {
                @Override
                public void onResponse(Call<Count> call, retrofit2.Response<Count> response) {
                    int statusCode = response.code();
                    if (statusCode == 200) {
                        try {
                            sizeOrders = Integer.valueOf(response.body().getCount());
                        } catch (NumberFormatException exception) {
                            Log.e(LOG_TAG, "NumberFormatException " + exception.getMessage());
                        }

                        /*
                        ContentValues values = new ContentValues();
                        values.put(WoodminContract.OrdersEntry.COLUMN_ENABLE, 0);
                        int ordersRowsDisabled = getContext().getContentResolver().update(WoodminContract.OrdersEntry.CONTENT_URI, values, null, null);
                        Log.v(LOG_TAG, "Orders " + ordersRowsDisabled + " disabled");
                        Log.v(LOG_TAG, "Orders " + sizeOrders + " to sync");
                        */

                        synchronizeBatchOrders(date);
                    }
                }

                @Override
                public void onFailure(Call<Count> call, Throwable t) {

                }
            });

        } else {
            synchronizeBatchOrders(date);
        }
    }

    private void synchronizeBatchOrders(final Date date) {
        Log.v(LOG_TAG,"Orders Read Total:" + sizeOrders + " Current: " + pageOrder * sizePageOrders + " Page : " + pageOrder);

        HashMap<String, String> options = new HashMap<>();
        options.put("status", "any");
        options.put("filter[limit]", String.valueOf(sizePageOrders));
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            options.put("filter[updated_at_min]", dateFormat.format(date));
        }
        options.put("page", String.valueOf(pageOrder));

        Call<Orders> call = woocommerceApi.getOrders(options);
        call.enqueue(new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, retrofit2.Response<Orders> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    Log.v(LOG_TAG,"Success page Order " + pageOrder);

                    //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_media_play);
                    Notification notification = new NotificationCompat.Builder(getContext())
                        .setProgress(sizeOrders, (sizePageOrders * pageOrder), false)
                        .setContentTitle(getContext().getString(R.string.sync_orders))
                        .setContentText(getContext().getString(R.string.sync_count, (sizePageOrders * pageOrder), sizeOrders))
                        /*
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(getContext().getString(R.string.sync_orders)))
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setVibrate(new long[]{0, 400})
                        .setSound(uri)
                        */
                        .setSmallIcon(R.drawable.ic_media_play)
                        .setLargeIcon(bm)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .build();
                    NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, notification);

                    ArrayList<ContentValues> ordersValues = new ArrayList<>();
                    for(Order order:response.body().getOrders()) {

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
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_JSON,gson.toJson(order));
                        orderValues.put(WoodminContract.OrdersEntry.COLUMN_ENABLE, 1);

                        ordersValues.add(orderValues);

                    }

                    ContentValues[] ordersValuesArray = new ContentValues[ordersValues.size()];
                    ordersValuesArray = ordersValues.toArray(ordersValuesArray);
                    int ordersRowsUpdated = getContext().getContentResolver().bulkInsert(WoodminContract.OrdersEntry.CONTENT_URI, ordersValuesArray);
                    Log.v(LOG_TAG,"Orders " + ordersRowsUpdated + " updated");
                }
                if (pageOrder == 0 || (sizePageOrders * pageOrder) < sizeOrders) {
                    pageOrder ++;
                    synchronizeBatchOrders(date);
                } else {
                    finalizeSyncOrders();
                }
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {
                if (pageOrder == 0 || (sizePageOrders * pageOrder) < sizeOrders) {
                    pageOrder ++;
                    synchronizeBatchOrders(date);
                } else {
                    finalizeSyncOrders();
                }
            }
        });

    }

    private void finalizeSyncOrders() {

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

        Utility.setPreferredLastSync(getContext(), System.currentTimeMillis());

        /*
        String query = WoodminContract.OrdersEntry.COLUMN_ENABLE + " = ?" ;
        String[] parameters = new String[]{ String.valueOf("0") };
        int rowsDeleted = getContext().getContentResolver().delete(WoodminContract.OrdersEntry.CONTENT_URI,
                query,
                parameters);
        Log.d(LOG_TAG, "Orders: " + rowsDeleted + " old records deleted.");
        */

        getContext().getContentResolver().notifyChange(WoodminContract.OrdersEntry.CONTENT_URI, null, false);
        pageOrder = 0;
    }

    private void synchronizeShop() {
        Log.v(LOG_TAG, "Shop sync start");
        Call<Shop> call = woocommerceApi.getShop();
        call.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, retrofit2.Response<Shop> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    Log.v(LOG_TAG, "Shop sync success");

                    int shopRowsDeleted = getContext().getContentResolver().delete(WoodminContract.ShopEntry.CONTENT_URI, null, null);
                    Log.v(LOG_TAG, shopRowsDeleted + " Shop rows deleted");

                    ContentValues shopValues = new ContentValues();
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_NAME, response.body().getStore().getName());
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_DESCRIPTION, response.body().getStore().getDescription());
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_URL, response.body().getStore().getUrl());
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_WC_VERSION, response.body().getStore().getWcVersion());
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_META_CURRENCY, response.body().getStore().getMeta().getCurrency());
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_META_CURRENCY_FORMAT, response.body().getStore().getMeta().getCurrencyFormat());
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_META_DIMENSION_UNIT, response.body().getStore().getMeta().getDimensionUnit());
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_META_TAXI_INCLUDE, response.body().getStore().getMeta().isTaxIncluded() ? "1" : "0");
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_META_TIMEZONE, response.body().getStore().getMeta().getTimezone());
                    shopValues.put(WoodminContract.ShopEntry.COLUMN_META_WEIGHT_UNIT, response.body().getStore().getMeta().getWeightUnit());

                    Uri insertedShopUri = getContext().getContentResolver().insert(WoodminContract.ShopEntry.CONTENT_URI, shopValues);
                    long shopId = ContentUris.parseId(insertedShopUri);
                    Log.d(LOG_TAG, "Shop successful inserted ID: " + shopId);
                }
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {

            }
        });

    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public static Account getSyncAccount(Context context) {

        String user = Utility.getPreferredUser(context);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = new Account("Woodmin", context.getString(R.string.sync_account_type));
        if ( accountManager.getPassword(account) == null  ) {
            String secret = Utility.getPreferredSecret(context);
            if (!accountManager.addAccountExplicitly(account, secret, null)) {
                return null;
            }
            onAccountCreated(account, context);
        }
        return account;

    }

    public static void removeAccount(Context context){
        String user = Utility.getPreferredUser(context);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = new Account("Woodmin", context.getString(R.string.sync_account_type));
        if ( accountManager.getPassword(account) != null  ) {
            String secret = Utility.getPreferredSecret(context);
            accountManager.removeAccount(account,null,null);
        }
    }

    public static void disablePeriodSync(Context context){
        Log.e(LOG_TAG, "disablePeriodSync");

        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        ContentResolver.cancelSync(account, authority);

        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        accountManager.removeAccount(account, null, null);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        WoodminSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle())
                    .build();
            ContentResolver.requestSync(request);
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(final Context context) {

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);

    }

}
