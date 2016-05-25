package app.bennsandoval.com.woodmin.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Mackbook on 12/23/14.
 */
public class WoodminAuthenticatorService  extends Service {

    private WoodminAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new WoodminAuthenticator(this);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
