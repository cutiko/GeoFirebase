package cl.cutiko.geopods.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import cl.cutiko.geopods.models.GeoPod;

/**
 * Created by cutiko on 17-08-17.
 */

public class GeoPodsAdapter extends FirebaseIndexRecyclerAdapter<GeoPod, GeoPodsAdapter.PodsHolder> {


    public GeoPodsAdapter(Query index, DatabaseReference locations) {
        super(
                GeoPod.class,
                android.R.layout.simple_list_item_1,
                PodsHolder.class,
                index,
                locations
        );
    }

    @Override
    protected void populateViewHolder(PodsHolder viewHolder, GeoPod model, int position) {
        TextView textView = (TextView) viewHolder.itemView;
        textView.setText(model.getName());
    }

    public static class PodsHolder extends RecyclerView.ViewHolder {
        public PodsHolder(View itemView) {
            super(itemView);
        }
    }

}
