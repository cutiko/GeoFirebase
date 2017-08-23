package cl.cutiko.geopods.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cl.cutiko.geopods.data.Nodes;
import cl.cutiko.geopods.models.GeoPod;
import cl.cutiko.geopods.models.PodsCallback;

/**
 * Created by cutiko on 23-08-17.
 */

public class PopulatePods implements ValueEventListener {

    private static final double RADIUS = 0.02;
    private PodsCallback callback;
    private double currentLongitude;

    public PopulatePods(PodsCallback callback) {
        this.callback = callback;
    }

    public void getNear(LatLng current, String countryIso) {
        currentLongitude = current.longitude;
        Query reference = new Nodes().nearLatitudes(countryIso, current.latitude, RADIUS);
        reference.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<GeoPod> geoPods = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            GeoPod geoPod = snapshot.getValue(GeoPod.class);
            double podLongitude = geoPod.getLongitude();
            double minLongitude = currentLongitude - RADIUS;
            double maxLongitude = currentLongitude + RADIUS;
            if (podLongitude <= maxLongitude && podLongitude >= minLongitude) {
                geoPods.add(geoPod);
            }
        }

        callback.podsReady(geoPods);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
