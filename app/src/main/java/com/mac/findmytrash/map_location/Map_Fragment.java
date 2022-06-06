package com.mac.findmytrash.map_location;


import static android.content.Context.MODE_PRIVATE;
import static com.mac.findmytrash.map_location.Constants.MAPVIEW_BUNDLE_KEY;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mac.findmytrash.FCM_experiment.Model.NotificationData;
import com.mac.findmytrash.FCM_experiment.Model.PushNotification;
import com.mac.findmytrash.FCM_experiment.SendNotification;
import com.mac.findmytrash.Login.OTP_Verification_Activity;
import com.mac.findmytrash.MainActivity;
import com.mac.findmytrash.R;
import com.mac.findmytrash.experimental.GoogleDirection;
import com.mac.findmytrash.submit_details.SubmitModel;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Map_Fragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap MoveMap, tempMap;
    private FusedLocationProviderClient mFusedLocationClient;
    //defining some widgets
//    private MaterialSearchBar searchBar;
    private EditText searchBar;
    private FloatingActionButton fab_search, fab_marker;
    private GoogleMap map = null;
    private int searchBarToggle = 0;
    private String uid, name;
    //finding places client for places api
    private PlacesClient placesClient;
    //finding ripple background
    private RippleBackground ripple_effect;
    private boolean admin = false;
    private boolean mapMarkFlag = false;
    private String area;
    private String address;
    private String pinCode;
    private Double pinLatitude, pinLongitude;
    private Bundle bundle;
    private String cus_latitude, cus_longitude;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<SubmitModel> list;
    private ArrayList<LatLng> wayPoint;
    Marker marker;
    private GoogleDirection gd;
    private SupportMapFragment mapFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_map_, container, false);
        bundle = getArguments();


        mMapView = view.findViewById(R.id.mapView);
        fab_search = view.findViewById(R.id.fab_search);
        fab_marker = view.findViewById(R.id.fab_marker);

        ripple_effect = view.findViewById(R.id.ripple_effect);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
//        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
//        mapFragment.getMapAsync(this);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
// searchbar task started

        Places.initialize(getContext(), "AIzaSyC7YwtlUEEQS6r5R-KCeERp6oUYVA19Nfk");
//        Places.initialize(getContext(),"AIzaSyBJUd_-MqjFOEG-BFjzSIxhdvlq3FsFb1c") ;
        getPlaces();

