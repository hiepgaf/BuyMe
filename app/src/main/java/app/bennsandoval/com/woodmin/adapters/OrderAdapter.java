package app.bennsandoval.com.woodmin.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Locale;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.models.orders.Item;
import app.bennsandoval.com.woodmin.models.orders.Order;

public class OrderAdapter extends CursorRecyclerViewAdapter<OrderAdapter.ViewHolder>  {

    private final String LOG_TAG = OrderAdapter.class.getSimpleName();

    private Context mContext;
    private int mLayoutResourceId;
    private View.OnClickListener mOnClickListener;

    public OrderAdapter(Context context, int layoutResourceId, Cursor cursor, View.OnClickListener onClickListener){
        super(context,cursor);
        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mOnClickListener = onClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout lyHeader;
        private TextView txtOrder;
        private TextView txtPrice;
        private TextView txtStatus;
        private TextView txtCustomer;
        private TextView txtItems;
        private TextView txtDate;

        public ViewHolder(View view) {
            super(view);
            lyHeader = (LinearLayout) view.findViewById(R.id.header);
            txtOrder = (TextView) view.findViewById(R.id.order);
            txtPrice = (TextView) view.findViewById(R.id.price);
            txtStatus = (TextView) view.findViewById(R.id.status);
            txtCustomer = (TextView) view.findViewById(R.id.customer);
            txtItems = (TextView) view.findViewById(R.id.items);
            txtDate = (TextView) view.findViewById(R.id.date);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutResourceId, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        String json = cursor.getString(cursor.getColumnIndexOrThrow(WoodminContract.OrdersEntry.COLUMN_JSON));
        String timestampCreatedAt = cursor.getString(cursor.getColumnIndexOrThrow(WoodminContract.OrdersEntry.COLUMN_CREATED_AT));
        Log.d(LOG_TAG, "Timestamp " + timestampCreatedAt);

        if(json!=null) {
            Gson gson = new GsonBuilder().create();
            Order order = gson.fromJson(json, Order.class);

            if(order.getStatus().toUpperCase().equals("COMPLETED")){
                holder.lyHeader.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else if(order.getStatus().toUpperCase().equals("CANCELLED") || order.getStatus().toUpperCase().equals("REFUNDED")){
                holder.lyHeader.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                holder.lyHeader.setBackgroundColor(mContext.getResources().getColor(R.color.orange));
                holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.orange));
            }
            holder.txtOrder.setText(mContext.getString(R.string.order, order.getOrderNumber()));
            holder.txtPrice.setText("$" + order.getTotal());
            holder.txtStatus.setText(order.getStatus().toUpperCase());

            if(order.getCustomer() != null && order.getCustomer().getFirstName() != null && order.getCustomer().getFirstName().length() > 0){
                holder.txtCustomer.setText(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
            } else if(order.getBillingAddress() != null && order.getBillingAddress().getFirstName() != null && order.getBillingAddress().getFirstName().length() > 0){
                holder.txtCustomer.setText(order.getBillingAddress().getFirstName() + " " + order.getBillingAddress().getLastName());
            } else {
                holder.txtCustomer.setText(mContext.getString(R.string.guest));
            }
            int itemsCount = 0;
            for (Item item:order.getItems()) {
                itemsCount += item.getQuantity();
            }
            holder.txtItems.setText(mContext.getString(R.string.items, itemsCount));

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.getDefault());
            holder.txtDate.setText(format.format(order.getCreatedAt()));
        }
    }

}
