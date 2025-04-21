package tr.com.huseyinaydin.data.repository;

import java.util.List;

import retrofit2.Call;

import tr.com.huseyinaydin.data.dtos.EarthquakeRecord;

public interface EarthquakeRepository {
    Call<List<EarthquakeRecord>> getEarthquakes(String startDate, String endDate, retrofit2.Callback<List<EarthquakeRecord>> callback);
}