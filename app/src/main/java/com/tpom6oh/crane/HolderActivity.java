package com.tpom6oh.crane;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class HolderActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    private boolean checkIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                PreferencesDataSource preferencesDataSource = new PreferencesDataSource(this);
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                try {
                    URL u = new URL(sharedText); // this would check for the protocol
                    URI uri = u.toURI();
                    if (sharedText != null) {
                        String host = uri.getHost();
                        String password = preferencesDataSource.getPassword(host);
                        if (password != null) {
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
                } catch (URISyntaxException e) {
                    Toast.makeText(this, "Not a valid url", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (MalformedURLException e) {
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
