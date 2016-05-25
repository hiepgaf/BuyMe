package app.bennsandoval.com.woodmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.models.Resume;

public class ResumeAdapter extends ArrayAdapter<Resume> {

    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<Resume> mData = null;

    public ResumeAdapter(Context context, int layoutResourceId, ArrayList<Resume> data) {
        super(context, layoutResourceId, data);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(mLayoutResourceId, parent, false);

            holder = new ItemHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.header);
            holder.txtfield11 = (TextView) row.findViewById(R.id.field1_1);
            holder.txtfield12 = (TextView) row.findViewById(R.id.field1_2);
            holder.txtfield13 = (TextView) row.findViewById(R.id.field1_3);
            holder.txtfield21 = (TextView) row.findViewById(R.id.field2_1);
            holder.txtfield22 = (TextView) row.findViewById(R.id.field2_2);
            holder.txtfield23 = (TextView) row.findViewById(R.id.field2_3);
            holder.txtfield31 = (TextView) row.findViewById(R.id.field3_1);
            holder.txtfield32 = (TextView) row.findViewById(R.id.field3_2);
            holder.txtfield33 = (TextView) row.findViewById(R.id.field3_3);

            row.setTag(holder);
        } else {
            holder = (ItemHolder) row.getTag();
        }

        Resume resume = mData.get(position);
        holder.txtTitle.setText(resume.getTitle());

        if(resume.getData().size() >= 1 ) {
            holder.txtfield11.setVisibility(View.VISIBLE);
            holder.txtfield12.setVisibility(View.VISIBLE);
            holder.txtfield13.setVisibility(View.VISIBLE);

            holder.txtfield11.setText(resume.getData().get(0).getField1());
            holder.txtfield12.setText(resume.getData().get(0).getField2());
            holder.txtfield13.setText(resume.getData().get(0).getField3());
        } else {
            holder.txtfield11.setVisibility(View.GONE);
            holder.txtfield12.setVisibility(View.GONE);
            holder.txtfield13.setVisibility(View.GONE);
        }

        if(resume.getData().size() >= 2 ) {
            holder.txtfield21.setVisibility(View.VISIBLE);
            holder.txtfield22.setVisibility(View.VISIBLE);
            holder.txtfield23.setVisibility(View.VISIBLE);

            holder.txtfield21.setText(resume.getData().get(1).getField1());
            holder.txtfield22.setText(resume.getData().get(1).getField2());
            holder.txtfield23.setText(resume.getData().get(1).getField3());
        } else {
            holder.txtfield21.setVisibility(View.GONE);
            holder.txtfield22.setVisibility(View.GONE);
            holder.txtfield23.setVisibility(View.GONE);
        }

        if(resume.getData().size() >= 3 ){
            holder.txtfield31.setVisibility(View.VISIBLE);
            holder.txtfield32.setVisibility(View.VISIBLE);
            holder.txtfield33.setVisibility(View.VISIBLE);

            holder.txtfield31.setText(resume.getData().get(2).getField1());
            holder.txtfield32.setText(resume.getData().get(2).getField2());
            holder.txtfield33.setText(resume.getData().get(2).getField3());
        } else {
            holder.txtfield31.setVisibility(View.GONE);
            holder.txtfield32.setVisibility(View.GONE);
            holder.txtfield33.setVisibility(View.GONE);
        }

        if(resume.getTitle().toUpperCase().equals("STOCK WARNINGS")){
            holder.txtTitle.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            //holder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            holder.txtTitle.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
            //holder.txtTitle.setTextColor(mContext.getResources().getColor(R.color.primary));
        }

        return row;
    }

    public static class ItemHolder {
        private TextView txtTitle;
        private TextView txtfield11;
        private TextView txtfield12;
        private TextView txtfield13;
        private TextView txtfield21;
        private TextView txtfield22;
        private TextView txtfield23;
        private TextView txtfield31;
        private TextView txtfield32;
        private TextView txtfield33;

    }

}
