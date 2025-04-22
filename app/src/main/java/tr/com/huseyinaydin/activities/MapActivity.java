package tr.com.huseyinaydin.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tr.com.huseyinaydin.R;
import tr.com.huseyinaydin.models.Earthquake;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng userLocation;
    private Earthquake earthquake; // Model diğer aktiviteden gelcek
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Harita fragment'ini başlat
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Konum istemcisi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Earthquake modelini al
        earthquake = (Earthquake) getIntent().getSerializableExtra("earthquakeModel");

        // Konum izni kontrolü
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (mMap != null) {
                            setupMap();
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            if (userLocation != null) {
                setupMap();
            }
        }
    }

    private void setupMap() {
        LatLng quakeLatLng = new LatLng(Double.parseDouble(earthquake.getLatitude()),
                Double.parseDouble(earthquake.getLongitude()));

        // Kullanıcı konumu varsa çizgi çiz
        if (userLocation != null) {
            mMap.addPolyline(new PolylineOptions()
                    .add(userLocation, quakeLatLng)
                    .color(Color.RED)
                    .width(5f));

            float[] distance = new float[1];
            Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    quakeLatLng.latitude, quakeLatLng.longitude,
                    distance
            );

            mMap.addMarker(new MarkerOptions()
                    .position(userLocation)
                    .title("Senin Konumun | Buradasın"));

            mMap.addMarker(new MarkerOptions()
                            .position(quakeLatLng)
                            .title("Deprem Yeri")
                            .snippet("Tarih: " + earthquake.getFormattedDate() + "\nBüyüklük: " + earthquake.getMagnitude()))
                    .showInfoWindow();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quakeLatLng, 7));

            mMap.setOnMarkerClickListener(marker -> {
                if (marker.getTitle() != null && marker.getTitle().equals("Deprem Yeri")) {
                    showEarthquakeDialog();
                    return true; // varsayılan behavior'ı engelle
                }
                return false;
            });

            evaluateRisk(distance[0], earthquake);
        } else {
            // Sadece deprem yeri gösterilecek
            mMap.addMarker(new MarkerOptions()
                            .position(quakeLatLng)
                            .title("Deprem Yeri")
                            .snippet("Tarih: " + earthquake.getFormattedDate()))
                    .showInfoWindow();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quakeLatLng, 7));
        }
    }

    private void evaluateRisk(float distanceMeters, Earthquake eq) {
        double magnitude = eq.getMagnitude();
        double depth = Double.parseDouble(eq.getDepth());
        float distanceKm = distanceMeters / 1000f;

        int riskColor = Color.GREEN;
        if (magnitude >= 6.0 && distanceKm <= 50 && depth <= 10) {
            riskColor = Color.RED;
        } else if (magnitude >= 5.0 && distanceKm <= 100) {
            riskColor = Color.YELLOW;
        } else if (magnitude >= 4.0 && distanceKm <= 150) {
            riskColor = Color.rgb(255, 165, 0); // Turuncu
        }

        // Ekranda da gösteriyom (örneğin bir TextView veya CardView'da)
        View riskView = findViewById(R.id.risk_light);
        riskView.setBackgroundColor(riskColor);

        TextView distanceText = findViewById(R.id.distance_info);
        distanceText.setText(String.format(Locale.getDefault(), "Mesafe: %.2f km", distanceKm));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            Toast.makeText(this, "Konum izni verilmedi, sadece deprem yeri gösterilecek ben ne yapıyım verseydin (-:", Toast.LENGTH_LONG).show();
        }
    }

    private void showEarthquakeDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("🌍 Deprem Bilgileri");

        StringBuilder message = new StringBuilder();
        /*message.append("📍 Konum: ").append(earthquake.getLocation()).append("\n");
        message.append("📅 Tarih: ").append(earthquake.getFormattedDate()).append("\n");
        message.append("📏 Derinlik: ").append(earthquake.getDepth()).append(" km\n");
        message.append("💥 Büyüklük: ").append(earthquake.getMagnitude()).append(" M\n");
        message.append("🆔 ID: ").append(earthquake.getId()).append("\n");
        message.append("🔗 Kaynak: ").append(earthquake.getSource());*/

        message.append("📅 Tarih: ").append(earthquake.getFormattedDate()).append("\n")
                .append("📍 Lokasyon: ").append(earthquake.getLocation()).append("\n")
                .append("💥 Büyüklük: ").append(earthquake.getMagnitude()).append("\n")
                .append("⛏️ Derinlik: ").append(earthquake.getDepth()).append(" km\n")
                .append("🌐 Enlem: ").append(earthquake.getLatitude()).append("\n")
                .append("📏 Boylam: ").append(earthquake.getLongitude());

        builder.setMessage(message.toString());

        builder.setCancelable(false); // boşluğa tıklanmasın
        builder.setPositiveButton("Kapat", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    /*private String formatDate(String dateStr) {
        try {
            // Verilen tarih formatını çözümle (e.g., 2025-04-21)
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = originalFormat.parse(dateStr);

            // Yeni formatta tarihi al (örneğin: 21 Nisan 2025)
            SimpleDateFormat newFormat = new SimpleDateFormat("dd MMMM yyyy");
            return newFormat.format(date);
        } catch (Exception e) {
            return dateStr; // Hata durumunda orijinal tarih döner
        }
    }*/
}