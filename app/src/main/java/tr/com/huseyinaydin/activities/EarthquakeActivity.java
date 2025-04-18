package tr.com.huseyinaydin.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tr.com.huseyinaydin.R;
import tr.com.huseyinaydin.fragments.SearchableFragment;
import tr.com.huseyinaydin.fragments.TabFragment;
import tr.com.huseyinaydin.fragments.TabFragment2;
import tr.com.huseyinaydin.fragments.TabFragment3;
import tr.com.huseyinaydin.fragments.TabFragment4;

public class EarthquakeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private SearchView searchView;
    private ViewPager2 viewPager;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        viewPager = findViewById(R.id.view_pager);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        // Toolbar ayarları
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_1);
        }

        fragmentManager = getSupportFragmentManager();

        // DrawerLayout ve NavigationView ayarları
        drawerLayout = findViewById(R.id.earthquake);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            // Menü öğeleri tıklama işlemleri
            Toast.makeText(getApplicationContext(), "tıklandı", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawers();

            int id = item.getItemId();

            if (id == R.id.saved_files) {
                /*fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new FileListFragment())
                        .commit();*/
                viewPager.setCurrentItem(4);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // TabLayout ve ViewPager ayarları
        //viewPager = findViewById(R.id.view_pager);
        //setupViewPager(viewPager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TabFragment());
        fragments.add(new TabFragment2());
        fragments.add(new TabFragment3());
        fragments.add(new TabFragment4());

        MyPagerAdapter adapter = new MyPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        //tabLayout.setupWithViewPager(viewPager);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Ana Sayfa");
                            break;
                        case 1:
                            tab.setText("Ayarlar");
                            break;
                        case 2:
                            tab.setText("Abc");
                            break;
                    }
                }).attach();

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

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("onQueryTextSubmit","sorgu submit");
                filterCurrentFragment(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("onQueryTextSubmit","sorgu text change");
                filterCurrentFragment(newText);
                return true;
            }
        });


// SearchView'in içindeki EditText'e ulaş
        int searchEditTextId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);

        EditText searchEditText = searchView.findViewById(searchEditTextId);

        if (searchEditText != null) {
            searchEditText.clearFocus(); // Fokus aldırma
            searchEditText.setFocusable(false); // İlk başta focus kapalı
            searchEditText.setFocusableInTouchMode(true); // Ama sonra kullanıcı dokunursa açılabilsin
        }

        // Klavye direkt açılmasın
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //new FetchEarthquakeData().execute(URLs.getLastOneHourAfad());
    }

    private void filterCurrentFragment(String query) {
        int currentItem = viewPager.getCurrentItem();
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentByTag("f" + currentItem); // ViewPager ile fragment'ı al
        Log.d("null mı?", currentFragment == null ? "null" : "null değil");
        if (currentFragment instanceof SearchableFragment) {
            Log.d("filterCurrentFragment", "Buradayım SearchableFragment");
            ((SearchableFragment) currentFragment).filterList(query);
        }
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
        adapter.addFragment(new TabFragment(), "3 saat 0-3");
        adapter.addFragment(new TabFragment2(), "24 saat 3-4");
        adapter.addFragment(new TabFragment3(), "20 gün 4>");
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

    public class MyPagerAdapter extends FragmentStateAdapter {

        private final List<Fragment> fragmentList;

        public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            this.fragmentList = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }

}