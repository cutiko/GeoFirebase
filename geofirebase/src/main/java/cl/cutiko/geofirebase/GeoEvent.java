package cl.cutiko.geofirebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cl.cutiko.geofirebase.models.GeoPod;

/**
 * Created by cutiko on 24-08-17.
 */

public abstract class GeoEvent<M extends GeoPod> implements ValueEventListener {

    private Class<M> mModelClass;
    private double minLat, maxLat, minLong, maxLong;

    public GeoEvent(double latitude, double longitude, double radius, Class<M> mModelClass, String dbRoute, String funnel) {
        minLat = latitude - radius;
        maxLat = latitude + radius;
        minLong = longitude - radius;
        maxLong = longitude + radius;
        this.mModelClass = mModelClass;
        nearBy(dbRoute, funnel);
    }


    private void nearBy(String dbRoute, String funnel) {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        root.child(dbRoute).child(funnel).orderByChild("latitude").startAt(minLat).endAt(maxLat).addListenerForSingleValueEvent(this);
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot != null) {
            List<M> geoPods = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                M geoPod = snapshot.getValue(mModelClass);
                double podLongitude = geoPod.getLongitude();
                if (podLongitude >= minLong && podLongitude <= maxLong) {
                    geoPods.add(geoPod);
                }
            }
            results(geoPods);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    protected abstract void results(List<M> geoPods);
}
