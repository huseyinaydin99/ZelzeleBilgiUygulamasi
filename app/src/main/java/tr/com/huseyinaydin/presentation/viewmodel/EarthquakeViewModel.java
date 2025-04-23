package tr.com.huseyinaydin.presentation.viewmodel;

import static tr.com.huseyinaydin.constants.URLs.getBaseUrl;
import static tr.com.huseyinaydin.di.Injector.provideAfadApiService;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tr.com.huseyinaydin.constants.URLs;
import tr.com.huseyinaydin.data.dtos.EarthquakeRecord;
import tr.com.huseyinaydin.data.remote.AfadApiService;
import tr.com.huseyinaydin.data.repository.EarthquakeRepository;
import tr.com.huseyinaydin.data.repository.EarthquakeRepositoryImpl;
import tr.com.huseyinaydin.di.Injector;
import tr.com.huseyinaydin.utils.DateUtil;

public class EarthquakeViewModel extends ViewModel {

    private final EarthquakeRepository repository;
    private final MutableLiveData<List<EarthquakeRecord>> earthquakes = new MutableLiveData<>();

    public EarthquakeViewModel(EarthquakeRepository repository) {
        this.repository = repository;
    }

    // Parametresiz constructor ekleyin
    public EarthquakeViewModel() {
        this.repository = new EarthquakeRepositoryImpl(provideAfadApiService());  // veya başka bir methodla repository'yi başlatabilirsiniz
    }

    public LiveData<List<EarthquakeRecord>> getEarthquakes() {
        return earthquakes;
    }

    public void fetchEarthquakes(int dayCount) {
        String startDate = DateUtil.getDateBefore(dayCount);
        String endDate = DateUtil.getTodayDate();

        repository.getEarthquakes(startDate, endDate, new Callback<List<EarthquakeRecord>>() {
            @Override
            public void onResponse(Call<List<EarthquakeRecord>> call, Response<List<EarthquakeRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    earthquakes.postValue(response.body());
                    Log.d("Veri", "--: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<List<EarthquakeRecord>> call, Throwable t) {
                Log.e("AFAD", "Failed: " + t.getMessage());
            }
        });
    }

    public void fetchEarthquakes(int dayCount, double minMagnitude) {
        String startDate = DateUtil.getDateBefore(dayCount);
        String endDate = DateUtil.getTodayDate();

        repository.getEarthquakes(startDate, endDate, new Callback<List<EarthquakeRecord>>() {
            @Override
            public void onResponse(Call<List<EarthquakeRecord>> call, Response<List<EarthquakeRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Şiddet filtresi uygula
                    List<EarthquakeRecord> filteredList = new ArrayList<>();
                    for (EarthquakeRecord record : response.body()) {
                        if (record.getMagnitude() >= minMagnitude) {
                            filteredList.add(record);
                        }
                    }
                    earthquakes.postValue(filteredList);
                    Log.d("Veri", "--: " + filteredList);
                }
            }

            @Override
            public void onFailure(Call<List<EarthquakeRecord>> call, Throwable t) {
                Log.e("AFAD", "Failed: " + t.getMessage());
            }
        });
    }

    public void fetchEarthquakes(int dayCount, double minMagnitude, double maxMagnitude) {
        String startDate = DateUtil.getDateBefore(dayCount);
        String endDate = DateUtil.getTodayDate();

        repository.getEarthquakes(startDate, endDate, new Callback<List<EarthquakeRecord>>() {
            @Override
            public void onResponse(Call<List<EarthquakeRecord>> call, Response<List<EarthquakeRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EarthquakeRecord> filteredList = new ArrayList<>();
                    for (EarthquakeRecord record : response.body()) {
                        double magnitude = record.getMagnitude();
                        if (magnitude >= minMagnitude && magnitude <= maxMagnitude) {
                            filteredList.add(record);
                        }
                    }
                    earthquakes.postValue(filteredList);
                    Log.d("Depremler", "Filtrelenen veri sayısı: " + filteredList.size());
                }
            }

            @Override
            public void onFailure(Call<List<EarthquakeRecord>> call, Throwable t) {
                Log.e("AFAD", "Veri çekme hatası: " + t.getMessage());
            }
        });
    }
}