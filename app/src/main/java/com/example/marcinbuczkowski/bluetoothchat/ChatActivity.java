package com.example.marcinbuczkowski.bluetoothchat;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import java.util.Set;
import java.util.UUID;

import android.content.Intent;
import android.content.IntentFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

public class ChatActivity extends ActionBarActivity {

    private Spinner receivers;
    private BluetoothAdapter bt;
    private ArrayAdapter<String> btArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.bt = BluetoothAdapter.getDefaultAdapter();
        this.bt.enable();
        this.bt.startDiscovery();

        this.receivers = (Spinner)findViewById(R.id.receiversSpinner);
        this.populateReceivers();

        Button sendButton = (Button)findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText messageText = (EditText)findViewById(R.id.messageText);
                String message = messageText.getText().toString();
                if (message.length() <= 0) {
                    return;
                }

                Spinner people = (Spinner)findViewById(R.id.receiversSpinner);
                String receiver = people.getSelectedItem().toString();

                if (receiver.length() <= 0) {
                    return;
                }

                BluetoothClientThread btc = new BluetoothClientThread();
                BluetoothDevice bd = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(receiver);
                //todo replace UUID as described in MainActivity
                btc.connect(bd, UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
                btc.sendMessage(message, receiver);
            }
        });
    }

    private void populateReceivers() {
        this.btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        Set<BluetoothDevice> pairedDevices = this.bt.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                //todo add address as ID and name as item content
                this.btArrayAdapter.add(device.getAddress());
            }
        }

        this.btArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.receivers.setAdapter(this.btArrayAdapter);
    }

    //todo add to view only when appropriate device is chosen from selectbox
    public boolean addMessage(String sender, String receiver, String message) {
        ScrollView sv = (ScrollView)findViewById(R.id.messageView);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(this);
        tv.setText(message);
        ll.addView(tv);

        sv.addView(ll);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
