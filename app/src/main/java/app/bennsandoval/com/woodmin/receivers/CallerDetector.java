package app.bennsandoval.com.woodmin.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import app.bennsandoval.com.woodmin.activities.MainActivity;
import app.bennsandoval.com.woodmin.data.WoodminContract;
import app.bennsandoval.com.woodmin.services.HeadInfoService;
import app.bennsandoval.com.woodmin.utilities.Utility;

/**
 * Created by Mackbook on 1/10/15.
 */
public class CallerDetector extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(Utility.getPreferredServer(context)!= null){
                Intent intentHeader= new Intent(context, HeadInfoService.class);
                intentHeader.putExtra("search",incomingNumber);
                context.startService(intentHeader);

            }
        }
    }
}
