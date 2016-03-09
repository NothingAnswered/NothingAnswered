package codepathproject.nothinganswered.services;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by gpalem on 3/8/16.
 */
// The types specified here are the input data type, the progress type, and the result type
public class UploadVideoAsyncTask extends AsyncTask<String, Void, String> {
    protected void onPreExecute() {
        // Runs on the UI thread before doInBackground
        // Good for toggling visibility of a progress indicator
        //progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    protected String doInBackground(String... strings) {
        // Some long-running task like downloading an image.
        return "";
    }

    protected void onPostExecute(Bitmap result) {
        // This method is executed in the UIThread
        // with access to the result of the long running task
        // Hide the progress bar
        //progressBar.setVisibility(ProgressBar.INVISIBLE);
    }
}
