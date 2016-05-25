package app.bennsandoval.com.woodmin;

import android.app.Application;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import app.bennsandoval.com.woodmin.interfaces.Woocommerce;
import app.bennsandoval.com.woodmin.utilities.Utility;
import io.fabric.sdk.android.Fabric;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mackbook on 1/10/15.
 */
public class Woodmin extends Application {

    public final String LOG_TAG = Woodmin.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Picasso.Builder picassoBuilder = new Picasso.Builder(getApplicationContext());
        Picasso picasso = picassoBuilder.build();
        //picasso.setIndicatorsEnabled(true);
        try {
            Picasso.setSingletonInstance(picasso);
        } catch (IllegalStateException ignored) {
            Log.e(LOG_TAG, "Picasso instance already used");
        }
    }

    public Woocommerce getWoocommerceApiHandler() {

        final String key = Utility.getPreferredUser(getApplicationContext());
        final String secret = Utility.getPreferredSecret(getApplicationContext());

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.MILLISECONDS)
                .readTimeout(60000, TimeUnit.MILLISECONDS)
                .cache(null);

        //TODO Remove this if you don't have a self cert
        /*
        if(Utility.getSSLSocketFactory() != null){
            clientBuilder
                    .sslSocketFactory(Utility.getSSLSocketFactory())
                    .hostnameVerifier(Utility.getHostnameVerifier());
        }
        */

        Interceptor basicAuthenticatorInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                String authenticationHeader = "Basic " + Base64.encodeToString(
                        (key + ":" + secret).getBytes(),
                        Base64.NO_WRAP);

                Request authenticateRequest = request.newBuilder()
                        .addHeader("Authorization", authenticationHeader)
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(authenticateRequest);
            }
        };


        //OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(key, secret);
        //consumer.setSigningStrategy(new QueryStringSigningStrategy());
        //clientBuilder.addInterceptor(new SigningInterceptor(consumer));
        clientBuilder.addInterceptor(basicAuthenticatorInterceptor);

        String server = Utility.getPreferredServer(getApplicationContext());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                .build();

        return retrofit.create(Woocommerce.class);
    }

}
