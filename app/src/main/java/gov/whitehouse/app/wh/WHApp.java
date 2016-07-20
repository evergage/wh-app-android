package gov.whitehouse.app.wh;

import android.app.Application;
import android.content.Intent;

import com.bugsnag.android.Bugsnag;
import com.google.android.gms.common.GooglePlayServicesUtil;

import gov.whitehouse.BuildConfig;
import timber.log.Timber;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public
class WHApp extends Application
{

    private
    void configureBugsnag()
    {
        Bugsnag.register(this, "77d9c523c3567a4537c341cb1dd06c09");
        Bugsnag.setReleaseStage(BuildConfig.BUILD_TYPE);
        Bugsnag.setProjectPackages("gov.whitehouse");
    }

    private
    void pokeLiveService()
    {
        final Intent sIntent = new Intent(this, LiveService.class);
        startService(sIntent);
    }

    @Override
    public
    void onCreate()
    {
        super.onCreate();

        configureBugsnag();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
        WHPrefs.initPrefs(this);
        pokeLiveService();
        if (WHPrefs.optInToAnalytics.getValue()) {
        }
    }

    private static
    class CrashReportingTree extends Timber.HollowTree
    {
        private static final
        String SEVERITY_ERROR = "error";

        @Override
        public
        void e(Throwable t, String message, Object... args)
        {
            String infoString = String.format(message, args);
            if (t != null) {
                Bugsnag.notify(new Throwable(infoString, t), SEVERITY_ERROR);
            } else {
                Bugsnag.notify(new Throwable(infoString), SEVERITY_ERROR);
            }
        }
    }
}
