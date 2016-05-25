package app.bennsandoval.com.woodmin.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Mackbook on 12/23/14.
 */
public class WoodminSyncService extends Service {

    public final String LOG_TAG = WoodminSyncService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static WoodminSyncAdapter sWoodminSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");
        synchronized (sSyncAdapterLock) {
            if (sWoodminSyncAdapter == null) {
                sWoodminSyncAdapter = new WoodminSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sWoodminSyncAdapter.getSyncAdapterBinder();
    }

}
