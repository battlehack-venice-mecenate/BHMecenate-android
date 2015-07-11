package com.battlehack_venice.mecenate.monument;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.battlehack_venice.lib.Donation;
import com.battlehack_venice.lib.Monument;
import com.battlehack_venice.lib.api.ApiClient;
import com.battlehack_venice.lib.api.ApiReponseStringParser;
import com.battlehack_venice.lib.api.ApiResponseEntityParser;
import com.battlehack_venice.lib.utils.ImageLoader;
import com.battlehack_venice.mecenate.Application;
import com.battlehack_venice.mecenate.BaseActivity;
import com.battlehack_venice.mecenate.R;
import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.Customization;

import java.io.Serializable;
import java.text.NumberFormat;
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
    @InjectView(R.id.monument_button_1)
    Button _button1;
    @InjectView(R.id.monument_button_2)
    Button _button2;
    @InjectView(R.id.monument_button_3)
    Button _button3;
    @InjectView(R.id.monument_donations)
    TextView _donations;

    @Inject
    ImageLoader _imageLoader;
    @Inject
    ApiClient _apiClient;

    private Monument _monument;
    private String _ppClientToken;
    private int _amount; // shit

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

        this._monument = monument;

        this._name.setText(monument.getName());
        this._description.setText(monument.getDescription());
        this._imageLoader.loadImage(monument.getImageUrl(), this._coverImage);
        this._donations.setText(monument.getTotalDonations() > 0 ? String.format("Collected %.2f", monument.getTotalDonations()/100.0f): "No donations yet.");

        this._preparePaypalTransaction();
    }

    private void _preparePaypalTransaction()
    {
        _button1.setEnabled(false);
        _button2.setEnabled(false);
        _button3.setEnabled(false);

        this._apiClient.get("/client_token", null, new ApiReponseStringParser("client_token"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>()
                {
                    @Override
                    public void onCompleted()
                    {
                        _button1.setEnabled(true);
                        _button2.setEnabled(true);
                        _button3.setEnabled(true);
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

    @OnClick(R.id.monument_button_1)
    void _onButton1Click(View v)
    {
        this._pay(1);
    }

    @OnClick(R.id.monument_button_2)
    void _onButton2Click(View v)
    {
        this._pay(5);
    }

    @OnClick(R.id.monument_button_3)
    void _onButton3Click(View v)
    {
        this._pay(10);
    }

    private void _pay(int amount)
    {
        Log.i("PAYMENT", "request payment of " + amount + " bucs");
        this._amount = amount;

        Intent intent = new Intent(this, BraintreePaymentActivity.class)
                .putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, this._ppClientToken);

        Customization customization = new Customization.CustomizationBuilder()
                .primaryDescription("" + this._monument.getName())
                .secondaryDescription("Contribute for repair this monument")
                .amount(NumberFormat.getCurrencyInstance().format(amount))
                .submitButtonText("Donate")
                .build();
        intent.putExtra(BraintreePaymentActivity.EXTRA_CUSTOMIZATION, customization);

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

        switch (resultCode) {
            case BraintreePaymentActivity.RESULT_OK:
                String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                _sendPaymentNonceToServer(this._monument.getId(), paymentMethodNonce, this._amount);
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

    private void _sendPaymentNonceToServer(long poiId, String nonce, int money)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("payment_method_nonce", nonce);
        params.put("amount_in_cents", "" + money * 100);

        this._apiClient.post("/pois/" + poiId + "/donations", null, params, new ApiResponseEntityParser<Donation>(Donation.PARSER, "donation"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Donation>()
                {
                    @Override
                    public void onCompleted()
                    {
                        Log.i("PAYMENT", "onCompleted");

                        Toast.makeText(MonumentActivity.this, "Thanks for donating!", Toast.LENGTH_SHORT).show();

                        _hydrate(_monument);
                        _preparePaypalTransaction();
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("PAYMENT", e.getMessage(), e);

                        Toast.makeText(MonumentActivity.this, "Something went wrong, sorry!", Toast.LENGTH_LONG).show();

                        _preparePaypalTransaction();
                    }

                    @Override
                    public void onNext(Donation donation)
                    {
                        _monument.addDonation(donation.getAmount());

                        Log.i("PAYMENT", "onNext " + donation.getAmount());
                    }
                });
    }
}
