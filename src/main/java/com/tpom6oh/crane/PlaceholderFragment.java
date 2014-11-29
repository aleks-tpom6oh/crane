package com.tpom6oh.crane;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private ListView hostsList;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hosts, container, false);
        hostsList = (ListView) rootView.findViewById(R.id.hosts_list);
        update();
        return rootView;
    }

    private void update() {
        final PreferencesDataSource preferencesDataSource = new PreferencesDataSource(getActivity());
        Set<String> hosts = preferencesDataSource.getHostsData();
        final String[] hostsArray = hosts.toArray(new String[hosts.size()]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, hostsArray);
        hostsList.setAdapter(arrayAdapter);
        hostsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String host = hostsArray[i];
                String password = preferencesDataSource.getPassword(host);
                if (password != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + host));
                    startActivity(browserIntent);
                    ClipboardManager clipboard =
                            (ClipboardManager) getActivity().getSystemService(Activity.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", password);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getActivity(), "Password for " + host +
                            " copied to clipboard!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "No password stored for " + host,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        hostsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SetPasswordActivity.class);
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "http://" + hostsArray[i]);
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        update();
    }
}
