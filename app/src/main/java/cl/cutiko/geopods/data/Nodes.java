package cl.cutiko.geopods.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by cutiko on 23-08-17.
 */

public class Nodes {

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public DatabaseReference getRoot() {
        return root;
    }

    public DatabaseReference locations(String countryIso) {
        return root.child("locations").child(countryIso);
    }

    public Query nearLatitudes(String countryIso, double latitude, double radius) {
        double minLat = latitude - radius;
        double maxLat = latitude + radius;
        return locations(countryIso).orderByChild("latitude").startAt(minLat).endAt(maxLat);
    }

    public Query categoryLocations(String countryIso) {
        //return root.child("places_name").child(countryIso).orderByValue().startAt("not").endAt("not" + "\uf8ff");
        return root.child("places_category").child(countryIso).orderByValue().equalTo("category1");
    }

    public Query favoriteLocations(String countryIso) {
        return root.child("favorites").child("user_uid_1").child(countryIso);
    }


}
