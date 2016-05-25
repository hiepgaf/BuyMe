package app.bennsandoval.com.woodmin.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app.bennsandoval.com.woodmin.R;
import app.bennsandoval.com.woodmin.Woodmin;
import app.bennsandoval.com.woodmin.interfaces.Woocommerce;
import app.bennsandoval.com.woodmin.models.shop.Shop;
import app.bennsandoval.com.woodmin.sync.WoodminSyncAdapter;
import app.bennsandoval.com.woodmin.utilities.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public final String LOG_TAG = LoginActivity.class.getSimpleName();

    private EditText mServerView;
    private EditText mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utility.getPreferredServer(getApplicationContext())!= null){
            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main);
            finish();
        } else {
            setContentView(R.layout.activity_login);

            // Set up the login form.
            mServerView = (EditText) findViewById(R.id.server);
            mUserView = (EditText) findViewById(R.id.user);
            mPasswordView = (EditText) findViewById(R.id.password);

            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
            mSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }
    }

    public void attemptLogin() {

        // Reset errors.
        mServerView.setError(null);
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String server = mServerView.getText().toString();
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid server address.
        if (TextUtils.isEmpty(server) && !isValid(server)) {
            mServerView.setError(getString(R.string.error_field_length));
            focusView = mServerView;
            cancel = true;
        }

        // Check for a valid user address.
        if (TextUtils.isEmpty(user) && !isValid(user)) {
            mUserView.setError(getString(R.string.error_field_length));
            focusView = mUserView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isValid(password)) {
            mPasswordView.setError(getString(R.string.error_field_length));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            userLogin(server, user, password);
        }
    }


    private boolean isValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void userLogin (String server, String user, String password){

        showProgress(true);
        Utility.setPreferredServer(getApplicationContext(), server);
        Utility.setPreferredUserSecret(getApplicationContext(), user, password);

        Woocommerce woocommerceApi = ((Woodmin) getApplication()).getWoocommerceApiHandler();
        Call<Shop> call = woocommerceApi.getShop();
        call.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    showProgress(false);

                    WoodminSyncAdapter.initializeSyncAdapter(getApplicationContext());
                    WoodminSyncAdapter.syncImmediately(getApplicationContext());

                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                    finish();
                } else {
                    showProgress(false);
                    mUserView.setError(getString(R.string.error_incorrect));
                    mPasswordView.setError(getString(R.string.error_incorrect));
                    mServerView.setError(getString(R.string.error_incorrect));
                    mServerView.requestFocus();

                    Utility.setPreferredServer(getApplicationContext(), null);
                    Utility.setPreferredUserSecret(getApplicationContext(), null, null);
                }
            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {
                showProgress(false);
                mUserView.setError(getString(R.string.error_incorrect));
                mPasswordView.setError(getString(R.string.error_incorrect));
                mServerView.setError(getString(R.string.error_incorrect));
                mServerView.requestFocus();

                Utility.setPreferredServer(getApplicationContext(), null);
                Utility.setPreferredUserSecret(getApplicationContext(), null, null);
            }
        });
    }
}



