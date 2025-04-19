package tr.com.huseyinaydin.fragments;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jakewharton.threetenabp.AndroidThreeTen;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tr.com.huseyinaydin.R;
import tr.com.huseyinaydin.database.DropboxRepository;
import tr.com.huseyinaydin.database.FileDatabaseHelper;
import tr.com.huseyinaydin.models.DropboxModel;
import tr.com.huseyinaydin.models.FileModel;
import tr.com.huseyinaydin.utils.DropboxHelper;
import tr.com.huseyinaydin.utils.EarthquakeExporter;
import tr.com.huseyinaydin.utils.EarthquakeExporterImpl;


public class TabFragment4 extends Fragment implements SearchableFragment {

    private ListView listView;
    private FileAdapter adapter;
    private FileDatabaseHelper dbHelper;

    //private EarthquakeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppCompatImageButton exportButton;
    private AppCompatImageButton dropBoxCloudBackupButton;
    /*private List<Earthquake> earthquakesBackup;
    private List<Earthquake> filteredList;*/
    private View view;
    private List<FileModel> fileList;
    private List<FileModel> filteredList;
    private DropboxHelper dropboxHelper;

    private static final int REQUEST_STORAGE_PERMISSION = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab4, container, false);

        AndroidThreeTen.init(view.getContext());

        //dropboxHelper = new DropboxHelper(view.getContext());

        filteredList = new ArrayList<>();
        //earthquakesBackup = new ArrayList<>();

        dropBoxCloudBackupButton = view.findViewById(R.id.dropBoxCloudBackupButton);
        dropBoxCloudBackupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropboxRepository repository = new DropboxRepository(requireContext());
                if(repository.getAllDropboxAccounts().size() <= 0)
                    showDropboxDialog(requireContext());
                else {
                    String appName = "";
                    String token = "";
                    appName = repository.getAllDropboxAccounts().get(0).getAppName();
                    token = repository.getAllDropboxAccounts().get(0).getAccessToken();
                    dropboxHelper = new DropboxHelper(requireContext(),appName, token);
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    }
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        dropboxHelper.backupFiles(fileList);
                    }
                }
            }
        });
        // Dışa aktarma düğmesini başlattım
        exportButton = view.findViewById(R.id.exportButton4);

        // Dışa aktarma düğmesi için bir tıklama dinleyicisi ayarladım
        exportButton.setOnClickListener(new View.OnClickListener() {
            EarthquakeExporter earthquakeExporter = new EarthquakeExporterImpl(view.getContext());
            @Override
            public void onClick(View view) {
                showSortDialog();
            }
        });

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
        listView = view.findViewById(R.id.list_view4);
        swipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> {
            return listView.canScrollVertically(-1);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(view.getContext(), "Veriler yenilendi!", Toast.LENGTH_SHORT).show();
            // Yenileme işlemini burada yap
            swipeRefreshLayout.setRefreshing(false);
            refreshData();
        });

        refreshData();
        return view;
    }

    private void showDropboxDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dropbox_modal, null);
        builder.setView(view);

        // Dialog boşluğa tıklanınca kapanmasın
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false); // Geri tuşu da kapatmasın istiyorsan bunu açık bırak

        EditText editAppName = view.findViewById(R.id.editAppName);
        EditText editAccessToken = view.findViewById(R.id.editAccessToken);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        DropboxRepository repository = new DropboxRepository(context);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String appName = editAppName.getText().toString().trim();
            String token = editAccessToken.getText().toString().trim();

            if (appName.isEmpty() || token.isEmpty()) {
                Toast.makeText(context, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aynı uygulama adına sahip kayıt var mı kontrol et
            List<DropboxModel> accounts = repository.getAllDropboxAccounts();
            boolean alreadyExists = false;
            for (DropboxModel model : accounts) {
                if (model.getAppName().equalsIgnoreCase(appName)) {
                    alreadyExists = true;
                    break;
                }
            }

            if (alreadyExists) {
                Toast.makeText(context, "Bu uygulama adı zaten kayıtlı!", Toast.LENGTH_LONG).show();
            } else {
                dropboxHelper = new DropboxHelper(view.getContext(),appName, token);
                repository.insertDropboxAccount(appName, token);
                Toast.makeText(context, "Başarıyla kaydedildi.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    dropboxHelper.backupFiles(fileList);
                }
            }
        });

        dialog.show();
    }

    public void openWebPageWithChooser() {
        String url = "https://www.dropbox.com/developers";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // Seçim ekranı göster
        Intent chooser = Intent.createChooser(intent, "Tarayıcı seçin");
        startActivity(chooser);
    }

    public void openDropboxDevelopersPage() {
        String url = "https://www.dropbox.com/developers";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // Chrome yüklüyse öncelikle Chrome'u kullan
        if (isChromeInstalled()) {
            intent.setPackage("com.android.chrome");
        }

        try {
            // Fragment'te activity'den başlatıyoruz
            requireActivity().startActivity(intent);
        } catch (Exception e) {
            // Herhangi bir hata durumunda varsayılan tarayıcıya dön
            intent.setPackage(null);
            requireActivity().startActivity(intent);
        }
    }

    public boolean isChromeInstalled() {
        try {
            requireActivity().getPackageManager().getPackageInfo("com.android.chrome", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // İzin verildi, dosya işlemleri yapılabilir
            } else {
                // İzin reddedildi
            }
        }
    }

    private void refreshData(){
        dbHelper = new FileDatabaseHelper(getContext());

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FileDatabaseHelper.TABLE_FILES, null);

        fileList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_PATH));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(FileDatabaseHelper.COLUMN_TIMESTAMP));

                fileList.add(new FileModel(id, path, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();

        Collections.sort(fileList, new Comparator<FileModel>() {
            @Override
            public int compare(FileModel fileModel1, FileModel fileModel2) {
                // timestamp'i karşılaştırıyoruz ki sıralayabilelim!
                //return file1.getTimestamp().compareTo(file2.getTimestamp());

                File file1 = new File(fileModel1.getFilePath());
                File file2 = new File(fileModel2.getFilePath());

                long modified1 = file1.lastModified();
                long modified2 = file2.lastModified();

                return Long.compare(modified2, modified1); // Yeni dosyalar önce gelsin sıralaması!
                /*File file1 = new File(fileModel1.getFilePath());
                File file2 = new File(fileModel2.getFilePath());

                String name1 = file1.getName().toLowerCase(); // Küçük harf dönüşümü daha stabil sıralama için
                String name2 = file2.getName().toLowerCase();

                return name1.compareTo(name2); // A'dan Z'ye sıralar*/
            }
        });
        adapter = new FileAdapter(getContext(), fileList);
        listView.setAdapter(adapter);
    }

    private void showSortDialog() {
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_sort_options, null);

        Spinner criteriaSpinner = dialogView.findViewById(R.id.spinner_criteria);
        Spinner orderSpinner = dialogView.findViewById(R.id.spinner_order);

        // Sıralama kriterleri ve yönleri
        String[] criteria = {"Dosya Adı", "Boyut", "Dosya Türü", "Oluşturulma Tarihi"};
        String[] orders = {"Artan (ASC)", "Azalan (DESC)"};

        criteriaSpinner.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, criteria));
        orderSpinner.setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, orders));

        // AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle("Sıralama Seçenekleri")
                .setView(dialogView)
                .setPositiveButton("Sırala", (dialogInterface, which) -> {
                    String selectedCriteria = criteriaSpinner.getSelectedItem().toString();
                    String selectedOrder = orderSpinner.getSelectedItem().toString();

                    // Seçime göre sıralama işlemi
                    sortFiles(selectedCriteria, selectedOrder);
                })
                .setNegativeButton("İptal", null)
                .create();

        dialog.setCanceledOnTouchOutside(false); // dışarı tıklayınca kapanmasın
        dialog.show();
    }

    private void sortFiles(final String criteria, final String order) {
        Comparator<FileModel> comparator = new Comparator<FileModel>() {
            @Override
            public int compare(FileModel f1, FileModel f2) {
                File file1 = new File(f1.getFilePath());
                File file2 = new File(f2.getFilePath());

                int result = 0;

                switch (criteria) {
                    case "Dosya Adı":
                        result = file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase());
                        break;

                    case "Boyut":
                        long size1 = file1.length();
                        long size2 = file2.length();
                        result = Long.compare(size1, size2);
                        break;

                    case "Dosya Türü":
                        String ext1 = getFileExtension(file1.getName());
                        String ext2 = getFileExtension(file2.getName());
                        result = ext1.compareTo(ext2);
                        break;

                    case "Oluşturulma Tarihi":
                        long date1 = file1.lastModified();
                        long date2 = file2.lastModified();
                        result = Long.compare(date1, date2);
                        break;
                }

                if (order.equals("DESC")) {
                    result = -result;
                }

                return result;
            }
        };

        Collections.sort(fileList, comparator);
        //adapter.notifyDataSetChanged();
        adapter = new FileAdapter(getContext(), fileList);
        listView.setAdapter(adapter);
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            return fileName.substring(index + 1).toLowerCase();
        } else {
            return "";
        }
    }

    /*@Override
    public void filterList(String query) {

    }*/

    @Override
    public void filterList(String query) {
        Log.d("filterList", "Buradayım filterList");
        if (adapter != null) {
            Log.d("filterList", "Buradayım adapter");
            adapter.getFilter().filter(query);
        }
    }

    public class FileAdapter extends ArrayAdapter<FileModel> implements Filterable {
        private Context context;
        private List<FileModel> fileList;
        private List<FileModel> originalList;

        public FileAdapter(Context context, List<FileModel> fileList) {
            super(context, R.layout.list_item_file, new ArrayList<>(fileList)); // adapter listesi
            this.context = context;
            this.fileList = new ArrayList<>(fileList); // güncel liste (filtrelenebilir)
            this.originalList = new ArrayList<>(fileList); // orijinal kopya
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

            FileModel fileModel;
            if(filteredList.size() <= 0)
                fileModel = fileList.get(position);
            else
                fileModel = filteredList.get(position); // ✅ doğru liste

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

        @Override
        public int getCount() {
            if(filteredList.size() <= 0)
                return fileList.size();
            else
                return filteredList.size(); // ✅ doğru liste
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

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    List<FileModel> filtered = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        filtered.addAll(fileList);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();
                        for (FileModel item : fileList) {
                            Log.d("filterMetot", filterPattern + " - " + new File(item.getFilePath()).getAbsolutePath().toLowerCase());
                            if (new File(item.getFilePath()).getName().toLowerCase().contains(filterPattern)) {
                                Log.d("girdi girmedi?", "province girdi");
                                filtered.add(item);
                            }
                            else if (new File(item.getFilePath()).getAbsolutePath().toLowerCase().contains(filterPattern)) {
                                Log.d("girdi girmedi?", "location girdi");
                                filtered.add(item);
                            }/*
                            else if (new File(item.getFilePath()).get.toLowerCase().contains(filterPattern)) {
                                filtered.add(item);
                            }
                            else if (item.getFormattedDate().toLowerCase().contains(filterPattern)) {
                                filtered.add(item);
                            }*/
                        }
                    }

                    results.values = filtered;
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredList.clear();
                    filteredList.addAll((List<FileModel>) results.values);
                    notifyDataSetChanged();
                    /*clear(); // adapter'daki listeyi temizle
                    addAll((List<FileModel>) results.values); // filtrelenen sonuçları ekle
                    notifyDataSetChanged(); // listeyi yenile*/
                }
            };
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