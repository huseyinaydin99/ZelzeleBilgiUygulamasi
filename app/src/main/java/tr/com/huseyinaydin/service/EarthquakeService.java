package tr.com.huseyinaydin.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;

import java.util.UUID;

import tr.com.huseyinaydin.R;
import tr.com.huseyinaydin.database.AppDatabase;
import tr.com.huseyinaydin.database.Earthquake;
import tr.com.huseyinaydin.database.EarthquakeDao;
import android.media.RingtoneManager;

public class EarthquakeService extends IntentService {
    public EarthquakeService() {
        super("EarthquakeService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        fetchEarthquakeData();
    }

    /*private void fetchEarthquakeData() {
        // Burada AFAD API'sinden veri çekeceğim. Örnek olarak HTTP isteği yapılabilir.
        // 3.5 büyüklüğündeki depremleri kontrol et ve bildirim gönder.
        String earthquakeData = "3.5 büyüklüğünde bir deprem oldu!";
        // Eğer 3.5 üzeri deprem varsa bildirim gönder
        showNotification("Deprem Bildirimi", earthquakeData);
    }*/

    private void fetchEarthquakeData() {
        // AFAD API'den veri çekme
        String uniqueId = UUID.randomUUID().toString().substring(3, 8); // benzersiz hash üretir
        String earthquakeTitle = "Örnek:" + uniqueId + " - 3.5 büyüklüğünde deprem";
        //String earthquakeTitle = "Örnek1: 3.5 büyüklüğünde deprem"; // API'den gelen başlık
        String earthquakeDescription = "Deprem detayları..."; // API'den gelen açıklama
        String earthquakeTime = "2025-05-01 10:00"; // API'den gelen zaman

        // Veritabanı kontrolü
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "earthquake_db").build();
        EarthquakeDao earthquakeDao = db.earthquakeDao();

        // Eğer aynı deprem daha önce bildirilmemişse
        if (earthquakeDao.countByTitle(earthquakeTitle) == 0) {
            // Yeni deprem kaydını ekle
            Earthquake earthquake = new Earthquake(earthquakeTitle, earthquakeDescription, earthquakeTime);
            earthquakeDao.insert(earthquake);

            // Bildirimi gönder
            showNotification("Deprem Oldu!", earthquakeTitle);
        }
    }

    private void showNotification(String title, String message) {
        // Bildirimleri göstermek için NotificationManager kullanıyorum.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("earthquake_channel", "Deprem Bildirimleri", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, "earthquake_channel")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.zelzelelogo)
                .setAutoCancel(true)
                .setOngoing(false)
                //.setTimeoutAfter(6000)  // Bildirimin ekran süresi (6 saniye)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // sesi burada ekledik
                .build();

        notificationManager.notify(1, notification);
    }
}