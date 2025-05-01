package tr.com.huseyinaydin.activities.worker;

import android.content.Context;
import android.content.Intent;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import tr.com.huseyinaydin.service.EarthquakeService;

public class EarthquakeWorker extends Worker {

    public EarthquakeWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        // Deprem verilerini çekmek ve bildirim göndermek için servisi çağır
        Intent intent = new Intent(getApplicationContext(), EarthquakeService.class);
        getApplicationContext().startService(intent);
        return Result.success();
    }
}