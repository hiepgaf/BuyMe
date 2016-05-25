package app.bennsandoval.com.woodmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.models.orders.DrawerOption;

public class DrawerAdapter extends ArrayAdapter<DrawerOption> {

    private final Context context;
    private final DrawerOption[] values;

    public DrawerAdapter(Context context, DrawerOption[] values) {

        super(context, R.layout.fragment_navigation_drawer_list_item, values);
        this.context = context;
        this.values = values;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.fragment_navigation_drawer_list_item, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        TextView countView = (TextView) rowView.findViewById(R.id.count);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position].getSection());
        if(values[position].getCount() > -1){
            countView.setText(String.valueOf(values[position].getCount()));
        }
        imageView.setImageResource(values[position].getIcon());

        return rowView;
    }

}
