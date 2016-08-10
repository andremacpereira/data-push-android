package andre.com.datapushandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;

import andre.com.datapushandroid.tasks.EncryptionOperation;
import andre.com.datapushandroid.interfaces.EncryptionResponseInterface;
import andre.com.datapushandroid.services.FCMService;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, EncryptionResponseInterface {

    public static final String MY_PREFS_NAME = "PushData";

    private String restoredId = "";

    MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declare Button references
        Button logLastMessageButton = (Button) findViewById(R.id.logLastMessageButton);
        Button logTokenButton = (Button) findViewById(R.id.logTokenButton);

        // Set respective ClickListeners
        logLastMessageButton.setOnClickListener(this);
        logTokenButton.setOnClickListener(this);

        // Display Push Notification
        showLastReceivedMessage();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub

        // Register BroadcastReceiver
        // to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FCMService.MY_ACTION);
        registerReceiver(myReceiver, intentFilter);

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationState.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationState.activityPaused();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.logTokenButton:
            {
                // Get token
                String token = FirebaseInstanceId.getInstance().getToken();

                // Log
                Log.i("YOUR_DEVICE_TOKEN = ", token);

                // If application fails to get token it will write 'null' as a string
                if (token.length() > 5) {
                    Toast.makeText(MainActivity.this, "Token = "+token, Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(MainActivity.this, "Não foi possível gerar o token.", Toast.LENGTH_SHORT).show();
                }

            } break;

            case R.id.logLastMessageButton:
            {
                // Display Last Received Push Notification
                showLastReceivedMessage();

            } break;

        }

    }

    @Override
    public void encrypted_push(String response) {
        Log.i("Push Criptografado: ", response);
    }

    @Override
    public void decrypted_push(String response) {
        Log.i("Push Descriptografado: ", response);


        Toast.makeText(MainActivity.this,
                "Push Id: " + String.valueOf(restoredId)
                + "\n\nMensagem: " + String.valueOf(response),
                Toast.LENGTH_LONG).show();
    }



    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            String MessageId = arg1.getStringExtra("MessageId");
            String Body = arg1.getStringExtra("Body");

            Toast.makeText(MainActivity.this, "Notificação Recebida!\n"
            + "Push Id: " + String.valueOf(MessageId)
            + "\nMensagem: " + String.valueOf(Body),
            Toast.LENGTH_LONG).show();

        }

    }

    public void showLastReceivedMessage()
    {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        restoredId = prefs.getString("MessageId", null);
        String restoredText = prefs.getString("Body", null);

        if ((restoredText != null) && (restoredId != null)) {


            EncryptionOperation task = new EncryptionOperation();
            task.DeHashString(String.valueOf(restoredText), this);

        }
        else
        {
            Toast.makeText(MainActivity.this, "Nenhum Push Registrado!", Toast.LENGTH_LONG).show();
        }
    }
}
