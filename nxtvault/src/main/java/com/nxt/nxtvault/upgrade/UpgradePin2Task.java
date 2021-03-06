package com.nxt.nxtvault.upgrade;

import android.content.Context;
import android.webkit.ValueCallback;

import com.nxt.nxtvault.JayClientApi;
import com.nxt.nxtvault.R;
import com.nxt.nxtvault.preference.PreferenceManager;
import com.nxt.nxtvaultclientlib.jay.IJavascriptLoadedListener;

/**
 *  on 5/30/2015.
 */
public class UpgradePin2Task implements IUpgradeTask{
    PreferenceManager mPreferences;
    Context mContext;
    JayClientApi mJay;

    public UpgradePin2Task(Context context, PreferenceManager preferenceManager, JayClientApi jay){
        mPreferences = preferenceManager;
        mContext = context;
        mJay = jay;
    }

    @Override
    public void upgrade(int fromVersion, final ValueCallback<Void> callback) {
        final com.nxt.nxtvault.legacy.JayClientApi legacyClient = new com.nxt.nxtvault.legacy.JayClientApi(mContext);
        legacyClient.addReadyListener(new IJavascriptLoadedListener() {
            @Override
            public void onLoaded() {
                legacyClient.hasPin(new ValueCallback<Boolean>() {
                    @Override
                    public void onReceiveValue(Boolean value) {
                        if (value){
                            mPreferences.getSharedPref().edit().putBoolean(mContext.getString(R.string.pin2upgraderequired), true).commit();
                        }

                        callback.onReceiveValue(null);
                    }
                });
            }
        });
    }

    @Override
    public boolean requiresUpgrade(int fromVersion) {
        return (fromVersion <= 10 && !mPreferences.getPinIsSet() );
    }
}
