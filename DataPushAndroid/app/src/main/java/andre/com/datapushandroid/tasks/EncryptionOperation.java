package andre.com.datapushandroid.tasks;

import android.os.AsyncTask;

import andre.com.datapushandroid.interfaces.EncryptionResponseInterface;
import se.simbio.encryption.Encryption;

public class EncryptionOperation {

    private EncryptionResponseInterface mEncripterResponseInterface;

    public void HashString(String push_str, EncryptionResponseInterface toResponse) {

        mEncripterResponseInterface = toResponse;

        class Hash extends AsyncTask<String, String, String> {
            @Override
            protected String doInBackground(String... params) {

                String crypt_push = params[0];
                Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
                return encryption.encryptOrNull(crypt_push);

            }

            @Override
            protected void onPostExecute(String result) {

                mEncripterResponseInterface.encrypted_push(result);

                return;
            }
        }

        new Hash().execute(push_str);

    }

    public void DeHashString(String push_str, EncryptionResponseInterface toResponse) {

        mEncripterResponseInterface = toResponse;

        class Hash extends AsyncTask<String, String, String> {
            @Override
            protected String doInBackground(String... params) {

                String crypt_push = params[0];
                Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
                return encryption.decryptOrNull(crypt_push);

            }

            @Override
            protected void onPostExecute(String result) {

                mEncripterResponseInterface.decrypted_push(result);

                return;
            }
        }

        new Hash().execute(push_str);

    }

}
