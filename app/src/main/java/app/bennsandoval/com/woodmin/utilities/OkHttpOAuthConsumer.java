package app.bennsandoval.com.woodmin.utilities;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import okhttp3.Request;
import se.akerfeldt.okhttp.signpost.OkHttpRequestAdapter;

/**
 * Created by Mackbook on 3/28/16.
 */
public class OkHttpOAuthConsumer extends AbstractOAuthConsumer {

    public OkHttpOAuthConsumer(String consumerKey, String consumerSecret) {
        super(consumerKey, consumerSecret);
    }

    @Override
    protected HttpRequest wrap(Object request) {
        if (!(request instanceof Request)) {
            throw new IllegalArgumentException("This consumer expects requests of type " + Request.class.getCanonicalName());
        }
        return new OkHttpRequestAdapter((Request) request);
    }
}