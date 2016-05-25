package app.bennsandoval.com.woodmin.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.activities.OrderDetail;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.models.customers.Customer;
import app.bennsandoval.com.woodmin.models.orders.Order;

/**
 * Created by Mackbook on 1/10/15.
 */
public class HeadInfoService extends Service {

    private final String LOG_TAG = HeadInfoService.class.getSimpleName();

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mContentRelativeLayoutParams;

    private boolean mOpen = true;
    private boolean mDestroy = false;

    private float mScreenDensity;
    private int mScreenWidthInPixels;
    private int mScreenHeightInPixels;

    private LinearLayout mHeadInfo;
    private LinearLayout mInfo;
    private LinearLayout mDestroyLayout;

    private int destroyLayoutPositionYInPixels;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null){

            String search = intent.getStringExtra("search");

            Gson gson = new GsonBuilder().create();

            //Search in orders by number
            String[] projectionOrders = {
                    WoodminContract.OrdersEntry.COLUMN_JSON,
            };
            String selectionOrder = WoodminContract.OrdersEntry.COLUMN_BILLING_PHONE+ " LIKE ? OR  " +
                    WoodminContract.OrdersEntry.COLUMN_CUSTOMER_BILLING_PHONE + " LIKE ?" ;
            String[] selectionOrderArgs = new String[]{ "%"+search+"%",
                    "%"+search+"%"};
            Cursor cursorOrder = getContentResolver().query(WoodminContract.OrdersEntry.CONTENT_URI,
                    projectionOrders,
                    selectionOrder,
                    selectionOrderArgs,
                    null);

            //Search in customers by number
            String[] projectionCustomer = {
                    WoodminContract.CustomerEntry.COLUMN_JSON,
            };
            String selectionCustomer = WoodminContract.CustomerEntry.COLUMN_SHIPPING_PHONE+ " LIKE ? OR  " +
                    WoodminContract.CustomerEntry.COLUMN_BILLING_PHONE + " LIKE ?" ;
            String[] selectionCustomerArgs = new String[]{ "%"+search+"%",
                    "%"+search+"%"};

            Cursor cursorCustomer = getContentResolver().query(WoodminContract.CustomerEntry.CONTENT_URI,
                    projectionCustomer,
                    selectionCustomer,
                    selectionCustomerArgs,
                    null);

            if(mHeadInfo == null && cursorCustomer!= null && cursorCustomer.getCount() > 0){
                mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

                DisplayMetrics metrics = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(metrics);

                mScreenHeightInPixels = metrics.heightPixels;
                mScreenWidthInPixels = metrics.widthPixels;
                mScreenDensity = metrics.density;
                //float dpHeight = mScreenHeightInPixels / mScreenDensity;
                //float dpWidth  = mScreenWidthInPixels / mScreenDensity;
                //int densityDpi = metrics.densityDpi;

                mHeadInfo = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.head_info_layout, null);
                mContentRelativeLayoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);

                mContentRelativeLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
                final int marginTopHeaderInDp = 0;
                final int marginLeftHeaderInDp = 0;
                mContentRelativeLayoutParams.x = (int) (marginLeftHeaderInDp * mScreenDensity);
                mContentRelativeLayoutParams.y = (int) (marginTopHeaderInDp * mScreenDensity);

                final int pixelsThreshold = (int) (150 * mScreenDensity); //150dp * density = pixels
                View.OnTouchListener motionListener = new View.OnTouchListener() {

                    private int initialX;
                    private int initialY;
                    private float initialTouchX;
                    private float initialTouchY;

                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                initialX = mContentRelativeLayoutParams.x;
                                initialY = mContentRelativeLayoutParams.y;
                                initialTouchX = event.getRawX();
                                initialTouchY = event.getRawY();
                                return true;
                            case MotionEvent.ACTION_UP:
                                if(mDestroy){
                                    stopSelf();
                                } else {
                                    float absoluteDistance = initialTouchX - event.getRawX();
                                    if(absoluteDistance < 0){
                                        absoluteDistance *= -1;
                                    }
                                    if(absoluteDistance == 0 && view.getId() == R.id.profile_container){
                                        if(mOpen){
                                            //TODO HIDE INFORMATION
                                            mInfo.setVisibility(View.GONE);
                                        } else {
                                            //TODO SHOW INFORMATION
                                            mInfo.setVisibility(View.VISIBLE);
                                            mContentRelativeLayoutParams.x = (int) (marginLeftHeaderInDp * mScreenDensity);
                                            mContentRelativeLayoutParams.y = (int) (marginTopHeaderInDp * mScreenDensity);
                                            mWindowManager.updateViewLayout(mHeadInfo, mContentRelativeLayoutParams);
                                        }
                                        mOpen=!mOpen;
                                    }
                                }
                                return true;
                            case MotionEvent.ACTION_MOVE:
                                int positionX = initialX + (int) (event.getRawX() - initialTouchX);
                                int positionY = initialY + (int) (event.getRawY() - initialTouchY);

                                if(positionY > 0 && positionY < mScreenHeightInPixels && positionX > 0 && positionX < mScreenWidthInPixels){
                                    if(mOpen){
                                        //TODO HIDE INFORMATION
                                        mInfo.setVisibility(View.GONE);
                                        mOpen = false;
                                    }
                                    float positionViewThresholdY = mHeadInfo.getHeight() + pixelsThreshold + positionY;
                                    //Log.v(getPackageName(),"Destroy Y " + destroyLayoutPositionYInPixels +" Position Y " + positionViewThresholdY);
                                    if(positionViewThresholdY  >= destroyLayoutPositionYInPixels){
                                        mDestroyLayout.setVisibility(View.VISIBLE);
                                        positionY = destroyLayoutPositionYInPixels;
                                        mDestroy = true;
                                    } else {
                                        mDestroyLayout.setVisibility(View.GONE);
                                        mDestroy = false;
                                    }

                                    mContentRelativeLayoutParams.x = positionX;
                                    mContentRelativeLayoutParams.y = positionY;
                                    mWindowManager.updateViewLayout(mHeadInfo, mContentRelativeLayoutParams);
                                }
                                return true;
                        }
                        return false;
                    }
                };
                LinearLayout profileContainer = (LinearLayout) mHeadInfo.findViewById(R.id.profile_container);
                profileContainer.setOnTouchListener(motionListener);

                //ADD INFO VIEW
                int marginLeftInDp = 10;
                int marginRightInDp = 10;
                int marginLeftAndRightInDp = marginLeftInDp + marginRightInDp;
                int marginTopInDp = marginTopHeaderInDp + 90;
                int marginBottomInDp = 50;
                int marginTopAnBottomInDp = marginTopInDp + marginBottomInDp;
                WindowManager.LayoutParams infoLayoutParams = new WindowManager.LayoutParams(
                        mScreenWidthInPixels - (int) (marginLeftAndRightInDp * mScreenDensity),
                        //WindowManager.LayoutParams.WRAP_CONTENT,
                        mScreenHeightInPixels -(int) (marginTopAnBottomInDp * mScreenDensity),
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                        PixelFormat.TRANSLUCENT);
                infoLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
                infoLayoutParams.x = (int) (marginLeftInDp * mScreenDensity);
                infoLayoutParams.y = (int) (marginTopInDp * mScreenDensity);

                mInfo = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.head_info_container_layout, null);
                mInfo.setVisibility(View.GONE);
                mWindowManager.addView(mInfo, infoLayoutParams);

                //ADD HEAD VIEW
                mWindowManager.addView(mHeadInfo, mContentRelativeLayoutParams);

                //ADD CLOSE VIEW
                mDestroyLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.head_info_close_layout, null);
                mDestroyLayout.setVisibility(View.GONE);
                WindowManager.LayoutParams destroyLayoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                        PixelFormat.TRANSLUCENT);

                destroyLayoutPositionYInPixels = mScreenHeightInPixels - mDestroyLayout.getHeight();
                Log.v(LOG_TAG, "Destroy Layout Y position: " + destroyLayoutPositionYInPixels);
                destroyLayoutParams.x = 0;
                destroyLayoutParams.y = destroyLayoutPositionYInPixels;
                mWindowManager.addView(mDestroyLayout, destroyLayoutParams);
            }

            if(mInfo != null && cursorCustomer!= null && cursorCustomer.getCount() > 0){
                LinearLayout listContainer = (LinearLayout) mInfo.findViewById(R.id.list);
                if(cursorCustomer.moveToFirst()) {
                    do {
                        String json = cursorCustomer.getString(0);
                        if(json!=null){
                            Customer customer = gson.fromJson(json, Customer.class);

                            View child =  LayoutInflater.from(this).inflate(R.layout.head_info_content, null);
                            TextView txtName = (TextView) child.findViewById(R.id.name);
                            TextView txtEmail = (TextView) child.findViewById(R.id.email);
                            TextView txtPhone = (TextView) child.findViewById(R.id.phone);

                            txtName.setText(customer.getFirstName() + " " + customer.getLastName());
                            txtEmail.setText(customer.getEmail());
                            if(customer.getBillingAddress()!= null &&
                                    customer.getBillingAddress().getPhone() != null &&
                                    customer.getBillingAddress().getPhone().length() > 1){

                                txtPhone.setText(customer.getBillingAddress().getPhone());

                            }

                            LinearLayout lyBilling = (LinearLayout) child.findViewById(R.id.billing);
                            if(customer.getBillingAddress()!= null ){
                                TextView addressOne = (TextView) child.findViewById(R.id.billing_address_one);
                                TextView addressTwo = (TextView) child.findViewById(R.id.billing_address_two);
                                String lineOne = customer.getBillingAddress().getAddressOne() + " " +
                                        customer.getBillingAddress().getAddressTwo() + " " +
                                        customer.getBillingAddress().getPostcode();
                                String lineTwo = customer.getBillingAddress().getCountry() + " " +
                                        customer.getBillingAddress().getState() + " " +
                                        customer.getBillingAddress().getCity();
                                addressOne.setText(lineOne);
                                addressTwo.setText(lineTwo);
                            } else {
                                lyBilling.setVisibility(View.GONE);
                            }

                            LinearLayout lyShipping = (LinearLayout) child.findViewById(R.id.shipping);
                            if(customer.getShippingAddress()!= null ){
                                TextView addressOne = (TextView) child.findViewById(R.id.shipping_address_one);
                                TextView addressTwo = (TextView) child.findViewById(R.id.shipping_address_two);
                                String lineOne = customer.getShippingAddress().getAddressOne() + " " +
                                        customer.getShippingAddress().getAddressTwo() + " " +
                                        customer.getShippingAddress().getPostcode();
                                String lineTwo = customer.getShippingAddress().getCountry() + " " +
                                        customer.getShippingAddress().getState() + " " +
                                        customer.getShippingAddress().getCity();
                                addressOne.setText(lineOne);
                                addressTwo.setText(lineTwo);
                            } else {
                                lyShipping.setVisibility(View.GONE);
                            }

                            LinearLayout contentLayout = (LinearLayout) child.findViewById(R.id.head_info_content);

                            //Search in orders by customer
                            String selectionOrderByCustomer = WoodminContract.OrdersEntry.COLUMN_CUSTOMER_ID + " = ?" ;
                            String[] selectionOrderByCustomerArgs = new String[]{ String.valueOf(customer.getId()) };
                            Cursor cursorOrderByCustomer = getContentResolver().query(WoodminContract.OrdersEntry.CONTENT_URI,
                                    projectionOrders,
                                    selectionOrderByCustomer,
                                    selectionOrderByCustomerArgs,
                                    null);
                            int ordersOfCustomer = cursorOrderByCustomer.getCount();
                            if(cursorOrderByCustomer.moveToFirst()) {
                                do {
                                    String jsonOrder = cursorOrderByCustomer.getString(0);
                                    if (jsonOrder != null) {
                                        final Order order = gson.fromJson(jsonOrder, Order.class);

                                        View orderViewChild =  LayoutInflater.from(this).inflate(R.layout.head_info_content_order_item, null);
                                        LinearLayout.LayoutParams orderViewChildParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f);
                                        orderViewChildParams.setMargins(0,0,0,(int) (5 * mScreenDensity));


                                        TextView txtOrder = (TextView) orderViewChild.findViewById(R.id.order);
                                        TextView txtStatus = (TextView) orderViewChild.findViewById(R.id.status);
                                        TextView txtItems = (TextView) orderViewChild.findViewById(R.id.items);
                                        TextView txtTotal = (TextView) orderViewChild.findViewById(R.id.total);
                                        TextView txtPayment = (TextView) orderViewChild.findViewById(R.id.payment);
                                        TextView txtDate = (TextView) orderViewChild.findViewById(R.id.date);

                                        txtOrder.setText(getString(R.string.order) + " " + order.getOrderNumber());
                                        txtStatus.setText(order.getStatus().toUpperCase());

                                        if(order.getStatus().toUpperCase().equals("COMPLETED")){
                                            txtStatus.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        } else if(order.getStatus().toUpperCase().equals("CANCELLED")){
                                            txtStatus.setTextColor(getResources().getColor(R.color.red));
                                        } else {
                                            txtStatus.setTextColor(getResources().getColor(R.color.orange));
                                        }

                                        txtItems.setText(String.valueOf(order.getItems().size()) + " " + getString(R.string.items));
                                        txtTotal.setText("$" + order.getTotal());
                                        txtPayment.setText(order.getPaymentDetails().getMethodTitle().toUpperCase());

                                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
                                        txtDate.setText(format.format(order.getCreatedAt()));

                                        orderViewChild.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent orderIntent = new Intent(getApplicationContext(), OrderDetail.class);
                                                orderIntent.putExtra("order", order.getId());
                                                orderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(orderIntent);

                                                mInfo.setVisibility(View.GONE);
                                                mOpen = false;

                                            }
                                        });

                                        contentLayout.addView(orderViewChild,orderViewChildParams);
                                    }
                                } while (cursorOrderByCustomer.moveToNext());
                            }

                            listContainer.addView(child);

                            //mCustomers.add(customer);
                        }
                    } while (cursorCustomer.moveToNext());
                }
            }

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHeadInfo != null) {
            mWindowManager.removeView(mHeadInfo);
            mWindowManager.removeView(mDestroyLayout);
        }
    }

}
