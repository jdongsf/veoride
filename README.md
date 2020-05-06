# veoride

1. click start button, you will be direct to your current location.
   I generate two destinations on the north, on the east, which is 500 meters away.
2. click one of destionations, the other will disappear.
   then you can start moving, it will generate path on the map.
   Upon reaching the destination,  you will be able to see traveled path, elapsed trip time, total distance traveled.
3. thoughts: 
    create map view, add start button, final dialog as overlay.
    Click start button, set startTime as 0, start updating location in OnMyLocationChangeListener().
    Once we received changes, we will add Polyline to draw path.
    Once you are in 5 meters radius of destinations,  I will set LiveData value to true.
    Then UI will be refreshed, it will hide the mapview, show you the snapshot of google map, total time, total distance.
