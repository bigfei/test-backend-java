package com.wiredcraft.wcapi.model;


import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import static org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE;

public class Address {
    private String name;
    @GeoSpatialIndexed(type = GEO_2DSPHERE)
    private GeoJsonPoint location;

    public Address() {
    }

    public Address(String name) {
        this.name = name;
    }

    public Address(String name, GeoJsonPoint location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
        this.location = location;
    }
}
