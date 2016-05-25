package app.bennsandoval.com.woodmin.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.interfaces.CustomerActions;
import app.bennsandoval.com.woodmin.models.customers.Customer;

public class CustomerAdapter extends CursorRecyclerViewAdapter<CustomerAdapter.ViewHolder>  {

    private Context mContext;
    private int mLayoutResourceId;
    private View.OnClickListener mOnClickListener;
    private CustomerActions mActionsListener;

    public CustomerAdapter(Context context, int layoutResourceId, Cursor cursor, View.OnClickListener onClickListener, CustomerActions actionsListener){
        super(context,cursor);
        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
        this.mOnClickListener = onClickListener;
        this.mActionsListener = actionsListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private TextView txtSpend;
        private TextView txtEmail;
        private TextView txtPhone;

        private LinearLayout lyPhone;
        private LinearLayout lyEmail;

        public ViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.name);
            txtSpend = (TextView) view.findViewById(R.id.spend);
            txtEmail = (TextView) view.findViewById(R.id.email);
            txtPhone = (TextView) view.findViewById(R.id.phone);

            lyPhone = (LinearLayout) view.findViewById(R.id.call_button);
            lyEmail = (LinearLayout) view.findViewById(R.id.email_button);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutResourceId, parent, false);
        itemView.setOnClickListener(mOnClickListener);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        String json = cursor.getString(cursor.getColumnIndexOrThrow(WoodminContract.CustomerEntry.COLUMN_JSON));
        if(json!=null) {
            Gson gson = new GsonBuilder().create();
            final Customer customer = gson.fromJson(json, Customer.class);

            if(customer.getBillingAddress() != null){
                StringBuilder builder = new StringBuilder();
                builder.append(customer.getBillingAddress().getFirstName());
                builder.append(" ");
                builder.append(customer.getBillingAddress().getLastName());

                holder.txtName.setText(builder.toString().toUpperCase());
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append(customer.getFirstName());
                builder.append(" ");
                builder.append(customer.getLastName());
                holder.txtName.setText(builder.toString().toUpperCase());
            }

            if(holder.txtName.getText().length() == 1){
                holder.txtName.setText(mContext.getString(R.string.guest));
            }

            holder.txtSpend.setText("$" + customer.getTotalSpent());

            if(customer.getEmail() != null && customer.getEmail().length() > 1){
                holder.txtEmail.setText(customer.getEmail());
                holder.lyEmail.setVisibility(View.VISIBLE);
                holder.lyEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActionsListener.sendEmail(customer);
                    }
                });
            } else {
                holder.lyEmail.setVisibility(View.GONE);
            }

            if(customer.getBillingAddress() != null &&
                    customer.getBillingAddress().getPhone() != null &&
                    customer.getBillingAddress().getPhone().length() > 1){
                holder.txtPhone.setText(customer.getBillingAddress().getPhone());
                holder.lyPhone.setVisibility(View.VISIBLE);
                holder.lyPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActionsListener.makeACall(customer);
                    }
                });
            } else {
                holder.txtPhone.setText("");
                holder.lyPhone.setVisibility(View.GONE);
            }
        }
    }
}
