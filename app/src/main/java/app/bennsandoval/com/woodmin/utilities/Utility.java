package app.bennsandoval.com.woodmin.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import app.bennsandoval.com.woodmin.R;

/**
 * Created by Mackbook on 12/23/14.
 */
public class Utility {

    public static String getPreferredUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_user_key),null);
    }

    public static String getPreferredSecret(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_secret_key),null);
    }

    public static Long getPreferredLastSync(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(context.getString(R.string.pref_last_update_key), 0);
    }

    public static String getPreferredServer(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_server), null);
    }

    public static Long setPreferredLastSync(Context context, Long time) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(context.getString(R.string.pref_last_update_key), time);
        editor.apply();
        return prefs.getLong(context.getString(R.string.pref_last_update_key), 0);
    }

    public static void setPreferredUserSecret(Context context, String user, String secret) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_user_key), user);
        editor.putString(context.getString(R.string.pref_secret_key), secret);
        editor.apply();
    }

    public static String setPreferredServer(Context context, String server) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_server), server);
        editor.apply();
        return prefs.getString(context.getString(R.string.pref_server),null);
    }

    public static String getPreferredShoppingCard(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_shopping_cart),null);
    }

    public static String setPreferredShoppingCard(Context context, String json) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.pref_shopping_cart), json);
        editor.apply();
        return prefs.getString(context.getString(R.string.pref_shopping_cart),null);
    }

    public static SSLSocketFactory getSSLSocketFactory(){
        //TODO USE THIS ONLY FOR TESTING
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc.getSocketFactory();
        }catch (Exception ex){

        }
        return null;
    }

    public static HostnameVerifier getHostnameVerifier(){
        //TODO USE THIS ONLY FOR TESTING
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        return allHostsValid;
    }
}
