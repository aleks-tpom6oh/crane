package com.tpom6oh.crane;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by alx on 12.10.14.
 */
public class SetPasswordActivity extends Activity {

    @Inject
    CryptUtils cryptUtils;

    private EditText hostInput;
    private EditText passwordInput;
    private Button saveButton;

    @Inject
    IDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set);

        ((Injector)getApplication()).inject(this);

        if (dataSource.getPersistentPasswordHash() != null)
        {
            String userPassword = ((CraneApplication) getApplication()).getUserPassword();
            if (userPassword == null)
            {
                askForPassword();
            }
            else
            {
                mainProcess();
            }
        }
        else
        {
            askToSetPassword();
        }

        mainProcess();
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

    private void askForPassword()
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
                            askForPassword();
                        }
                        else
                        {
                            ((CraneApplication)getApplication()).setUserPassword(passHash);
                            mainProcess();
                        }
                    }
                })
                .cancelable(false)
                .show();
    }


    private void mainProcess()
    {
        hostInput = (EditText) findViewById(R.id.host_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        saveButton = (Button) findViewById(R.id.save_password);

        checkIntent();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordInput.getText().toString();
                String masterPass = ((CraneApplication)getApplication()).getUserPassword();
                String encryptedPassword = cryptUtils.encrypt(masterPass,
                                                              password);

                String host = hostInput.getText().toString();
                dataSource.putPassword(host, encryptedPassword);
                ClipboardManager clipboard =
                        (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", password);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SetPasswordActivity.this, "Password for " + host +
                        " saved and copied to clipboard!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void checkIntent() {
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
                        final String host = uri.getHost();
                        hostInput.setText(host);
                    } else {
                        finish();
                    }
                } catch (URISyntaxException | MalformedURLException e) {
                    Toast.makeText(this, "Not a valid url", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
