package org.kpa.openstreetmap;

import org.kpa.sonar.PointCollection;

public class OpenStreetMapRest {
    private final PointCollection collection;

    public OpenStreetMapRest(PointCollection collection) {
        this.collection = collection;
    }

    public String getBoundingBoxRequest() {
//        return String.format("http://www.openstreetmap.org/api/0.6/map?bbox=left,bottom,right,top");
        return String.format("http://www.openstreetmap.org/api/0.6/map?bbox=%s,%s,%s,%s",
                collection.getSwPoint().getLongitude(), collection.getSwPoint().getLattitude(),
                collection.getNePoint().getLongitude(), collection.getNePoint().getLattitude()
        );
    }
}
