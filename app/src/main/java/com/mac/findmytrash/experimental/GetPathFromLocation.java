package com.mac.findmytrash.experimental;

import static com.google.android.gms.maps.model.JointType.ROUND;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.mac.findmytrash.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GetPathFromLocation extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

    private Context context;
    private String TAG = "GetPathFromLocation";
    private LatLng source, destination;
    private ArrayList<LatLng> wayPoint;
    private GoogleMap mMap;
    private boolean animatePath, repeatDrawingPath;
    private DirectionPointListener resultCallback;
    private ProgressDialog progressDialog;

    //https://www.mytrendin.com/draw-route-two-locations-google-maps-android/
    //https://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/

    public GetPathFromLocation(Context context, LatLng source, LatLng destination, ArrayList<LatLng> wayPoint, GoogleMap mMap, boolean animatePath, boolean repeatDrawingPath, DirectionPointListener resultCallback) {
        this.context = context;
        this.source = source;
        this.destination = destination;
        this.wayPoint = wayPoint;
        this.mMap = mMap;
        this.animatePath = animatePath;
        this.repeatDrawingPath = repeatDrawingPath;
        this.resultCallback = resultCallback;
    }

    synchronized public String getUrl(LatLng source, LatLng dest, ArrayList<LatLng> wayPoint) {

        String url = "https://maps.googleapis.com/maps/api/directions/json?sensor=false&mode=driving&origin="
                + source.latitude + "," + source.longitude + "&destination=" + dest.latitude + "," + dest.longitude;
        for (int centerPoint = 0; centerPoint < wayPoint.size(); centerPoint++) {
            if (centerPoint == 0) {
                url = url + "&waypoints=optimize:true|" + wayPoint.get(centerPoint).latitude + "," + wayPoint.get(centerPoint).longitude;
            } else {
                url = url + "|" + wayPoint.get(centerPoint).latitude + "," + wayPoint.get(centerPoint).longitude;
            }
        }
        url = url + "&key=" + context.getResources().getString(R.string.gmap_api_key);

        return url;
    }

    public int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... url) {

        String data;

        try {
            InputStream inputStream = null;
            HttpURLConnection connection = null;
            try {
                URL directionUrl = new URL(getUrl(source, destination, wayPoint));
                connection = (HttpURLConnection) directionUrl.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                data = stringBuffer.toString();
                bufferedReader.close();

            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e.toString());
                return null;
            } finally {
                inputStream.close();
                connection.disconnect();
            }
            Log.e(TAG, "Background Task data : " + data);

            //Second AsyncTask

            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObject = new JSONObject(data);
                // Starts parsing data
                DirectionHelper helper = new DirectionHelper();
                routes = helper.parse(jsonObject);
                Log.e(TAG, "Executing Routes : "/*, routes.toString()*/);

                return routes;

            } catch (Exception e) {
                Log.e(TAG, "Exception in Executing Routes : " + e.toString());
                return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "Background Task Exception : " + e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        super.onPostExecute(result);

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        String distance = "";
        String duration = "";

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                if (j == 0) {    // Get distance from the list
                    distance = (String) point.get("distance");
                    continue;
                } else if (j == 1) { // Get duration from the list
                    duration = (String) point.get("duration");
                    continue;
                }

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(8);
            lineOptions.color(Color.RED);
            //lineOptions.color(getRandomColor());

            if (animatePath) {
                final ArrayList<LatLng> finalPoints = points;
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PolylineOptions polylineOptions;
                        final Polyline greyPolyLine, blackPolyline;
                        final ValueAnimator polylineAnimator;

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng latLng : finalPoints) {
                            builder.include(latLng);
                        }
                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.RED);
                        polylineOptions.width(8);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.endCap(new SquareCap());
                        polylineOptions.jointType(ROUND);
                        polylineOptions.addAll(finalPoints);
                        greyPolyLine = mMap.addPolyline(polylineOptions);

                        polylineOptions = new PolylineOptions();
                        polylineOptions.width(8);
                        polylineOptions.color(Color.WHITE);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.endCap(new SquareCap());
                        polylineOptions.zIndex(5f);
                        polylineOptions.jointType(ROUND);

                        blackPolyline = mMap.addPolyline(polylineOptions);
                        polylineAnimator = ValueAnimator.ofInt(0, 100);
                        polylineAnimator.setDuration(5000);
                        polylineAnimator.setInterpolator(new LinearInterpolator());
                        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                List<LatLng> points = greyPolyLine.getPoints();
                                int percentValue = (int) valueAnimator.getAnimatedValue();
                                int size = points.size();
                                int newPoints = (int) (size * (percentValue / 100.0f));
                                List<LatLng> p = points.subList(0, newPoints);
                                blackPolyline.setPoints(p);
                            }
                        });

                        polylineAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (repeatDrawingPath) {
                                    List<LatLng> greyLatLng = greyPolyLine.getPoints();
                                    if (greyLatLng != null) {
                                        greyLatLng.clear();

                                    }
                                    polylineAnimator.start();
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                                polylineAnimator.cancel();
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                        polylineAnimator.start();
                    }
                });
            }

            Log.e(TAG, "PolylineOptions Decoded");
        }

        // Drawing polyline in the Google Map for the i-th route
        if (resultCallback != null && lineOptions != null)
            resultCallback.onPath(lineOptions, distance, duration);
    }
}
