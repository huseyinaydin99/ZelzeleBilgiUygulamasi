package tr.com.huseyinaydin.fragments;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tr.com.huseyinaydin.R;
import tr.com.huseyinaydin.constants.URLs;
import tr.com.huseyinaydin.model.Earthquake;

public class TabFragment extends Fragment {


    private EarthquakeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        AndroidThreeTen.init(view.getContext());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeHoursAgo = now.minusHours(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        String start = threeHoursAgo.format(formatter);
        String end = now.format(formatter);
        new FetchEarthquakeData(view).execute(URLs.getLastOneHourAfad() + "start=" + start + "&end=" + end + "&minmag=0&maxmag=3");
        return view;
    }

    public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

        private Context context;
        private List<Earthquake> earthquakes;

        public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
            super(context, R.layout.list_item_earthquake, earthquakes);
            this.context = context;
            this.earthquakes = earthquakes;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_item_earthquake, parent, false);

                holder = new ViewHolder();
                holder.magnitudeIndicator = convertView.findViewById(R.id.magnitudeIndicator);
                holder.locationText = convertView.findViewById(R.id.locationText);
                holder.dateText = convertView.findViewById(R.id.dateText);
                holder.depthText = convertView.findViewById(R.id.depthText);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Earthquake earthquake = earthquakes.get(position);

            // Şiddete göre renk belirleme
            int color = getMagnitudeColor(earthquake.getMagnitude());
            GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitudeIndicator.getBackground();
            magnitudeCircle.setColor(color);

            holder.magnitudeIndicator.setText(String.valueOf(earthquake.getMagnitude()));
            holder.locationText.setText(earthquake.getLocation());
            holder.dateText.setText(earthquake.getFormattedDate());
            holder.depthText.setText("Derinlik: " + earthquake.getDepth() + " km");

            return convertView;
        }

        private int getMagnitudeColor(double magnitude) {
            int colorResourceId;
            switch ((int) Math.floor(magnitude)) {
                case 0:
                case 1:
                    colorResourceId = R.color.magnitude1; // Yeşil
                    break;
                case 2:
                    colorResourceId = R.color.magnitude2; // Açık Yeşil
                    break;
                case 3:
                    colorResourceId = R.color.magnitude3; // Sarı
                    break;
                case 4:
                    colorResourceId = R.color.magnitude4; // Turuncu
                    break;
                default:
                    colorResourceId = R.color.magnitude5; // Kırmızı (5+)
                    break;
            }
            return ContextCompat.getColor(getContext(), colorResourceId);
        }

        class ViewHolder {
            TextView magnitudeIndicator;
            TextView locationText;
            TextView dateText;
            TextView depthText;
        }
    }


    private class FetchEarthquakeData extends AsyncTask<String, Void, String> {
        private ListView earthquakeListView;
        private View view;
        private List<Earthquake> earthquakeList;

        public FetchEarthquakeData(View view) {
            earthquakeList = new ArrayList<>();
            this.view = view;
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String earthquakeJsonStr = null;
            Log.e("URL", urls[0]);
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                earthquakeJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("EarthquakeActivity", "!Hata: ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("EarthquakeActivity", "Akış kapanırken hata oldu!", e);
                    }
                }
            }

            return earthquakeJsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // JSONArray olarak parse ediyoruz çünkü gelen veri bir array
                    JSONArray earthquakes = new JSONArray(result);

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Son Depremler:\n\n");

                    for (int i = 0; i < earthquakes.length(); i++) {
                        JSONObject earthquake = earthquakes.getJSONObject(i);

                        // Tarih formatını düzenle
                        String dateStr = earthquake.getString("date");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                        Date date = sdf.parse(dateStr);
                        String formattedDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date);


                        earthquakeList.add(new Earthquake(
                                earthquake.getDouble("magnitude"),
                                earthquake.getString("type"),
                                earthquake.getString("location"),
                                earthquake.getString("province"),
                                earthquake.getString("district"),
                                formattedDate, // Bu tarih formatlanmış bir String ise direkt geçiyoruz
                                earthquake.getString("depth"),
                                earthquake.getString("latitude"),
                                earthquake.getString("longitude")
                        ));
                        stringBuilder.append("Büyüklük: ").append(earthquake.getString("magnitude")).append(" - ")
                                .append(earthquake.getString("type")).append("\n")
                                .append("Yer: ").append(earthquake.getString("location")).append("\n")
                                .append("İl/İlçe: ").append(earthquake.getString("province")).append("/")
                                .append(earthquake.getString("district")).append("\n")
                                .append("Tarih: ").append(formattedDate).append("\n")
                                .append("Derinlik: ").append(earthquake.getString("depth")).append(" km\n")
                                .append("Koordinat: ").append(earthquake.getString("latitude")).append(", ")
                                .append(earthquake.getString("longitude")).append("\n\n");
                    }

                    if (earthquakes.length() == 0) {
                        stringBuilder.append("Son deprem bulunamadı.");
                    }

                    System.out.println(stringBuilder.toString());
                    ListView listView = view.findViewById(R.id.list_view);
                    // Adapter oluştur ve listeye bağla
                    adapter = new EarthquakeAdapter(view.getContext(), earthquakeList);
                    listView.setAdapter(adapter);
                    //resultTextView.setText(stringBuilder.toString());
                } catch (Exception e) {
                    Log.e("MainActivity", "Error parsing JSON", e);
                    //resultTextView.setText("Deprem verileri işlenirken hata oluştu: " + e.getMessage());
                }
            } else {
                //resultTextView.setText("Veri alınamadı. İnternet bağlantınızı kontrol edin.");
            }
        }
    }
}