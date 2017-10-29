package de.aaronoe.greet.sync;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import de.aaronoe.greet.repository.PostsRepository;

public class WidgetSyncJob extends JobService {

    private AsyncTask mBackgroundTask;
    PostsRepository mRepo;

    private static final String TAG = "WidgetSyncJob";

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final JobParameters job) {

        Log.d(TAG, "onStartJob() called with: job = [" + job + "]");

        mBackgroundTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mRepo.refreshWidgetPosts();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                jobFinished(job, false);
            }
        };

        mBackgroundTask.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
