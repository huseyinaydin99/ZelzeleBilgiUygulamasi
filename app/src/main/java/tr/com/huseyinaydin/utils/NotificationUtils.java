package tr.com.huseyinaydin.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import tr.com.huseyinaydin.R;

public class NotificationUtils {
    private static final String CHANNEL_ID = "earthquake_alert_channel";

    public static void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android 8.0+ için kanal oluşturma
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Deprem Uyarıları";
            String description = "4.0 üzeri depremler için anlık bildirimler";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Bildirim oluşturma
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_home) // İkon ekle
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // Yüksek öncelikli bildirim
                .setDefaults(NotificationCompat.DEFAULT_ALL)  // Ses, titreşim, ışık vs.
                .setAutoCancel(true); // Kullanıcı bildirimi tıkladığında kaybolur

        // Bildirimi gösterme
        notificationManager.notify(1, builder.build());  // ID'yi her zaman benzersiz tutmak gerekebilir
    }
}