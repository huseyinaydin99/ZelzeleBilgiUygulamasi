package tr.com.huseyinaydin.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tr.com.huseyinaydin.models.Earthquake;

public class EarthquakeSorter {
    public static void sortByDateDescending(List<Earthquake> earthquakeList) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        Collections.sort(earthquakeList, new Comparator<Earthquake>() {
            @Override
            public int compare(Earthquake e1, Earthquake e2) {
                try {
                    Date d1 = sdf.parse(e1.getStringDate());
                    Date d2 = sdf.parse(e2.getStringDate());
                    return d2.compareTo(d1); // en yeni önce
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }

    public static void sortByDateAscending(List<Earthquake> earthquakeList) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

        Collections.sort(earthquakeList, new Comparator<Earthquake>() {
            @Override
            public int compare(Earthquake e1, Earthquake e2) {
                try {
                    Date d1 = sdf.parse(e1.getStringDate());
                    Date d2 = sdf.parse(e2.getStringDate());
                    return d1.compareTo(d2); // küçükten büyüğe (en eski önce)
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
    }
}