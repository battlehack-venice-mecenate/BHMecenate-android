package com.battlehack_venice.mecenate.monument;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.battlehack_venice.lib.Monument;
import com.battlehack_venice.lib.api.ApiClient;
import com.battlehack_venice.lib.api.ApiResponseEntityParser;
import com.battlehack_venice.lib.api.ApiResponseTextParser;
import com.battlehack_venice.lib.api.ApiReponseStringParser;
import com.battlehack_venice.lib.utils.ImageLoader;
import com.battlehack_venice.mecenate.Application;
import com.battlehack_venice.mecenate.BaseActivity;
import com.battlehack_venice.mecenate.R;
import com.braintreepayments.api.dropin.BraintreePaymentActivity;

import java.io.Serializable;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MonumentActivity extends BaseActivity
{
    public static final String EXTRA_MONUMENT = "monument";
    private static final int PAYMENT_REQUEST = RESULT_FIRST_USER + 1000;

    @InjectView(R.id.monument_image)
    ImageView _coverImage;
    @InjectView(R.id.monument_title)
    TextView _name;
    @InjectView(R.id.monument_description)
    TextView _description;
    @InjectView(R.id.monument_button)
    Button _button;

    @Inject
    ImageLoader _imageLoader;
    @Inject
    ApiClient _apiClient;

    private String _ppClientToken;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.monument_activity);

        ButterKnife.inject(this);
        Application.injector().inject(this);

        if (getIntent() != null) {
            this._hydrate((Monument) getIntent().getSerializableExtra(EXTRA_MONUMENT));
        }
    }

    private void _hydrate(Monument monument)
    {
        if (monument == null) {
            return;
        }

        this._name.setText(monument.getName());
        this._description.setText(monument.getDescription());
        this._imageLoader.loadImage(monument.getImageUrl(), this._coverImage);

        this._initPaypal();
    }

    private void _initPaypal()
    {
        this._apiClient.get("/client_token", null, new ApiReponseStringParser("client_token"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>()
                {
                    @Override
                    public void onCompleted()
                    {
                        _button.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Toast.makeText(MonumentActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String s)
                    {
                        _ppClientToken = s;
                        Log.d("PAYMENT", "client token: " + s);
                    }
                });
    }

    @OnClick(R.id.monument_button)
    void _onButtonClick(View v)
    {
        Intent intent = new Intent(this, BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, this._ppClientToken);

        /*
        Customization customization = new Customization.CustomizationBuilder()
                .amount(1)
                .build();
        intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION, customization);
        */

        startActivityForResult(intent, PAYMENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != PAYMENT_REQUEST) {
            return;
        }

        if (getIntent() == null) {
            return;
        }

        Monument monument = (Monument) getIntent().getSerializableExtra(EXTRA_MONUMENT);

        switch (resultCode) {
            case BraintreePaymentActivity.RESULT_OK:
                String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                _sendPaymentNonceToServer(monument.getId(), paymentMethodNonce, 100);
                break;

            case BraintreePaymentActivity.BRAINTREE_RESULT_DEVELOPER_ERROR:
            case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_ERROR:
            case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_UNAVAILABLE:
                // handle errors here, a throwable may be available in
                Serializable msg = data.getSerializableExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE);
                Toast.makeText(MonumentActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                break;

            case BraintreePaymentActivity.RESULT_CANCELED:
                break;

            default:
                Log.i("onActivityResult", "" + resultCode + " " + data);
                Toast.makeText(MonumentActivity.this, "Something went wrong... Badly", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void _sendPaymentNonceToServer(long poiId, String nonce, int cents)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("payment_method_nonce", nonce);
        params.put("amount_in_cents", "" + cents);

        this._apiClient.post("/pois/"+poiId+"/donations", null, params, new ApiResponseTextParser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>()
                {
                    @Override
                    public void onCompleted()
                    {
                        Log.i("PAYMENT", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("PAYMENT", e.getMessage(), e);
                    }

                    @Override
                    public void onNext(String s)
                    {
                        Log.i("PAYMENT", "onNext " + s);
                    }
                });
    }
}
