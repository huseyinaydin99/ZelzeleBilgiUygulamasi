package tr.com.huseyinaydin.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tr.com.huseyinaydin.R;
import tr.com.huseyinaydin.data.dtos.EarthquakeRecord;
import tr.com.huseyinaydin.di.Injector;
import tr.com.huseyinaydin.presentation.viewmodel.EarthquakeViewModel;

public class EarthquakeMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EarthquakeViewModel viewModel;
    private GoogleMap mMap; // düzeltme: 'map' yerine 'mMap'
    private Spinner spinnerMagnitude;
    double selectedMinMagnitude = 0.0;
    double selectedMaxMagnitude = 9.0; // üst limit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_map_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.earthquakeMapActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedMinMagnitude = 0.0;
        selectedMaxMagnitude = 3.0;

        spinnerMagnitude = findViewById(R.id.spinnerMagnitude);

        List<String> magnitudeOptions = new ArrayList<>();
        magnitudeOptions.add("0 - 3 şiddeti");
        magnitudeOptions.add("3 - 5 şiddeti");
        magnitudeOptions.add("5 - 9 şiddeti");
        magnitudeOptions.add("9 - 12 şiddeti");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                magnitudeOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMagnitude.setAdapter(adapter);

        spinnerMagnitude.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: // 0 - 3
                        selectedMinMagnitude = 0.0;
                        selectedMaxMagnitude = 3.0;
                        break;
                    case 1: // 3 - 5
                        selectedMinMagnitude = 3.0;
                        selectedMaxMagnitude = 5.0;
                        break;
                    case 2: // 5 - 9
                        selectedMinMagnitude = 5.0;
                        selectedMaxMagnitude = 9.0;
                        break;
                    case 3: // 9 - 12
                        selectedMinMagnitude = 9.0;
                        selectedMaxMagnitude = 12.0;
                        break;
                }

                // Örnek kullanım: fetchEarthquakes(5, selectedMinMagnitude, selectedMaxMagnitude);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Gerekirse default değer atanabilir
            }
        });

        // ViewModel
        viewModel = Injector.provideViewModel(); // DI üzerinden

        // Butonlar
        Button btn5 = findViewById(R.id.btn5Days);
        Button btn10 = findViewById(R.id.btn10Days);
        Button btn20 = findViewById(R.id.btn20Days);
        Button btnRefresh = findViewById(R.id.btnRefresh);

        btn5.setOnClickListener(v -> viewModel.fetchEarthquakes(5, selectedMinMagnitude, selectedMaxMagnitude));
        btn10.setOnClickListener(v -> viewModel.fetchEarthquakes(10, selectedMinMagnitude, selectedMaxMagnitude));
        btn20.setOnClickListener(v -> viewModel.fetchEarthquakes(20, selectedMinMagnitude, selectedMaxMagnitude));
        btnRefresh.setOnClickListener(v -> viewModel.fetchEarthquakes(3, selectedMinMagnitude, selectedMaxMagnitude)); // default: 1 hafta

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng turkey = new LatLng(39.0, 35.0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkey, 5.5f));

        //son 3 gün
        viewModel.fetchEarthquakes(3, selectedMinMagnitude, selectedMaxMagnitude);

        viewModel.getEarthquakes().observe(this, records -> {
            Log.d("DepremVerisi", "Toplam kayıt sayısı: " + records.size());
            showEarthquakesOnMap(records);
        });

        mMap.setOnMarkerClickListener(marker -> {
            showEarthquakeDialog(marker);
            return true;
        });
    }

    private void showEarthquakesOnMap(List<EarthquakeRecord> records) {
        if (mMap == null) return;

        mMap.clear();
        for (EarthquakeRecord record : records) {
            LatLng location = new LatLng(record.getLatitude(), record.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title(record.getLocation())
                    .snippet("Tarih: " + record.getDate() + ", Büyüklük: " + record.getMagnitude());

            mMap.addMarker(markerOptions);
        }
    }

    private void showEarthquakeDialog(com.google.android.gms.maps.model.Marker marker) {
        List<EarthquakeRecord> records = viewModel.getEarthquakes().getValue();

        if (records == null || records.isEmpty()) return;

        EarthquakeRecord selectedQuake = null;

        for (EarthquakeRecord record : records) {
            if (record.getLocation().equals(marker.getTitle())) {
                selectedQuake = record;
                break;
            }
        }

        if (selectedQuake == null) return;

        StringBuilder details = new StringBuilder();
        details.append("📅 Tarih: ").append(formatDate(selectedQuake.getDate())).append("\n")
                .append("📍 Lokasyon: ").append(selectedQuake.getLocation()).append("\n")
                .append("💥 Büyüklük: ").append(selectedQuake.getMagnitude()).append("\n")
                .append("⛏️ Derinlik: ").append(selectedQuake.getDepth()).append(" km\n")
                .append("🌐 Enlem: ").append(selectedQuake.getLatitude()).append("\n")
                .append("📏 Boylam: ").append(selectedQuake.getLongitude());

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("🌍 Deprem Detayları")
                .setMessage(details.toString())
                .setCancelable(false)
                .setPositiveButton("Tamam", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private String formatDate(String dateStr) {
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
    }
}