// search bar task finished

        initGoogleMap(savedInstanceState);


        return view;
    }

    private void markCusLocation(GoogleMap cusMap, String name, String uid) {


        cusMap.clear();

//        wayPoint = new ArrayList<>();


        mapMarkFlag = true;
        pinLatitude = null;
        pinLongitude = null;

        LatLng destinantion = new LatLng(Double.parseDouble(cus_latitude), Double.parseDouble(cus_longitude));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(bitmapDescriptor(getContext(), R.drawable.custom_marker));
        markerOptions.position(destinantion);
        markerOptions.title(name);
        cusMap.addMarker(markerOptions);
//
//
//        MarkerOptions markerOptions1 = new MarkerOptions();
//        markerOptions1.icon(bitmapDescriptor(getContext(), R.drawable.custom_marker));
//        markerOptions1.position(source);
//        markerOptions1.title(source.latitude + " : " + source.longitude);
//        cusMap.addMarker(markerOptions1);
//        builder.include(source);
//
//
//
//        LatLngBounds bounds = builder.build();
//
//
//
//        int width = mapFragment.getView().getMeasuredWidth();
//        int height = mapFragment.getView().getMeasuredHeight();
//        int padding = (int) (width * 0.15); // offset from edges of the map 10% of screen
//
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
//
//        cusMap.animateCamera(cu);
//
//        Toast.makeText(getContext(), "working", Toast.LENGTH_SHORT).show();
//        cusMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source,15));
//        gd = new GoogleDirection(getContext());
//        gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
//            public void onResponse(String status, Document doc, GoogleDirection gd) {
//                Toast.makeText(getContext(), status, Toast.LENGTH_SHORT).show();
//
//                gd.animateDirection(cusMap, gd.getDirection(doc), GoogleDirection.SPEED_FAST
//                        , true, true, true, false, null, false, true, new PolylineOptions().width(8).color(Color.RED));
//
//                cusMap.addMarker(new MarkerOptions().position(source)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)));
//
//                cusMap.addMarker(new MarkerOptions().position(destinantion)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker2)));
//
//                String TotalDistance = gd.getTotalDistanceText(doc);
//                String TotalDuration = gd.getTotalDurationText(doc);
//            }
//        });
//
//
//        gd.request(source, destinantion, GoogleDirection.MODE_DRIVING);


    }

    private void pinMarkerPoint(GoogleMap pinMap) {
        if (!admin) {


            pinMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {

                    if (PrefConfig.GetPref(getContext(), "markerPref", "marker").equals("1")) {

                        mapMarkFlag = true;
                        pinLatitude = null;
                        pinLongitude = null;

                        pinLatitude = latLng.latitude;
                        pinLongitude = latLng.longitude;

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.icon(bitmapDescriptor(getContext(), R.drawable.ic_svg_flag));
                            markerOptions.position(latLng);
                            markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                            if(bundle==null){
                                pinMap.clear();

                            }


                            pinMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                            pinMap.addMarker(markerOptions);



                    } else {
                        Toast.makeText(getContext(), "Turn on marker toggle", Toast.LENGTH_SHORT).show();
                    }


                    //getting mark info///////////////////////////////////////


                }
            });

            fab_marker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (mapMarkFlag) {

                            Geocoder geocoder;
                            List<Address> addresses = null;
                            geocoder = new Geocoder(getContext(), Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(pinLatitude, pinLongitude, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            pinCode = addresses.get(0).getPostalCode();
                            address = addresses.get(0).getAddressLine(0);


                            area = addresses.get(0).getSubLocality();
                            if (area == null) {
                                area = addresses.get(0).getLocality();
                            }


                            Intent intent = new Intent(getContext(), pinMarkInfo_Activity.class);
                            intent.putExtra("area", area);
                            intent.putExtra("address", address);
                            intent.putExtra("pinCode", pinCode);
                            intent.putExtra("latitude", pinLatitude.toString());
                            intent.putExtra("longitude", pinLongitude.toString());
//                            getContext().startActivity(intent);
                            if(bundle==null){
                                showDialog(pinCode, area, address, pinLatitude, pinLongitude, pinMap);

                            }else{
                                showShareDialog(pinCode, area, address, pinLatitude, pinLongitude, pinMap);
                            }


                            mapMarkFlag = false;
//                            pinMap.clear();


                        } else {
                            Toast.makeText(getContext(), "Please long press on map to mark the location !", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }


    }

    private void showShareDialog(String pinCode, String area, String address, Double pinLatitude, Double pinLongitude, GoogleMap pinMap) {


        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        View view  = getActivity().getLayoutInflater().inflate(R.layout.submission_dialog, null);
        dialog.setContentView(R.layout.location_share_dialog);

        TextView shared_username = (TextView) dialog.findViewById(R.id.shared_username);
        TextView et_address = (TextView) dialog.findViewById(R.id.et_address);
        EditText et_sharePin = (EditText) dialog.findViewById(R.id.et_sharePin);
        CardView shareBtn = (CardView) dialog.findViewById(R.id.share_btn);
        shared_username.setText(PrefConfig.GetPref(getContext(), "userPref", "username"));
        et_sharePin.setText(pinCode);
        et_address.setText(address);


        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latitude = String.valueOf(pinLatitude);
//                String latitude = "29.017652732110413";
                String longitude =String.valueOf(pinLongitude);
//                String longitude = "77.7619469538331";
                String topic ="/topics/"+uid;
//                String topic ="/topics/"+"250001";
                String phoneNumber =user.getPhoneNumber();

                String displayMessage ="Hey I am "+PrefConfig.GetPref(getContext(), "userPref", "username")+" sharing you dustbin location near by you";

                String message = latitude+","+longitude+","+shared_username.getText().toString()+","+displayMessage+","+phoneNumber;

                PushNotification pushNotification = new PushNotification(new NotificationData("Received Help",message),topic);
                SendNotification.Send(pushNotification,getContext());
                dialog.dismiss();

                SharedPreferences preferences = getContext().getSharedPreferences("helpPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                FirebaseMessaging.getInstance().unsubscribeFromTopic("cancel"+uid);

                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.slide_up);


            }
        });


        dialog.show();
    }


    //Creating Custom Marker///////////////////////////////////////////

    private BitmapDescriptor bitmapDescriptor(Context context, int iconId) {

        Drawable iconDrawable = ContextCompat.getDrawable(context, iconId);
        iconDrawable.setBounds(0, 0, iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        iconDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }


    // search bar task method started //////////////////////

    private void getPlaces() {
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .build(getContext());

                startActivityForResult(intent, 100);

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == getActivity().RESULT_OK) {

            ripple_effect.startRippleAnimation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ripple_effect.stopRippleAnimation();

                }
            }, 3000);

            Place place = Autocomplete.getPlaceFromIntent(data);
            LatLng latLng = place.getLatLng();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(bitmapDescriptor(getContext(), R.drawable.custom_marker));
            markerOptions.position(latLng);
            markerOptions.title(place.getName());
            MoveMap.addMarker(markerOptions);
            float zoomLevel = 13.0f; //This goes up to 21
            MoveMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        } else if (requestCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    ///search bar task method finished//////////////////////////////////

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();


    }

    @Override
    public void onStart() {
        super.onStart();

//        if(user!=null){
//            if(user.getPhoneNumber().equals("6393567935")){
//                admin=true;
//            }else {
//                fab_marker.setVisibility(View.GONE);
//            }
//        }
        mMapView.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {


        MoveMap = map;
        tempMap = map;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            return;
        }
        map.setMyLocationEnabled(true);


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    //For getting current user location with zoom

                    final Handler handler = new Handler();

                    final Runnable runnable = new Runnable() {
                        public void run() {

                            getContinuousLocation();

                            handler.postDelayed(this, 5000);

                        }
                    };
                    handler.post(runnable);

                    getLocation(map, task);

                    pinMarkerPoint(map);


                    if (bundle != null) {
                        cus_latitude = bundle.getString("cus_latitude");
                        cus_longitude = bundle.getString("cus_longitude");
                        uid = bundle.getString("uid");
                        name = bundle.getString("name");

                        markCusLocation(map, name, uid);


                    } else {
                        MarkLocationsOnMap(map);

                    }


                }

            }
        });


    }

    private void getContinuousLocation() {

        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {


                return;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                try {
                    Location location = task.getResult();
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();

                    GetPostalCode(currentLatitude, currentLongitude);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void MarkLocationsOnMap(GoogleMap map) {
        list = new ArrayList<>();
        reference.child("Area").child(PrefConfig.GetPref(getContext(), "pinCode", "code")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    SubmitModel mapLocationModel = snapshot1.getValue(SubmitModel.class);

                    list.add(mapLocationModel);


                }

                Marker marker = null;

                for (int i = 0; i < list.size(); i++) {
                    marker = map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(list.get(i).getSubmitLat()), Double.parseDouble(list.get(i).getSubmitLong())))
                            .anchor(0.5f, 0.5f)

                            .icon(bitmapDescriptor(getContext(), R.drawable.custom_marker2)));
                    marker.setTag("Marked by: " + list.get(i).getSubmitName() + " near-> " + list.get(i).getSubmitAddress());

                }


                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        Object tag = marker.getTag();
                        if (!tag.equals("user")) {

                            ShowDialog(String.valueOf(marker.getPosition().latitude), String.valueOf(marker.getPosition().longitude), tag);


                        }


                        return false;
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    public void getLocation(GoogleMap map, Task<Location> task) {
        Marker marker;
        Location location = task.getResult();
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(bitmapDescriptor(getContext(), R.drawable.custom_marker));
        markerOptions.position(latLng);
        markerOptions.title("i am here");
        marker = map.addMarker(markerOptions);
        marker.setTag("user");
        float zoomLevel = 13.0f; //This goes up to 21
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        GetPostalCode(currentLatitude, currentLongitude);


    }

    private void GetPostalCode(double currentLatitude, double currentLongitude) {

        try {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String code = addresses.get(0).getPostalCode();
            PrefConfig.SetPref(getContext(), "pinCode", "code", code);
            PrefConfig.SetPref(getContext(), "latitudePref", "latitude", String.valueOf(currentLatitude));
            PrefConfig.SetPref(getContext(), "longitudePref", "longitude", String.valueOf(currentLongitude));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        getContinuousLocation();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    protected void showDialog(String pinCode, String area, String address, Double pinLatitude, Double pinLongitude, GoogleMap pinMap) {

        Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        View view  = getActivity().getLayoutInflater().inflate(R.layout.submission_dialog, null);
        dialog.setContentView(R.layout.submission_dialog);

        EditText et_landmark = (EditText) dialog.findViewById(R.id.et_landmark);
        EditText et_markPin = (EditText) dialog.findViewById(R.id.et_markPin);
        TextView txt_name = (TextView) dialog.findViewById(R.id.user_name);
        CardView proceedbtn = (CardView) dialog.findViewById(R.id.proceed_btn);
        txt_name.setText(PrefConfig.GetPref(getContext(), "userPref", "username"));
        et_markPin.setText(pinCode);
        et_landmark.requestFocus();


        proceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = reference.push().getKey();
                SubmitModel submitModel = new SubmitModel(txt_name.getText().toString(), user.getUid(), pinCode, String.valueOf(pinLatitude), String.valueOf(pinLongitude), et_landmark.getText().toString().trim(), address, key);


                reference.child("Area").child(pinCode).child(key).setValue(submitModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Marked at " + address, Toast.LENGTH_SHORT).show();
                        pinMap.clear();
                        MarkLocationsOnMap(pinMap);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });


        dialog.show();
    }




    private void ShowDialog(String lat, String lon, Object tag) {

        final Dialog dialog = new Dialog(getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.ask_nav_dialog);

        CardView walking_btn = (CardView) dialog.findViewById(R.id.walking_btn);
        CardView driving_btn = (CardView) dialog.findViewById(R.id.driving_btn);
        TextView title_txt = (TextView) dialog.findViewById(R.id.title);
        title_txt.setText(tag.toString());


        walking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "&mode=w");
                //String url = "https://www.google.com/maps/dir/?api=1&destination=" + submitModel.getSubmitLat() + "," + submitModel.getSubmitLong() + "&travelmode=driving";

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

                dialog.dismiss();
            }
        });

        driving_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon);
                //String url = "https://www.google.com/maps/dir/?api=1&destination=" + submitModel.getSubmitLat() + "," + submitModel.getSubmitLong() + "&travelmode=driving";

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    public void operateMapFromOutSide() {
        tempMap.clear();
        MarkLocationsOnMap(tempMap);
    }


}