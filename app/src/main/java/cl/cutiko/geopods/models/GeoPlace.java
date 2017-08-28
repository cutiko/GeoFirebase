package cl.cutiko.geopods.models;

import cl.cutiko.geofirebase.models.GeoPod;

/**
 * Created by cutiko on 23-08-17.
 */

public class GeoPlace extends GeoPod {

    private String name, category;

    public GeoPlace() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
