package de.eclipsemagazin.mqtt.push;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import de.eclipsemagazin.mqtt.constant.WebConstant;


/**
 * @author Dominik Obermaier
 */
public class MQTTService extends Service {

    public static final String clientId = "android-client";
    private MqttClient mqttClient;
    private MqttConnectOptions options;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mqttClient = new MqttClient(WebConstant.MQTT_URL, clientId, new MemoryPersistence());

            mqttClient.setCallback(new PushCallback(this));

            options = new MqttConnectOptions();
            options.setUserName("admin");
            options.setPassword("password".toCharArray());
            mqttClient.connect(options);

            //Subscribe to all subtopics of homeautomation
            String[] toipcs = WebConstant.TOPICS.trim().split(",");
            for (String s : toipcs) {
                mqttClient.subscribe(s);
            }

        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        try {
            mqttClient.disconnect(0);
        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
