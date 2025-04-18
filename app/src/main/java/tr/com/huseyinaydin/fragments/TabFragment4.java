package tr.com.huseyinaydin.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.UUID;

import tr.com.huseyinaydin.R;
import tr.com.huseyinaydin.activities.EarthquakeActivity;
import tr.com.huseyinaydin.constants.URLs;
import tr.com.huseyinaydin.database.FileDatabaseHelper;
import tr.com.huseyinaydin.database.FileRepository;
import tr.com.huseyinaydin.models.Earthquake;
import tr.com.huseyinaydin.models.FileModel;
import tr.com.huseyinaydin.utils.EarthquakeExporter;
import tr.com.huseyinaydin.utils.EarthquakeExporterImpl;

public class TabFragment4 extends Fragment implements SearchableFragment {

    private ListView listView;
    private FileAdapter adapter;
    private FileDatabaseHelper dbHelper;

    //private EarthquakeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    //private AppCompatImageButton exportButton;
    /*private List<Earthquake> earthquakesBackup;
    private List<Earthquake> filteredList;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab4, container, false);

        AndroidThreeTen.init(view.getContext());

        /*filteredList = new ArrayList<>();
        earthquakesBackup = new ArrayList<>();*/

        // Dışa aktarma düğmesini başlattım
        //exportButton = view.findViewById(R.id.exportButton3);

        // Dışa aktarma düğmesi için bir tıklama dinleyicisi ayarladım
        /*exportButton.setOnClickListener(new View.OnClickListener() {
            EarthquakeExporter earthquakeExporter = new EarthquakeExporterImpl(view.getContext());
            @Override
            public void onClick(View view) {
                FileRepository fileRepository = new FileRepository(view.getContext());
                // Dışa aktarım işlemleri
                Toast.makeText(view.getContext(), "Dışa aktarım butonu tıklandı!", Toast.LENGTH_SHORT).show();
                // Bir iletişim kutusu oluşturucu oluşturun
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Dışa Aktarım İçin Dosya Formatı Seç");

                // PDF, HTML, Metin ve Word için onay kutuları oluşturun
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
                final CheckBox pdfCheckBox = dialogView.findViewById(R.id.pdfCheckBox);
                final CheckBox htmlCheckBox = dialogView.findViewById(R.id.htmlCheckBox);
                final CheckBox textCheckBox = dialogView.findViewById(R.id.textCheckBox);
                final CheckBox wordCheckBox = dialogView.findViewById(R.id.wordCheckBox);

                // İletişim düzenini ayarladım
                builder.setView(dialogView);

                // Eylem düğmelerini ayarladım (İptal ve Kaydet)
                builder.setPositiveButton("Kaydet", (dialog, which) -> {
                    if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((EarthquakeActivity)requireActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                    }
                    // Kaydetme işlemini gerçekleştirdim
                    String selectedFormats = "Dışa aktarım dosya formatı seç: ";
                    String path = "";
                    if(filteredList.size() > 0) {
                        earthquakesBackup.clear();
                        earthquakesBackup.addAll(filteredList);
                    }
                    if (pdfCheckBox.isChecked()) {
                        selectedFormats += "PDF ";
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "3 saat 0-3 - " + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + " - " + UUID.randomUUID().toString().substring(0, 8) + ".PDF";
                        earthquakeExporter.exportToPdf(earthquakesBackup, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "3 saat 0-3 - " + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + " - " + UUID.randomUUID().toString().substring(0, 8) + ".PDF");
                        fileRepository.insertFilePath(path);
                    }
                    if (htmlCheckBox.isChecked()) {
                        selectedFormats += "HTML ";
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "3 saat 0-3 - " + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + " - " + UUID.randomUUID().toString().substring(0, 8) + ".HTML";
                        earthquakeExporter.exportToHtml(earthquakesBackup, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "3 saat 0-3 - " + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + " - " + UUID.randomUUID().toString().substring(0, 8) + ".HTML");
                        fileRepository.insertFilePath(path);
                    }
                    if (textCheckBox.isChecked()) {
                        selectedFormats += "Text ";
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "3 saat 0-3 - " + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + " - " + UUID.randomUUID().toString().substring(0, 8) + ".TXT";
                        earthquakeExporter.exportToTxt(earthquakesBackup, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "3 saat 0-3 - " + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + " - " + UUID.randomUUID().toString().substring(0, 8) + ".TXT");
                        fileRepository.insertFilePath(path);
                    }
                    if (wordCheckBox.isChecked()) {
                        selectedFormats += "Word ";
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "3 saat 0-3 - " + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + " - " + UUID.randomUUID().toString().substring(0, 8) + ".DOCX";
                        earthquakeExporter.exportToWord(earthquakesBackup, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "3 saat 0-3 - " + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + " - " + UUID.randomUUID().toString().substring(0, 8) + ".DOCX");
                        fileRepository.insertFilePath(path);
                    }
                    // Dosya ekleme
                    //fileRepository.insertFilePath("/storage/emulated/0/Download/dosya1.txt");

                    // Listeyi çekme
                    List<FileModel> files = fileRepository.getAllFiles();
                    for(int i = 0; i < files.size(); i++){
                        Log.d("dosya", files.get(i).getFilePath());
                    }
                    Toast.makeText(view.getContext(), selectedFormats, Toast.LENGTH_SHORT).show();
                });

                builder.setNegativeButton("İptal", (dialog, which) -> {
                    // İptal işlemini gerçekleştirdim
                    dialog.dismiss();
                });

                // Dokunmatik ekranın dışında iptal edilemeyecek şekilde iletişim kutusunu ayarladım
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);  // Dış dokunuşta iletişim kutusunun kapanmasını devre dışı bıraktım

                dialog.show(); //Ya Allah!
            }
        });*/

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeHoursAgo = now.minusHours(480);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        String start = threeHoursAgo.format(formatter);
        String end = now.format(formatter);

