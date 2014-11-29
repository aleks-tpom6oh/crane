package com.tpom6oh.crane;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.opengl.ETC1;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by alx on 12.10.14.
 */
public class SetPasswordActivity extends Activity {

    private EditText hostInput;
    private EditText passwordInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        final PreferencesDataSource preferencesDataSource = new PreferencesDataSource(this);

        hostInput = (EditText) findViewById(R.id.host_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        saveButton = (Button) findViewById(R.id.save_password);

        checkIntent();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordInput.getText().toString();
                String host = hostInput.getText().toString();
                preferencesDataSource.putPassword(host, password);
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
                } catch (URISyntaxException e) {
                    Toast.makeText(this, "Not a valid url", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (MalformedURLException e) {
                    Toast.makeText(this, "Not a valid url", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
