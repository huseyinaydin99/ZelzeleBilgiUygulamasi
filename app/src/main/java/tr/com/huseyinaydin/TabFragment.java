package tr.com.huseyinaydin;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;

import tr.com.huseyinaydin.model.Earthquake;

public class TabFragment extends Fragment {

    private ListView earthquakeListView;
    private EarthquakeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        ListView listView = view.findViewById(R.id.list_view);

        // Örnek veri (API'den gelen verilerle değiştirilecek)
        List<Earthquake> earthquakes = new ArrayList<>();
        earthquakes.add(new Earthquake(4.5, "İstanbul - Silivri", "2024-03-01T14:09:54", 7.2));
        earthquakes.add(new Earthquake(2.3, "İzmir - Seferihisar", "2024-03-01T15:22:11", 5.8));
        earthquakes.add(new Earthquake(1.8, "Ankara - Çubuk", "2024-03-01T16:45:33", 3.5));

        // Adapter oluştur ve listeye bağla
        adapter = new EarthquakeAdapter(view.getContext(), earthquakes);
        listView.setAdapter(adapter);
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

}