package tr.com.huseyinaydin.data.remote;

import tr.com.huseyinaydin.data.dtos.EarthquakeRecord;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AfadApiService {

    @GET("apiv2/event/filter")
    Call<List<EarthquakeRecord>> getEarthquakes(
            @Query("start") String startDate,
            @Query("end") String endDate
    );
}
