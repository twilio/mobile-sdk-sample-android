package com.twilio.authsample.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.twilio.auth.TwilioAuth;
import com.twilio.authsample.App;
import com.twilio.authsample.R;
import com.twilio.authsample.main.MainActivity;
import com.twilio.authsample.network.SampleApi;
import com.twilio.authsample.network.model.RegistrationTokenResponse;
import com.twilio.authsample.utils.AuthyActivityListener;
import com.twilio.authsample.utils.AuthyTask;
import com.twilio.authsample.utils.MessageHelper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A registration screen.
 */
public class RegistrationActivity extends AppCompatActivity {

    public static final String EXTRA_ERROR_MESSAGE_ID = "error_message";
    private TwilioAuth twilioAuth;
    private Button registerDeviceButton;
    private EditText authyId;
    private EditText backendUrl;
    private View buttonContainer;

    private ProgressDialog progressDialog;

    private Call<RegistrationTokenResponse> registrationTokenCall;

    private MessageHelper messageHelper;

    private AuthyActivityListener<Void> registerDeviceListener = new AuthyActivityListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            updateProgressDialog(false);
            startMainActivity();
        }

        @Override
        public void onError(Exception exception) {
            registerDeviceButton.setEnabled(true);
            updateProgressDialog(false);
            Toast.makeText(RegistrationActivity.this, R.string.registration_error_verification, Toast.LENGTH_LONG).show();
        }
    };

    public static void startRegistrationActivity(Activity activity, @StringRes int errorMessageId) {
        Intent registrationIntent = new Intent(activity, RegistrationActivity.class);
        registrationIntent.putExtra(EXTRA_ERROR_MESSAGE_ID, errorMessageId);
        registrationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(registrationIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initViews();
        initData();

        if (twilioAuth.isDeviceRegistered()) {
            startMainActivity();
        } else {
            startRegistrationProcess();
        }
    }

    @Override
    protected void onStop() {
        progressDialog.dismiss();
        messageHelper.dismiss();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (registrationTokenCall != null && !registrationTokenCall.isCanceled()) {
            registrationTokenCall.cancel();
        }
        super.onDestroy();
    }

    public void startMainActivity() {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initData() {
        twilioAuth = ((App) getApplicationContext()).getTwilioAuth();

        int errorMessageId = getIntent().getIntExtra(EXTRA_ERROR_MESSAGE_ID, -1);
        if (errorMessageId != -1) {
            messageHelper.show(buttonContainer, errorMessageId);
            // Make sure that the keyboard is not automatically opened
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private void initViews() {
        messageHelper = new MessageHelper();
        registerDeviceButton = (Button) findViewById(R.id.registerDeviceButton);
        authyId = (EditText) findViewById(R.id.authy_id);
        backendUrl = (EditText) findViewById(R.id.backend_url);
        buttonContainer = findViewById(R.id.buttonContainer);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading_msg));
    }

    private void updateProgressDialog(boolean show) {
        if (show) {
            progressDialog.show();
        } else if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void startRegistrationProcess() {
        registerDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterDeviceButtonClicked();
            }
        });
    }

    private void onRegisterDeviceButtonClicked() {
        hideKeyboard();
        authyId.setError(null);
        backendUrl.setError(null);

        String authyIdString = authyId.getText().toString();
        String backendUrlString = backendUrl.getText().toString();

        if (TextUtils.isEmpty(authyIdString)) {
            authyId.setError(getString(R.string.registration_error_invalid_field));
            return;
        }

        if (TextUtils.isEmpty(backendUrlString)) {
            backendUrl.setError(getString(R.string.registration_error_invalid_field));
            return;
        }

        if (!backendUrlString.startsWith("https://")) {
            backendUrl.setError(getString(R.string.registration_error_invalid_field));
            return;
        }

        if (!Patterns.WEB_URL.matcher(backendUrlString).matches()) {
            backendUrl.setError(getString(R.string.registration_error_invalid_field));
            return;
        }

        // Fetch registration token
        registerDeviceButton.setEnabled(false);
        fetchRegistrationToken(backendUrlString, authyIdString);
    }

    private void hideKeyboard() {
        View focusedView = getCurrentFocus();

        if (focusedView == null) {
            return;
        }

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void fetchRegistrationToken(String backendUrl, final String authyId) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(backendUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final SampleApi sampleApi = retrofit.create(SampleApi.class);

        callGetRegistrationToken(authyId, sampleApi);
    }

    private void callGetRegistrationToken(String authyId, SampleApi sampleApi) {
        registrationTokenCall = sampleApi.getRegistrationToken(authyId);
        updateProgressDialog(true);
        registrationTokenCall.enqueue(new Callback<RegistrationTokenResponse>() {
            @Override
            public void onResponse(Call<RegistrationTokenResponse> call, Response<RegistrationTokenResponse> response) {
                onRegistrationTokenReceived(response);
            }

            @Override
            public void onFailure(Call<RegistrationTokenResponse> call, Throwable t) {
                onRegistrationTokenError(t);
            }
        });
    }

    private void onRegistrationTokenError(Throwable t) {
        registerDeviceButton.setEnabled(true);
        updateProgressDialog(false);

        messageHelper.show(buttonContainer, R.string.registration_error_verification);
        Log.e(RegistrationActivity.class.getSimpleName(), "Unable to get a registration token", t);
    }

    private void onRegistrationTokenReceived(Response<RegistrationTokenResponse> response) {
        registerDeviceButton.setEnabled(true);
        updateProgressDialog(false);

        if (response == null || response.body() == null) {
            Log.e(RegistrationActivity.class.getSimpleName(), "Unable to get a registration token, please check that the provided authy_id is added to your app");
            messageHelper.show(buttonContainer, R.string.registration_error_verification);
            return;
        }
        RegistrationTokenResponse registrationTokenResponse = response.body();

        registerDevice(registrationTokenResponse.getRegistrationToken());
    }

    private void registerDevice(final String registrationToken) {
        updateProgressDialog(true);
        new AuthyTask<Void>(registerDeviceListener) {
            @Override
            public Void executeOnBackground() {
                twilioAuth.registerDevice(registrationToken, FirebaseInstanceId.getInstance().getToken());
                return null;
            }
        }.execute();
    }
}

