package kr.or.hcc.young.csvautoupload.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import kr.or.hcc.young.csvautoupload.R;
import kr.or.hcc.young.csvautoupload.filechooser.FileBrowserAppsAdapter;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_FILE_REQUEST = 1;
    private Context mContext;

    private TextView mFileNameTextView;
    private Button mSelectFileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        initView();
    }

    private void initView() {
        mFileNameTextView = (TextView) findViewById(R.id.file_name_textview);
        mSelectFileButton = (Button) findViewById(R.id.select_file_button);

        mSelectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(mContext);
            }
        });
    }

    @SuppressLint("InflateParams")
    private void openFileChooser(Context context) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // file browser has been found on the device
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, SELECT_FILE_REQUEST);
        }
        // there is no any file browser app, let's try to download one
        else {
            final View customView = getLayoutInflater().inflate(R.layout.app_file_browser, null);
            final ListView lvApp = (ListView) customView.findViewById(android.R.id.list);
            lvApp.setAdapter(new FileBrowserAppsAdapter(context));
            lvApp.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lvApp.setItemChecked(0, true);

            new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT)
                    .setView(customView)
                    .setTitle("Select a file browser")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final int pos = lvApp.getCheckedItemPosition();
                    if (pos >= 0) {
                        final String query = getResources().getStringArray(R.array.app_file_browser_action)[pos];
                        final Intent storeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(query));
                        startActivity(storeIntent);
                    }
                }
            }).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case SELECT_FILE_REQUEST:
                // and read new one
                final Uri uri = data.getData();
                extractFileInfoFromUri(uri);
                break;
        }
    }

    private void extractFileInfoFromUri(final Uri uri) {
        final String path = uri.getPath();
        final File file = new File(path);
        boolean isFileExists = file.exists();
        String filePath = "";
        try {
            filePath = getPath(mContext, uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Log.e("test", "file : " + file.getName() + " / " + file.getPath() + " / " + isFileExists + " / " + file.getAbsolutePath() + "\n"
                + " / ");
        Log.e("test", "uri : " + uri.toString() + " / " + uri.getScheme() + " / " + uri.getPath());
        Log.e("test", "filePath : " + filePath);

        readFile(uri);
        mFileNameTextView.setText(file.getName());
    }

    public void readFile(Uri uri) {
        // new CSVReader(new InputStreamReader(new BufferedInputStream(getContentResolver().openInputStream(uri))));
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            assert inputStream != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
            reader.close();
            inputStream.close();
            Log.e("test", "result : " + stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPathFromUri(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        String path = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToNext();
            path = cursor.getString(cursor.getColumnIndex("_data"));
            cursor.close();
        }
        return path;
    }

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
