package com.example.mqttestlast;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button btndishere, btnpubhere, btnsubhere, btnhishere;
    private TextView txtSubshere, txtTutorialhere;
    private static final String TAG= "MainActivity";
    private String clientID, amount;
    private MqttAndroidClient client;
    private ProgressBar mProgress;
    private int percentage;
    RequestQueue queue;
    final String URL = "http://192.168.0.244/comments/api.php"; //http://192.168.43.6/comments/api.php database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(getApplicationContext());

        init();
    }

    private void init(){
        btnhishere = findViewById(R.id.btnHistory);
        btndishere = findViewById(R.id.disbtn);
        btnpubhere = findViewById(R.id.btnpub);
        btnsubhere = findViewById(R.id.btnsub);
        txtSubshere = findViewById(R.id.txtSubs);
        mProgress = (ProgressBar) findViewById(R.id.progressBar3);

        btndishere.setVisibility(View.INVISIBLE);

        clientID = "xxx";
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.mobilepit.com:1883",
                        clientID); // tcp://broker.mobilepit.com:1883 , tcp://192.168.43.12:1883

        btnpubhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "/feeds/motor";
                String message = "1";
                try {
                    client.publish(topic, message.getBytes(),0,false);
                    Toast.makeText(MainActivity.this, "Published Message", Toast.LENGTH_SHORT).show();
                    makeRequest();

                } catch ( MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        btnsubhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "/feeds/monitor";
                try {
                    client.subscribe(topic,0);
                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            Log.d(TAG, "topic: " + topic);
                            Log.d(TAG, "message: " + new String(message.getPayload()));

                                amount =  new String(message.getPayload());
                                percentage = Integer.parseInt(new String(message.getPayload()));
                                mProgress.setProgress(percentage);

                            txtSubshere.setText("" + percentage + "%");
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });

                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        btndishere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
        btnhishere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, petfeedhistory.class);
                startActivity(intent);
            }
        });
    }

    public void makeRequest(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        }, errorListener) {
            @Override
            protected Map<String,String> getParams () {
                Map  <String, String> params = new HashMap<>();

                params.put("name", "Pet Feed!");
                params.put("amount",amount);

                return params;

            }
        };
        queue.add(stringRequest);
    }

    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.connect:
                btndishere.setVisibility(View.VISIBLE);
                IMqttToken token = null;
                try {
                    token = client.connect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        // We are connected
                        Log.d(TAG, "onSuccess");
                    }
                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        // Something went wrong e.g. connection timeout or firewall problems
                        Log.d(TAG, "onFailure");
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
    public void disconnect(){
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this,"Disconnected!!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, home.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"Could not diconnect!!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

