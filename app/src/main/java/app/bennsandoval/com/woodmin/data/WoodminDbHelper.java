package app.bennsandoval.com.woodmin.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mackbook on 12/23/14.
 */
public class WoodminDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "woodmin.db";

    public WoodminDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_SHOP_TABLE = "CREATE TABLE " + WoodminContract.ShopEntry.TABLE_NAME + " (" +
                WoodminContract.ShopEntry._ID + " INTEGER PRIMARY KEY, " +
                WoodminContract.ShopEntry.COLUMN_NAME + " TEXT, " +
                WoodminContract.ShopEntry.COLUMN_DESCRIPTION + " TEXT, " +
                WoodminContract.ShopEntry.COLUMN_URL + " TEXT, " +
                WoodminContract.ShopEntry.COLUMN_WC_VERSION + " TEXT, " +
                WoodminContract.ShopEntry.COLUMN_META_TIMEZONE + " TEXT, " +
                WoodminContract.ShopEntry.COLUMN_META_CURRENCY + " TEXT, " +
                WoodminContract.ShopEntry.COLUMN_META_CURRENCY_FORMAT + " TEXT, " +
                WoodminContract.ShopEntry.COLUMN_META_TAXI_INCLUDE + " INTEGER DEFAULT 0 NOT NULL, " +
                WoodminContract.ShopEntry.COLUMN_META_WEIGHT_UNIT + " TEXT, " +
                WoodminContract.ShopEntry.COLUMN_META_DIMENSION_UNIT + " TEXT);";

        final String SQL_CREATE_ORDER_TABLE = "CREATE TABLE " + WoodminContract.OrdersEntry.TABLE_NAME + " (" +
                WoodminContract.OrdersEntry._ID + " INTEGER PRIMARY KEY, " +
                WoodminContract.OrdersEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                WoodminContract.OrdersEntry.COLUMN_ORDER_NUMBER + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                WoodminContract.OrdersEntry.COLUMN_UPDATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                WoodminContract.OrdersEntry.COLUMN_COMPLETED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                WoodminContract.OrdersEntry.COLUMN_STATUS + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CURRENCY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_TOTAL + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SUBTOTAL + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_TOTAL_LINE_ITEMS_QUANTITY + " INTEGER, " +
                WoodminContract.OrdersEntry.COLUMN_TOTAL_TAX + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_TOTAL_SHIPPING + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CART_TAX + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_TAX + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_TOTAL_DISCOUNT + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CART_DISCOUNT + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_ORDER_DISCOUNT + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_METHODS + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_NOTE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_VIEW_ORDER_URL + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_ID + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_METHOD_TITLE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_PAYMENT_DETAILS_PAID + " INTEGER DEFAULT 0 NOT NULL, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_FIRST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_LAST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_COMPANY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_ADDRESS_1 + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_ADDRESS_2 + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_CITY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_STATE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_POSTCODE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_COUNTRY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_EMAIL + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_BILLING_PHONE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_FIRST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_LAST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_COMPANY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_1 + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_ADDRESS_2 + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_CITY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_STATE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_POSTCODE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_SHIPPING_COUNTRY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_ID + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_EMAIL + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_FIRST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_LAST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_USERNAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_ID + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_LAST_ORDER_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_ORDERS_COUNT + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_TOTAL_SPEND + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_AVATAR_URL + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_FIRST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_LAST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COMPANY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_1 + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_ADDRESS_2 + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_CITY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_STATE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_POSTCODE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_COUNTRY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_EMAIL + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_PHONE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_FIRST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_LAST_NAME + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COMPANY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_1 + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_ADDRESS_2 + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_CITY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_STATE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_POSTCODE + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_CUSTOMER_SHIPPING_COUNTRY + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_JSON + " TEXT, " +
                WoodminContract.OrdersEntry.COLUMN_ENABLE + " INTEGER);";

        final String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + WoodminContract.ProductEntry.TABLE_NAME + " (" +
                WoodminContract.ProductEntry._ID + " INTEGER PRIMARY KEY, " +
                WoodminContract.ProductEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                WoodminContract.ProductEntry.COLUMN_TITLE + " TEXT, " +
                WoodminContract.ProductEntry.COLUMN_SKU + " TEXT, " +
                WoodminContract.ProductEntry.COLUMN_PRICE + " TEXT, " +
                WoodminContract.ProductEntry.COLUMN_STOCK + " INTEGER, " +
                WoodminContract.ProductEntry.COLUMN_JSON + " TEXT, " +
                WoodminContract.ProductEntry.COLUMN_ENABLE + " INTEGER);";

        final String SQL_CREATE_CONSUMER_TABLE = "CREATE TABLE " + WoodminContract.CustomerEntry.TABLE_NAME + " (" +
                WoodminContract.CustomerEntry._ID + " INTEGER PRIMARY KEY, " +
                WoodminContract.CustomerEntry.COLUMN_ID + " INTEGER NOT NULL UNIQUE, " +
                WoodminContract.CustomerEntry.COLUMN_EMAIL + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_FIRST_NAME + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_LAST_NAME + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_SHIPPING_FIRST_NAME + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_SHIPPING_LAST_NAME + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_SHIPPING_PHONE + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_BILLING_FIRST_NAME + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_BILLING_LAST_NAME + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_BILLING_PHONE + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_USERNAME + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_LAST_ORDER_ID + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_JSON + " TEXT, " +
                WoodminContract.CustomerEntry.COLUMN_ENABLE + " INTEGER);";

        sqLiteDatabase.execSQL(SQL_CREATE_SHOP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ORDER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CONSUMER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WoodminContract.ShopEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WoodminContract.OrdersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WoodminContract.ProductEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WoodminContract.CustomerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
