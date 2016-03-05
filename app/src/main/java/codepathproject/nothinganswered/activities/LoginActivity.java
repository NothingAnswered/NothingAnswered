package codepathproject.nothinganswered.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import codepathproject.nothinganswered.R;
import codepathproject.nothinganswered.activities.Adapter.CardsAdapter;
import codepathproject.nothinganswered.activities.Model.Card;

public class LoginActivity extends AppCompatActivity {

    ArrayList<Card> cards;
    RecyclerView rvCards;

    private static final String TAG = LoginActivity.class.getSimpleName();

    private CallbackManager callbackManager;

    @Bind(R.id.login_button) LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();

        ButterKnife.bind(this);
        loginButton.setReadPermissions("user_friends");
        populateHomeScreen();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i(TAG, loginResult.toString());
                Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginActivity.this, "Login cancelled!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LoginActivity.this, "Login error to Facebook!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateHomeScreen()
    {
        rvCards = (RecyclerView) findViewById(R.id.rvCards);

        cards = Card.createCardsList(20);
        CardsAdapter adapter = new CardsAdapter(cards);
        rvCards.setAdapter(adapter);
        rvCards.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }
}
