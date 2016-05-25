package app.bennsandoval.com.woodmin.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WoodminContract {

    // Match <string name="content_authority" translatable="false">com.woodmin.app</string>
    public static final String CONTENT_AUTHORITY = "com.woodmin.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // For instance, content://com.woodmin.app/shop/
    public static final String PATH_SHOP = "shop";
    // For instance, content://com.woodmin.app/order/
    public static final String PATH_ORDER = "order";
    // For instance, content://com.woodmin.app/product/
    public static final String PATH_PRODUCT = "product";
    // For instance, content://com.woodmin.app/customer/
    public static final String PATH_CUSTOMER = "customer";

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static String getDbDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static Date getDateFromDbString(String dateText) throws ParseException {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dbDateFormat.parse(dateText);
    }

    public static final class ShopEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHOP).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_SHOP;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_SHOP;

        // Table name
        public static final String TABLE_NAME = "shop";

        //Fields
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_URL = "URL";
        public static final String COLUMN_WC_VERSION = "wc_version";
        public static final String COLUMN_META_TIMEZONE = "timezone";
        public static final String COLUMN_META_CURRENCY = "currency";
        public static final String COLUMN_META_CURRENCY_FORMAT = "currency_format";
        public static final String COLUMN_META_TAXI_INCLUDE = "tax_included";
        public static final String COLUMN_META_WEIGHT_UNIT = "weight_unit";
        public static final String COLUMN_META_DIMENSION_UNIT = "dimension_unit";

        public static Uri buildShopUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class OrdersEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDER).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;

        // Table name
        public static final String TABLE_NAME = "orders";

        //Fields
        public static final String COLUMN_ID = "woocommerce_id";
        public static final String COLUMN_ORDER_NUMBER = "order_number";

        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
        public static final String COLUMN_COMPLETED_AT = "completed_at";

        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_CURRENCY = "currency";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_SUBTOTAL = "subtotal";
        public static final String COLUMN_TOTAL_LINE_ITEMS_QUANTITY = "total_line_items_quantity";
        public static final String COLUMN_TOTAL_TAX = "total_tax";
        public static final String COLUMN_TOTAL_SHIPPING = "total_shipping";
        public static final String COLUMN_CART_TAX = "cart_tax";
        public static final String COLUMN_SHIPPING_TAX = "shipping_tax";
        public static final String COLUMN_TOTAL_DISCOUNT = "total_discount";
        public static final String COLUMN_CART_DISCOUNT = "cart_discount";
        public static final String COLUMN_ORDER_DISCOUNT = "order_discount";
        public static final String COLUMN_SHIPPING_METHODS = "shipping_methods";

        public static final String COLUMN_NOTE = "note";
        public static final String COLUMN_VIEW_ORDER_URL = "view_order_url";

        public static final String COLUMN_PAYMENT_DETAILS_METHOD_ID = "method_id";
        public static final String COLUMN_PAYMENT_DETAILS_METHOD_TITLE = "method_title";
        public static final String COLUMN_PAYMENT_DETAILS_PAID = "paid";

        public static final String COLUMN_BILLING_FIRST_NAME = "billing_first_name";
        public static final String COLUMN_BILLING_LAST_NAME = "billing_last_name";
        public static final String COLUMN_BILLING_COMPANY = "billing_company";
        public static final String COLUMN_BILLING_ADDRESS_1 = "billing_address_1";
        public static final String COLUMN_BILLING_ADDRESS_2 = "billing_address_2";
        public static final String COLUMN_BILLING_CITY = "billing_city";
        public static final String COLUMN_BILLING_STATE = "billing_state";
        public static final String COLUMN_BILLING_POSTCODE = "billing_postcode";
        public static final String COLUMN_BILLING_COUNTRY = "billing_country";
        public static final String COLUMN_BILLING_EMAIL = "billing_email";
        public static final String COLUMN_BILLING_PHONE = "billing_phone";

        public static final String COLUMN_SHIPPING_FIRST_NAME = "shipping_first_name";
        public static final String COLUMN_SHIPPING_LAST_NAME = "shipping_last_name";
        public static final String COLUMN_SHIPPING_COMPANY = "shipping_company";
        public static final String COLUMN_SHIPPING_ADDRESS_1 = "shipping_address_1";
        public static final String COLUMN_SHIPPING_ADDRESS_2 = "shipping_address_2";
        public static final String COLUMN_SHIPPING_CITY = "shipping_city";
        public static final String COLUMN_SHIPPING_STATE = "shipping_state";
        public static final String COLUMN_SHIPPING_POSTCODE = "shipping_postcode";
        public static final String COLUMN_SHIPPING_COUNTRY = "shipping_country";

        public static final String COLUMN_CUSTOMER_ID = "customer_id";

        public static final String COLUMN_CUSTOMER_EMAIL = "customer_email";
        public static final String COLUMN_CUSTOMER_FIRST_NAME = "customer_first_name";
        public static final String COLUMN_CUSTOMER_LAST_NAME = "customer_last_name";
        public static final String COLUMN_CUSTOMER_USERNAME = "customer_username";
        public static final String COLUMN_CUSTOMER_LAST_ORDER_ID = "customer_last_order_id";
        public static final String COLUMN_CUSTOMER_LAST_ORDER_DATE = "customer_last_order_date";
        public static final String COLUMN_CUSTOMER_ORDERS_COUNT = "customer_orders_count";
        public static final String COLUMN_CUSTOMER_TOTAL_SPEND = "customer_total_spent";
        public static final String COLUMN_CUSTOMER_AVATAR_URL = "customer_avatar_url";

        public static final String COLUMN_CUSTOMER_BILLING_FIRST_NAME = "customer_billing_first_name";
        public static final String COLUMN_CUSTOMER_BILLING_LAST_NAME = "customer_billing_last_name";
        public static final String COLUMN_CUSTOMER_BILLING_COMPANY = "customer_billing_company";
        public static final String COLUMN_CUSTOMER_BILLING_ADDRESS_1 = "customer_billing_address_1";
        public static final String COLUMN_CUSTOMER_BILLING_ADDRESS_2 = "customer_billing_address_2";
        public static final String COLUMN_CUSTOMER_BILLING_CITY = "customer_billing_city";
        public static final String COLUMN_CUSTOMER_BILLING_STATE = "customer_billing_state";
        public static final String COLUMN_CUSTOMER_BILLING_POSTCODE = "customer_billing_postcode";
        public static final String COLUMN_CUSTOMER_BILLING_COUNTRY = "customer_billing_country";
        public static final String COLUMN_CUSTOMER_BILLING_EMAIL = "customer_billing_email";
        public static final String COLUMN_CUSTOMER_BILLING_PHONE = "customer_billing_phone";

        public static final String COLUMN_CUSTOMER_SHIPPING_FIRST_NAME = "customer_shipping_first_name";
        public static final String COLUMN_CUSTOMER_SHIPPING_LAST_NAME = "customer_shipping_last_name";
        public static final String COLUMN_CUSTOMER_SHIPPING_COMPANY = "customer_shipping_company";
        public static final String COLUMN_CUSTOMER_SHIPPING_ADDRESS_1 = "customer_shipping_address_1";
        public static final String COLUMN_CUSTOMER_SHIPPING_ADDRESS_2 = "customer_shipping_address_2";
        public static final String COLUMN_CUSTOMER_SHIPPING_CITY = "customer_shipping_city";
        public static final String COLUMN_CUSTOMER_SHIPPING_STATE = "customer_shipping_state";
        public static final String COLUMN_CUSTOMER_SHIPPING_POSTCODE = "customer_shipping_postcode";
        public static final String COLUMN_CUSTOMER_SHIPPING_COUNTRY = "customer_shipping_country";

        public static final String COLUMN_JSON = "json";
        public static final String COLUMN_ENABLE = "enable";

        public static Uri buildOrderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCT).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        // Table name
        public static final String TABLE_NAME = "product";

        //Fields
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_SKU = "sku";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_STOCK = "stock";

        public static final String COLUMN_JSON = "json";
        public static final String COLUMN_ENABLE = "enable";

        public static Uri buildOrderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class CustomerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMER).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CUSTOMER;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_CUSTOMER;

        // Table name
        public static final String TABLE_NAME = "customer";

        //Fields
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_LAST_ORDER_ID = "last_order_id";

        public static final String COLUMN_SHIPPING_FIRST_NAME = "shipping_first_name";
        public static final String COLUMN_SHIPPING_LAST_NAME = "shipping_last_name";
        public static final String COLUMN_SHIPPING_PHONE = "shipping_phone";

        public static final String COLUMN_BILLING_FIRST_NAME = "billing_first_name";
        public static final String COLUMN_BILLING_LAST_NAME = "billing_last_name";
        public static final String COLUMN_BILLING_PHONE = "billing_phone";

        public static final String COLUMN_JSON = "json";
        public static final String COLUMN_ENABLE = "enable";

        public static Uri buildOrderUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
