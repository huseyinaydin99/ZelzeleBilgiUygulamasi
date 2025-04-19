package tr.com.huseyinaydin.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import tr.com.huseyinaydin.database.DropboxRepository;
import tr.com.huseyinaydin.models.FileModel;

public class DropboxHelper {
    private static String ACCESS_TOKEN = "sl.u.AFqm86B_Ti2tHjhq-hmD4jeZ1hj5KXriy71pByhJvZBeQkBzRe8lhZJErqMC4vVNrX_Ffrja5fZ1DZdE4219PpI69_LletcZPCLJZzp-aAI7wjOuzIXwUJ5To1EW4YBjxxFXoKehWOkoZWxzMker_D8QXO6K3sl5t4gyvrjAxAqKnW83gW8zg7j6UdBuaa7sqzBVc5b7_J4gsGWCyLVm3hfLiXBgyxbkx3CcdFygVpuC8dzqIty3j_LhxwqAa7-8hZuUJo1fhRvKoC_t2UlF5yU6vKcI4oxl5KivVwfyahQ9PWJ8tlCohMdSDbKT25eDwim9ucPaeKkH0xMQRAsCDGlu_1dt6OmLTCrHrbBvucVM0MuuKsowNajY3ipVU_fiFD6ouBuyOTU_dBwiNevPRz4djA9ZBN-TGpRzk0FfkWpDu11EC6hh4t38JE2Ybu7PFBelajUyC4l5NoPnkiw7rWbhreixIdtxofrUzvSLxV-u9-2tsKwOFV2tr0GEzpG-SdldjbAheoUTA4MtBM0fwMlZzHs1q7vGMDlszscYUxKzhngPrThxwyP9v6ccWYeJFs-_MHXy-vDNcs2qjgZqOj7oU4KdvvWbevlLYKDV-gRpiedabDBgJowFzulR2bBgquRao9PUIEyU6gwTeREMyRr9cW0DVwPK-bE3zYz25b0bjF_JaAdz0AkU1N0Xxc7VffsT4IjN9JQ0Z7dlK3OHmxb9M6_YhmgB8ydQPIc4bs4CpKKLIGvUkNdwWN_PYgut0Z3zLt-4iCnDjSDvLPqh7Kcj2MVQMvIpuo_rOKuQrf5L5eK4pJcM1CBqUA5bh35SZrY4x9_bW3wTCWLaeFL8gGjgBgMmKqlnP_M2yvGDxEJDz59a2vT28IDh84gnRg0yZFY9aBlMgsY8I6um2_YFY5RIEDwhZDrm5jMkjyoibTeRvgvzv-mCkgUlbd2wCJs0yGJJo4PGJgXo_AGKHWdDzjrLUntIfH1wJLsAe_eL0oKYDXedmI26LRRkBf_nTu5hwAOMQpby_H9dduAlIrjUpfVIxGIxCVntrBDmzKrFrKmIoCwgYu_1vmn6L1nYRJQcoCRaUrvFQpT-aFH5AQqG0CpaLehNkajE6nYHYFFUWT_dzysSO-xFyMoDURYn7MO7ZmznVw87icejVjJ9nVUT06C_VjcnvqHSplirkmUL2BAPKCuLGpQC60uIhKvSAz7hdIaLBbnvK8ZOhvzRc8u2yjnpelKHHQJTY0cKLdgA9gyT_p9Q7TK9MWzVaYwAD0NSM3H0YWIcM5yQd0u3HcHAqHgfZzCort3YHTScHpzoYw8wEw";
    private Context context;
    private DbxClientV2 dbxClient;
    private DropboxRepository dropboxRepository;
    private String appName;
    private String token;

    public DropboxHelper(Context context, String appName, String token) {
        this.context = context;
        this.appName = appName;
        this.token = token;
        dropboxRepository = new DropboxRepository(context);
        DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).build();
        dbxClient = new DbxClientV2(config, ACCESS_TOKEN);
        ACCESS_TOKEN = token;
    }

    public void backupFiles(List<FileModel> fileList) {
        new BackupTask().execute(fileList);
    }

    private class BackupTask extends AsyncTask<List<FileModel>, Void, Boolean> {
        @Override
        protected Boolean doInBackground(List<FileModel>... lists) {
            List<FileModel> fileList = lists[0];
            boolean allSuccess = true;

            for (FileModel fileModel : fileList) {
                /*Uri uri = Uri.parse(fileModel.getFilePath());
                String realPath = getRealPathFromUri(context, uri);
                File file = new File(realPath);*/
                File file = new File(fileModel.getFilePath());
                Log.e("dosya", file.getAbsolutePath() + " - " + fileModel.getFilePath());

                Log.e("dosya-debug", "Kontrol ediliyor: " + file.getAbsolutePath());
                Log.e("dosya-debug", "exists: " + file.exists());
                Log.e("dosya-debug", "canRead: " + file.canRead());
                if (file.exists()) {
                    //Log.e("dosya", file.getAbsolutePath() + " - " + fileModel.getFilePath());
                    try (InputStream inputStream = new FileInputStream(file)) {
                        FileMetadata metadata = dbxClient.files().uploadBuilder("/backup/" + file.getName())
                                .withMode(WriteMode.OVERWRITE)
                                .uploadAndFinish(inputStream);
                    } catch (IOException | DbxException e) {
                        e.printStackTrace();
                        allSuccess = false;
                    }
                }
                else
                    allSuccess = false;
            }
            return allSuccess;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(context, "Yedekleme başarılı!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Yedekleme sırasında hata oluştu!", Toast.LENGTH_SHORT).show();
            }
        }

        public String getRealPathFromUri(Context context, Uri uri) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String s = cursor.getString(column_index);
                cursor.close();
                return s;
            }
            return null;
        }

    }
}