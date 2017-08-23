package cl.cutiko.geopods.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import cl.cutiko.geopods.R;
import cl.cutiko.geopods.models.GeoPod;
import cl.cutiko.geopods.models.PodsCallback;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PodsCallback {

    private GoogleApiClient googleApiClient;
    private GoogleMap googleMap;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment != null) {
            fragmentTransaction.remove(mapFragment);
        }
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.add(R.id.mapContainer, SupportMapFragment.newInstance());
        fragmentTransaction.commitNowAllowingStateLoss();

        SupportMapFragment googleMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContainer);
        googleMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        //LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        LatLng current = new LatLng(-33.429072, -70.603748);
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String countryIso = telephonyManager.getSimCountryIso();
        new PopulatePods(this).getNear(current, countryIso);
        googleMap.addMarker(new MarkerOptions().position(current));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 14));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(), "Conexión Suspendida", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Conexión Fallida", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        googleApiClient.disconnect();
    }

    @Override
    public void podsReady(List<GeoPod> geoPods) {
        for (GeoPod geoPod : geoPods) {
            LatLng latLng = new LatLng(geoPod.getLatitude(), geoPod.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(latLng));
        }
    }
}
