package com.mac.findmytrash.experimental;

import com.google.android.gms.maps.model.PolylineOptions;

public interface DirectionPointListener {
    public void onPath(PolylineOptions polyLine, String distance, String duration);
}
