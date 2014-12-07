package com.tpom6oh.crane;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class HolderActivity extends Activity {

    @Inject
    CryptUtils cryptUtils;
    @Inject
    IDataSource dataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((Injector)getApplication()).inject(this);

        if (dataSource.getPersistentPasswordHash() != null)
        {
            String userPassword = ((CraneApplication) getApplication()).getUserPassword();
            if (userPassword == null)
            {
                askForPassword(savedInstanceState);
            }
            else
            {
                mainProcess(savedInstanceState);
            }
        }
        else
        {
            askToSetPassword();
        }
    }

    private void mainProcess(Bundle savedInstanceState)
    {
        if (checkIntent()) {
            return;
        }

        setContentView(R.layout.activity_holder);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void askToSetPassword()
    {
        final EditText passwordEditText = new EditText(this);
        new MaterialDialog.Builder(this)
                .title("Set password to protect your data")
                .customView(passwordEditText)
                .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                .positiveText("Go")
                .callback(new MaterialDialog.SimpleCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        String passHash = cryptUtils.hash(passwordEditText.getText().toString());
                        ((CraneApplication)getApplication()).setUserPassword(passHash);
                        dataSource.setPersistentPasswordHash(passHash);
                    }
                })
                .cancelable(false)
                .show();
    }

    private void askForPassword(final Bundle savedInstanceState)
    {
        final EditText passwordEditText = new EditText(this);
        new MaterialDialog.Builder(this)
                .title("Type your password to access the application")
                .customView(passwordEditText)
                .theme(Theme.LIGHT)  // the default is light, so you don't need this line
                .positiveText("Go")
                .callback(new MaterialDialog.SimpleCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        String hash = dataSource.getPersistentPasswordHash();
                        String passHash = cryptUtils.hash(passwordEditText.getText().toString());
                        if (!passHash.equals(hash))
                        {
                            askForPassword(savedInstanceState);
                        }
                        else
                        {
                            ((CraneApplication)getApplication()).setUserPassword(passHash);
                            mainProcess(savedInstanceState);
                        }
                    }
                })
                .cancelable(false)
                .show();
    }

    private boolean checkIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                try {
                    URL u = new URL(sharedText); // this would check for the protocol
                    URI uri = u.toURI();
                    if (sharedText != null) {
                        String host = uri.getHost();
                        String encryptedPassword = dataSource.getPassword(host);
                        if (encryptedPassword != null) {
                            String password = cryptUtils.decrypt(((CraneApplication)getApplication()).getUserPassword(),
                                                                 encryptedPassword);
                            ClipboardManager clipboard =
                                    (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", password);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(this, "Password for " + host +
                                    " copied to clipboard!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No password stored for " + host,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    finish();
                } catch (URISyntaxException | MalformedURLException e) {
                    Toast.makeText(this, "Not a valid url", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_holder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, SetPasswordActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
