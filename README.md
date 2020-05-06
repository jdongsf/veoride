# veoride

1. Enable Location service for the app
2. click on the map, choose your destionation, then click start button
3. move your position toward the destionation.
   when you are in 20 meters radius, it will show the dialog info.
   a. Their traveled path
   b. The elapsed trip time
   c. The total distance traveled
   If you are using emulator like me,  you can just set gps location for your emulator.
   It will draw the path for you dynanmically.
4. thoughts: 
    create map view, add start button, final dialog as overlay.
    Click start button, set startTime as 0, start updating location in OnMyLocationChangeListener().
    Once we received changes, we will add Polyline to draw path.
    Once you are in 5 meters radius of destinations,  I will set LiveData value to true.
    Then UI will be refreshed, it will hide the mapview, show you the snapshot of google map, total time, total distance.
