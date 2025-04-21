package tr.com.huseyinaydin.data.repository;

import tr.com.huseyinaydin.data.repository.EarthquakeRepository;
import tr.com.huseyinaydin.data.dtos.EarthquakeRecord;
import tr.com.huseyinaydin.data.remote.AfadApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class EarthquakeRepositoryImpl implements EarthquakeRepository {

    private final AfadApiService apiService;

    public EarthquakeRepositoryImpl(AfadApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Call<List<EarthquakeRecord>> getEarthquakes(String startDate, String endDate, Callback<List<EarthquakeRecord>> callback) {
        Call<List<EarthquakeRecord>> call = apiService.getEarthquakes(startDate, endDate);
        call.enqueue(callback);
        return call;
    }
}