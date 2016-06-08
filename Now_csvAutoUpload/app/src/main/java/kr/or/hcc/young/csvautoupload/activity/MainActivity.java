package kr.or.hcc.young.csvautoupload.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import kr.or.hcc.young.csvautoupload.R;
import kr.or.hcc.young.csvautoupload.filechooser.FileBrowserAppsAdapter;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_FILE_REQ = 1;
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
            startActivityForResult(intent, SELECT_FILE_REQ);
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
}
