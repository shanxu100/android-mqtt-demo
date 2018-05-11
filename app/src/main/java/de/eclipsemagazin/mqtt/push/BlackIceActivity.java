package de.eclipsemagazin.mqtt.push;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.eclipsemagazin.mqtt.constant.WebConstant;

public class BlackIceActivity extends Activity {

    public static final String SERVICE_CLASSNAME = "de.eclipsemagazin.mqtt.push.MQTTService";
    private Button btn_start;
    private EditText et_url, et_topics;
    private CheckBox cb_HEX;
    private ListView lv_output;

    private List<String> outputList = new ArrayList<>();
    private ArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        et_url = (EditText) findViewById(R.id.et_url);
        et_topics = (EditText) findViewById(R.id.et_topics);
        btn_start = (Button) findViewById(R.id.btn_start);
        cb_HEX = (CheckBox) findViewById(R.id.cb_HEX);
//        tv_output = (TextView) findViewById(R.id.tv_output);

        et_url.setText(WebConstant.MQTT_URL);
        et_topics.setText(WebConstant.TOPICS);

        lv_output = (ListView) findViewById(R.id.lv_output);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, outputList);
        lv_output.setAdapter(adapter);

        updateButton();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateButton();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void updateButton() {
        if (serviceIsRunning()) {
            btn_start.setText("正在运行，点击断开");
            btn_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_start.setText("Start Service");
                    stopBlackIceService();
                    updateButton();
                }
            });

        } else {
            btn_start.setText("点击连接");
            btn_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btn_start.setText("正在运行，点击断开");
                    startBlackIceService();
                    updateButton();
                }
            });
        }
    }

    private void startBlackIceService() {
        et_url.setEnabled(false);
        et_topics.setEnabled(false);
        outputList.clear();
        adapter.notifyDataSetChanged();
        WebConstant.MQTT_URL = et_url.getText().toString().trim();
        WebConstant.TOPICS = et_topics.getText().toString().trim();
        final Intent intent = new Intent(this, MQTTService.class);
        startService(intent);
    }

    private void stopBlackIceService() {
        et_url.setEnabled(true);
        et_topics.setEnabled(true);
        final Intent intent = new Intent(this, MQTTService.class);
        stopService(intent);
    }

    private boolean serviceIsRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SERVICE_CLASSNAME.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void onEventBusMsg(MqttMessage message) {
        if (outputList.size() > 10) {
            outputList.clear();
        }
        if (cb_HEX.isChecked()) {
            outputList.add(ByteUtil.byte2hex(message.getPayload()));
        } else {
            outputList.add(ByteUtil.byte2string(message.getPayload()));
        }
        adapter.notifyDataSetChanged();

    }


}
