package app.bennsandoval.com.woodmin.utilities;

import android.util.Log;

import java.io.IOException;

import oauth.signpost.exception.OAuthException;
import oauth.signpost.http.HttpRequest;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Mackbook on 3/28/16.
 */
public class SigningInterceptor implements Interceptor {

    public final String LOG_TAG = SigningInterceptor.class.getSimpleName();
    private final OkHttpOAuthConsumer consumer;

    public SigningInterceptor(OkHttpOAuthConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        try {
            HttpRequest httpRequest = consumer.sign(request);
            Request authenticateRequest = (Request) httpRequest.unwrap();
            return chain.proceed(authenticateRequest);
        } catch (OAuthException e) {
            Log.e(LOG_TAG, "Error " + e.getMessage());
            throw new IOException("Could not sign request", e);
        }
    }
}