package com.battlehack_venice.mecenate.monument;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.battlehack_venice.lib.api.ApiClient;
import com.battlehack_venice.lib.api.ApiReponseStringParser;
import com.battlehack_venice.lib.api.ApiResponseEntityParser;
import com.battlehack_venice.lib.model.Donation;
import com.battlehack_venice.lib.model.Monument;
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
import rx.Subscription;
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
    private Subscription _subscription;
    private Subscription _reloadSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.monument_activity);

        ButterKnife.inject(this);
        Application.injector().inject(this);

        if (getIntent() != null) {

            Monument monument = (Monument) getIntent().getSerializableExtra(EXTRA_MONUMENT);
            this._hydrate(monument);
            this._reload(monument);
        }
    }

    private void _reload(Monument monument)
    {
        this._reloadSubscription = this._apiClient.get("/pois/"+monument.getId(), null, new ApiResponseEntityParser<Monument>(Monument.PARSER, "monument"))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Monument>()
        {
            @Override
            public void onCompleted()
            {
                // Nothing
            }

            @Override
            public void onError(Throwable e)
            {
                // TODO:
            }

            @Override
            public void onNext(Monument monument)
            {
                _hydrate(monument);
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        if (this._subscription != null) {
            this._subscription.unsubscribe();
            this._subscription = null;
        }

        if (this._reloadSubscription != null) {
            this._reloadSubscription.unsubscribe();
            this._reloadSubscription = null;
        }

        super.onDestroy();
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

        String collected = monument.getTotalDonations() > 0 ? String.format("Collected $ %.2f", monument.getTotalDonations() / 100.0f) : "No donations yet. Be the first!";
        String target = String.format("Target $ %.2f", monument.getTarget() / 100.0f);
        this._donations.setText(collected+". "+target);

        this._preparePaypalTransaction();
    }

    private void _preparePaypalTransaction()
    {
        _button1.setEnabled(false);
        _button2.setEnabled(false);
        _button3.setEnabled(false);

        this._subscription = this._apiClient.get("/client_token", null, new ApiReponseStringParser("client_token"))
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
        if (requestCode != PAYMENT_REQUEST || data == null) {
            return;
        }

        switch (resultCode) {
            case BraintreePaymentActivity.RESULT_OK:
                String paymentMethodNonce = data.getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);
                this._askEmail(this._monument.getId(), paymentMethodNonce, this._amount);
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
                Toast.makeText(MonumentActivity.this, "Something went wrong...", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void _sendPaymentNonceToServer(long poiId, String nonce, int money, String email)
    {
        HashMap<String, String> params = new HashMap<>();
        params.put("payment_method_nonce", nonce);
        params.put("amount_in_cents", "" + money * 100);
        if (!TextUtils.isEmpty(email)) {
            params.put("email", email);
        }

        this._subscription = this._apiClient.post("/pois/" + poiId + "/donations", null, params, new ApiResponseEntityParser<Donation>(Donation.PARSER, "donation"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Donation>()
                {
                    @Override
                    public void onCompleted()
                    {
                        Log.i("PAYMENT", "onCompleted");

                        Toast.makeText(MonumentActivity.this, "Donation received! Thanks!", Toast.LENGTH_SHORT).show();

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

    private void _askEmail(final long poiId, final String nonce, final int money)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thanks for donating!");
        builder.setMessage("Want to win a ticket for an exclusive event around the monument? Type in your email");

        // Set up the input
        final EditText input = new EditText(this);
        input.setHint("Optional");

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        input.setText(sharedPref.getString("email", null));

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setPadding(30, 30, 30, 30);

        input.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.addView(input);

        builder.setView(linearLayout);

        // Set up the buttons
        builder.setPositiveButton("Absolutely", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String email = input.getText().toString();
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("email", email);
                editor.commit();

                _sendPaymentNonceToServer(poiId, nonce, money, email);
            }
        });
        builder.setNegativeButton("No, Thanks", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                _sendPaymentNonceToServer(poiId, nonce, money, null);
                dialog.cancel();
            }
        });

        builder.show();
    }
}
