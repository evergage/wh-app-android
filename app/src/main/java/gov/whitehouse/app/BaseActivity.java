package gov.whitehouse.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


import gov.whitehouse.app.wh.NoConnActivity;
import gov.whitehouse.app.wh.WHApp;
import gov.whitehouse.util.NetworkUtils;
import icepick.Icepick;

public abstract class BaseActivity extends ActionBarActivity {

    public static final int NO_LAYOUT = 0;

    public
    boolean checkNetworkElseFail()
    {
        if (!(this instanceof NoConnActivity)) {
            if (!NetworkUtils.checkNetworkAvailable(this)) {
                startActivity(new Intent(this, NoConnActivity.class));
                finish();
                return false;
            }
        }
        return true;
    }

    protected void
    onCreate(Bundle icicle, int layoutRes)
    {
        super.onCreate(icicle);
        Icepick.restoreInstanceState(this, icicle);
        setContentView(layoutRes);
    }

    @Override
    protected void
    onCreate(Bundle savedInstanceState)
    {
        onCreate(savedInstanceState, NO_LAYOUT);
    }

    @Override
    protected
    void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected
    void onPause()
    {
        super.onPause();
    }

    @Override
    protected
    void onResume()
    {
        super.onResume();
    }

    @Override
    protected void
    onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
