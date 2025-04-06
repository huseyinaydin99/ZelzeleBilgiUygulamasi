package tr.com.huseyinaydin;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tr.com.huseyinaydin.constants.URLs;

public class EarthquakeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        // Toolbar ayarları
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_1);
        }

        // DrawerLayout ve NavigationView ayarları
        drawerLayout = findViewById(R.id.earthquake);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Menü öğeleri tıklama işlemleri
            Toast.makeText(getApplicationContext(), "tıklandı", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawers();
            return true;
        });

        // TabLayout ve ViewPager ayarları
        ViewPager viewPager = findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        // Tab ikonlarını ayarla
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_favorite);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_search);

        // Drawer Listener ekleme
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Drawer kaydırılırken çalışır (animasyon sırasında)
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Drawer tamamen açıldığında çalışır
                //Toast.makeText(EarthquakeActivity.this, "Menü açıldı", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Drawer tamamen kapandığında çalışır
                //Toast.makeText(EarthquakeActivity.this, "Menü kapandı", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Drawer durumu değiştiğinde çalışır
                // newState: STATE_IDLE, STATE_DRAGGING, STATE_SETTLING
            }
        });

        new FetchEarthquakeData().execute(URLs.getLastOneHourAfad());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Sol üst menü butonu kontrolü (android.R.id.home)
        //Toast.makeText(getApplicationContext(), "tıklandı", Toast.LENGTH_SHORT).show();
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START); // Drawer'ı aç
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_2);
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabFragment(), "Afad");
        adapter.addFragment(new TabFragment(), "Kandilli");
        adapter.addFragment(new TabFragment(), "Ara");
        viewPager.setAdapter(adapter);
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    private class FetchEarthquakeData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String earthquakeJsonStr = null;
            Log.e("URL", urls[0]);
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                earthquakeJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("EarthquakeActivity", "!Hata: ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("EarthquakeActivity", "Akış kapanırken hata oldu!", e);
                    }
                }
            }

            return earthquakeJsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // JSONArray olarak parse ediyoruz çünkü gelen veri bir array
                    JSONArray earthquakes = new JSONArray(result);

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Son Depremler:\n\n");

                    for (int i = 0; i < earthquakes.length(); i++) {
                        JSONObject earthquake = earthquakes.getJSONObject(i);

                        // Tarih formatını düzenle
                        String dateStr = earthquake.getString("date");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        Date date = sdf.parse(dateStr);
                        String formattedDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date);

                        stringBuilder.append("Büyüklük: ").append(earthquake.getString("magnitude")).append(" - ")
                                .append(earthquake.getString("type")).append("\n")
                                .append("Yer: ").append(earthquake.getString("location")).append("\n")
                                .append("İl/İlçe: ").append(earthquake.getString("province")).append("/")
                                .append(earthquake.getString("district")).append("\n")
                                .append("Tarih: ").append(formattedDate).append("\n")
                                .append("Derinlik: ").append(earthquake.getString("depth")).append(" km\n")
                                .append("Koordinat: ").append(earthquake.getString("latitude")).append(", ")
                                .append(earthquake.getString("longitude")).append("\n\n");
                    }

                    if (earthquakes.length() == 0) {
                        stringBuilder.append("Son deprem bulunamadı.");
                    }

                    System.out.println(stringBuilder.toString());

                    //resultTextView.setText(stringBuilder.toString());
                } catch (Exception e) {
                    Log.e("MainActivity", "Error parsing JSON", e);
                    //resultTextView.setText("Deprem verileri işlenirken hata oluştu: " + e.getMessage());
                }
            } else {
                //resultTextView.setText("Veri alınamadı. İnternet bağlantınızı kontrol edin.");
            }
        }
    }
}