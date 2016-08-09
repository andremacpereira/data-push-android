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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    public static final String MY_PREFS_NAME = "PushData";

    MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

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

                // Log and toast
                String msg = getString(R.string.msg_token_fmt, token);
                Log.d(TAG, msg);

                if (msg.length() > 5) {
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            String MessageId = arg1.getStringExtra("MessageId");
            String Body = arg1.getStringExtra("Body");

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("MessageId", MessageId);
            editor.putString("Body", Body);
            editor.apply();

            Toast.makeText(MainActivity.this,
                    "Notificação Recebida!\n"
                            + "Id da Mensagem: " + String.valueOf(MessageId)
                            + "\nTexto: " + String.valueOf(Body),
                    Toast.LENGTH_LONG).show();

        }

    }

    public void showLastReceivedMessage()
    {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredId = prefs.getString("MessageId", null);
        String restoredText = prefs.getString("Body", null);

        if ((restoredText != null) && (restoredId != null)) {

            Toast.makeText(MainActivity.this,
                            "Push Id: " + String.valueOf(restoredId)
                            + "\n\nMensagem: " + String.valueOf(restoredText),
                    Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(MainActivity.this,
                "Nenhum Push Registrado!",
                Toast.LENGTH_LONG).show();
        }
    }
}
