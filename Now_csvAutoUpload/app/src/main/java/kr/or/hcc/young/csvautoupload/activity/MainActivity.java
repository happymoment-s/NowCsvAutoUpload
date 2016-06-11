package kr.or.hcc.young.csvautoupload.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import kr.or.hcc.young.csvautoupload.Const;
import kr.or.hcc.young.csvautoupload.R;
import kr.or.hcc.young.csvautoupload.data.NowData;
import kr.or.hcc.young.csvautoupload.filechooser.FileBrowserAppsAdapter;
import kr.or.hcc.young.csvautoupload.filechooser.NowUploadAsyncTask;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_FILE_REQUEST = 1;
    private Context mContext;

    private TextView mFileNameTextView;
    private Button mSelectFileButton;
    private Button mUploadButton;

    private List<NowData> mNowDataList;

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
        mUploadButton = (Button) findViewById(R.id.upload_button);

        mSelectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser(mContext);
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNowDataList != null && mNowDataList.size() > 0) {
                    // TODO 서버 업로드 + progressDialog AsyncTask
                    new NowUploadAsyncTask(mContext, mNowDataList).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "올바른 파일이 선택되지 않았습니다.", Toast.LENGTH_LONG).show();
                }
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
        // file name 추출
        String fileName = new File(uri.getPath()).getName();
        // 파일 확장자가 csv가 아니면 에러처리
        if (!fileName.contains(".csv")) {
            showErrorToast(mContext, Const.ERROR_NOT_CSV_FILE);
            return;
        }

        // set file name
        mFileNameTextView.setText(fileName);
        // csv parsing
        mNowDataList = extractNowDataFromCsvFile(uri);

        // Log
        if (mNowDataList != null && mNowDataList.size() > 0) {
            for (NowData nowData : mNowDataList) {
                Log.i("Test", nowData.toString());
            }
        } else {
            Log.i("Test", "데이터 없음");
        }
    }

    private List<NowData> extractNowDataFromCsvFile(Uri uri) {
        List<NowData> nowDataList = new ArrayList<>();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(new BufferedInputStream(getContentResolver().openInputStream(uri)), "MS949"), '$');
            String[] nextLine;
            int date = 0, title = 1, content = 2, explanation = 3; // default index 설정
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length < 4) continue;
                if (nextLine[0].contains("date")) { //csv 파일에 해당명칭으로 설정되어있다면 index 재설정
                    for (int i = 0; i < nextLine.length; ++i) {
                        if (nextLine[i].contains("date")) date = i;
                        else if (nextLine[i].contains("title")) title = i;
                        else if (nextLine[i].contains("content")) content = i;
                        else if (nextLine[i].contains("explanation")) explanation = i;
                    }
                    if (date < 0 || title < 0 || content < 0 || explanation < 0) break;
                    continue;
                }

                String dateString = nextLine[date];
                String titleString = nextLine[title];
                String contentString = nextLine[content];
                String explanationString = nextLine[explanation];
                NowData nowData = new NowData(dateString, titleString, contentString, explanationString);
                nowDataList.add(nowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Exception 발생시
            showErrorToast(mContext, Const.ERROR_CSV_PARSING);
        }

        // parsing 후 arrayList에 아무것도 들어있지 않을 경우
        if (nowDataList.size() <= 0) {
            showErrorToast(mContext, Const.ERROR_CSV_PARSING);
        }
        return nowDataList;
    }

    private void showErrorToast(Context context, int errorCode) {
        String message = "";
        switch (errorCode) {
            case Const.ERROR_NOT_CSV_FILE:
                message = "csv파일이 아닙니다.";
                break;
            case Const.ERROR_CSV_PARSING:
                message = "csv파일 parsing에 실패하였습니다.";
                break;
            case Const.ERROR_SERVER_UPLOAD:
                message = "서버 업로드에 실패하였습니다.";
                break;
        }
        Toast.makeText(context, errorCode + " / " + message, Toast.LENGTH_SHORT).show();
    }
}
