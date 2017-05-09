package projectmanager.dada.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.amap.api.services.core.PoiItem;
import java.util.List;
import projectmanager.dada.R;

/**
 * Created by tao on 2016/12/12.
 */

public class LocationAdapter extends ArrayAdapter<PoiItem> {

    int resource;

    public LocationAdapter(Context context, int resource, List<PoiItem> tipList) {
        super(context, resource, tipList);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PoiItem tip = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resource, null);//子项的view
        TextView name = (TextView) view.findViewById(R.id.locatin_name);
        TextView address = (TextView) view.findViewById(R.id.locatin_address);
        name.setText(tip.getTitle());
        address.setText(tip.getSnippet());
        return view;
    }
}
