package kr.or.hcc.young.csvautoupload.filechooser;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import kr.or.hcc.young.csvautoupload.Const;
import kr.or.hcc.young.csvautoupload.data.NowData;

public class NowUploadAsyncTask extends AsyncTask<Integer, Integer, Integer> {

    private List<NowData> mNowDataList;
    private Context mContext;
    private ProgressDialog mProgressDialog;
    private int mMaxSize;
    private int mSuccessCount = 0; // 서버 업로드 성공시 count 누적


    public NowUploadAsyncTask(Context context, List<NowData> nowDataList) {
        this.mNowDataList = nowDataList;
        this.mContext = context;
        if (nowDataList != null) {
            mMaxSize = nowDataList.size();
        }
        mProgressDialog = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMax(mMaxSize);
        mProgressDialog.show(); //ProgressDialog 보여주기
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        if (mNowDataList == null) {
            return Const.ERROR_SERVER_UPLOAD;
        }
        int currentIndex = 0;
        while (currentIndex < mMaxSize) {
            // TODO rest api 호출
            // progress 상태 변경
            // doInBackground는 ui thread가 아니기 때문에 progress ui 변경 불가능
            publishProgress(currentIndex);

            // 성공시 index +1
            currentIndex++;

            // test code
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return Const.SUCCESS_SERVER_UPLOAD;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mProgressDialog != null) {
            mProgressDialog.setProgress(values[0]);

            NowData nowData = mNowDataList.get(values[0]);
            String message = nowData.getDate() + " " + nowData.getTitle() + " 업로드 중입니다..";
            mProgressDialog.setMessage(message);
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        // TODO 업로드 성공한 total 개수 호출되도록 변경 필요
        if (integer == Const.SUCCESS_SERVER_UPLOAD) {
            Toast.makeText(mContext, "총 " + mMaxSize + "개의 묵상이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
        } else if (integer == Const.ERROR_SERVER_UPLOAD) {
            Toast.makeText(mContext, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