        //new FetchEarthquakeData(view).execute(URLs.getLastOneHourAfad() + "start=" + start + "&end=" + end + "&minmag=4&maxmag=12");

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout3);
        // SwipeRefreshLayout'ı dinleyelim
        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(view.getContext(), "Veriler yenilendi!", Toast.LENGTH_SHORT).show();
                // Burada verileri yenileme işlemini yaparım
                //new FetchEarthquakeData(view).execute(URLs.getLastOneHourAfad() + "start=" + start + "&end=" + end + "&minmag=4&maxmag=12");
            }
        });*/

        swipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> {
            return listView.canScrollVertically(-1);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(view.getContext(), "Veriler yenilendi!", Toast.LENGTH_SHORT).show();
            // Yenileme işlemini burada yap
            swipeRefreshLayout.setRefreshing(false);
        });

        listView = view.findViewById(R.id.list_view4);
        dbHelper = new FileDatabaseHelper(getContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FileDatabaseHelper.TABLE_FILES, null);

        List<FileModel> fileList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_PATH));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_TIMESTAMP));

                fileList.add(new FileModel(id, path, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new FileAdapter(getContext(), fileList);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void filterList(String query) {

    }

    /*@Override
    public void filterList(String query) {
        Log.d("filterList", "Buradayım filterList");
        if (adapter != null) {
            Log.d("filterList", "Buradayım adapter");
            adapter.getFilter().filter(query);
        }
    }*/

    public class FileAdapter extends ArrayAdapter<FileModel> {
        private Context context;
        private List<FileModel> fileList;

        public FileAdapter(Context context, List<FileModel> fileList) {
            super(context, R.layout.list_item_file, fileList);
            this.context = context;
            this.fileList = fileList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FileModel model = fileList.get(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_file, parent, false);
            }

            TextView nameText = convertView.findViewById(R.id.file_name);
            TextView pathText = convertView.findViewById(R.id.file_path);
            TextView infoText = convertView.findViewById(R.id.file_info);
            ImageView iconView = convertView.findViewById(R.id.file_icon);

            File file = new File(model.getFilePath());

            String fileName = file.getName();
            String extension = getFileExtension(file);
            long fileSize = file.length();

            nameText.setText(fileName);
            pathText.setText(model.getFilePath());
            infoText.setText("Boyut: " + fileSize + " byte | Tür: " + extension.toUpperCase());

            // Dosya türüne göre ikon ata
            switch (extension.toLowerCase()) {
                case "pdf":
                    iconView.setImageResource(R.drawable.ic_pdf);
                    break;
                case "html":
                    iconView.setImageResource(R.drawable.ic_html);
                    break;
                case "docx":
                    iconView.setImageResource(R.drawable.ic_docx);
                    break;
                case "txt":
                    iconView.setImageResource(R.drawable.ic_txt);
                    break;
                default:
                    iconView.setImageResource(R.drawable.ic_file);
                    break;
            }

            return convertView;
        }

        private String getFileExtension(File file) {
            String name = file.getName();
            int lastDot = name.lastIndexOf(".");
            if (lastDot != -1 && lastDot < name.length() - 1) {
                return name.substring(lastDot + 1);
            } else {
                return ""; // uzantı yok
            }
        }
    }

    /*
    public class EarthquakeAdapter extends ArrayAdapter<Earthquake> implements Filterable {

        private Context context;
        private List<Earthquake> earthquakes;
        private List<Earthquake> originalList;

        public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
            super(context, R.layout.list_item_earthquake, earthquakes);
            this.context = context;
            this.earthquakes = earthquakes;
            originalList = new ArrayList<>();
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

            Earthquake earthquake;
            if(filteredList.size() <= 0)
                earthquake = earthquakes.get(position);
            else
                earthquake = filteredList.get(position); // ✅ doğru liste

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

        @Override
        public int getCount() {
            if(filteredList.size() <= 0)
                return earthquakes.size();
            else
                return filteredList.size(); // ✅ doğru liste
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

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<Earthquake> filtered = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        filtered.addAll(earthquakes);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (Earthquake item : earthquakes) {
                            Log.d("filterMetot", filterPattern + " - " + item.getLocation().toLowerCase());
                            if (item.getProvince().toLowerCase().contains(filterPattern)) {
                                Log.d("girdi girmedi?", "province girdi");
                                filtered.add(item);
                            }
                            else if (item.getLocation().toLowerCase().contains(filterPattern)) {
                                Log.d("girdi girmedi?", "location girdi");
                                filtered.add(item);
                            }
                            else if (item.getDistrict().toLowerCase().contains(filterPattern)) {
                                filtered.add(item);
                            }
                            else if (item.getFormattedDate().toLowerCase().contains(filterPattern)) {
                                filtered.add(item);
                            }
                        }
                    }

                    results.values = filtered;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList.clear();
                    filteredList.addAll((List<Earthquake>) results.values);
                    notifyDataSetChanged();
                }
            };
        }
    }*/
/*
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
            earthquakeList.clear();
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
                    ListView listView = view.findViewById(R.id.list_view3);
                    // Adapter oluştur ve listeye bağla
                    adapter = new EarthquakeAdapter(view.getContext(), earthquakeList);
                    listView.setAdapter(adapter);
                    if(filteredList.size() > 0)
                        earthquakesBackup.addAll(filteredList);
                    else
                        earthquakesBackup.addAll(earthquakeList);
                    //resultTextView.setText(stringBuilder.toString());
                } catch (Exception e) {
                    Log.e("MainActivity", "Error parsing JSON", e);
                    //resultTextView.setText("Deprem verileri işlenirken hata oluştu: " + e.getMessage());
                }
            } else {
                //resultTextView.setText("Veri alınamadı. İnternet bağlantınızı kontrol edin.");
            }
            // 🔽 Bu satır ile yenileme spinner'ını durduruyorum!
            swipeRefreshLayout.setRefreshing(false);
        }
    }*/
}