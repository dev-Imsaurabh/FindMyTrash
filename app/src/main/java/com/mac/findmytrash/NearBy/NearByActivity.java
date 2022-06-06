package com.mac.findmytrash.NearBy;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.model.LatLng;
import com.mac.findmytrash.R;
import com.mac.findmytrash.map_location.Map_Fragment;
import com.mac.findmytrash.map_location.PrefConfig;

public class NearByActivity extends AppCompatActivity {
    private String lat, lon, address;
    private WebView mWebView;
    private LottieAnimationView progressBar;
    private String baseUrl = "https://www.google.com/maps/dir/";
    private String finalAddress;
    private String finalUrl;
    private String mapUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by);
        hideStatusBar();
        lat = getIntent().getStringExtra("lat");
        lon = getIntent().getStringExtra("lon");
        address = getIntent().getStringExtra("address");
        mWebView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

//        PerformOperation();

//        CallWebView();
//        openMapFragment();
    }

    private void PerformOperation() {
        String removeComma = address.replaceAll(",", "");
        String removeDoubleSpace = removeComma.replaceAll("  ", " ");
        String addPlus = removeDoubleSpace.replaceAll(" ", "+");
        finalAddress = addPlus.replaceAll("/", "%2F");

        LatLng source = new LatLng(Double.parseDouble(PrefConfig.GetPref(NearByActivity.this, "latitudePref", "latitude")), Double.parseDouble(PrefConfig.GetPref(NearByActivity.this, "longitudePref", "longitude")));


        String addSource = String.valueOf(source.latitude) + "," + String.valueOf(source.longitude) + "/";
        String addAddress = finalAddress + "/";
        String addDestination = "@" + lat + "," + lon;


       finalUrl = baseUrl + addSource + addAddress + addDestination;
//        Toast.makeText(this, finalUrl, Toast.LENGTH_SHORT).show();
        CallWebView(finalUrl);


    }


    private void openMapFragment() {
        Map_Fragment fragment = new Map_Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("cus_latitude", lat);
        bundle.putString("cus_longitude", lon);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
//        Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();


    }


    public void hideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    private void CallWebView(String finalUrl) {

        // Brower niceties -- pinch / zoom, follow links in place
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new GeoWebViewClient());
        // Below required for geolocation
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.setWebChromeClient(new GeoWebChromeClient());
        // Load google.com
        mWebView.loadUrl(finalUrl);


        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);



            }


            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }


        });




    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();


        }
    }

    public class GeoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url != null && (url.startsWith("intent://"))) {
                String uri =finalUrl;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
                return true;
            } else {
                return false;

            }

        }
    }

    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }
    }
}