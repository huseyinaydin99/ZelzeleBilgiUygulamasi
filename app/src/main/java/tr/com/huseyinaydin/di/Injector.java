package tr.com.huseyinaydin.di;

import static tr.com.huseyinaydin.constants.URLs.getBaseUrl;

import tr.com.huseyinaydin.constants.URLs;
import tr.com.huseyinaydin.data.remote.AfadApiService;
import tr.com.huseyinaydin.data.repository.EarthquakeRepositoryImpl;
import tr.com.huseyinaydin.data.repository.EarthquakeRepository;
import tr.com.huseyinaydin.presentation.viewmodel.EarthquakeViewModel;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Injector {

    public static EarthquakeViewModel provideViewModel() {
        AfadApiService apiService = provideAfadApiService();
        EarthquakeRepository repository = new EarthquakeRepositoryImpl(apiService);
        return new EarthquakeViewModel(repository);
    }

    public static AfadApiService provideAfadApiService(){
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AfadApiService.class);
    }
}