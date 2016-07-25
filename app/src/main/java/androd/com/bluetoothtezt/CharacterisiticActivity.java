package androd.com.bluetoothtezt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androd.com.bluetoothtezt.adapter.BleCharacteristicAdapter;
import androd.com.bluetoothtezt.service.BleService;
import androd.com.bluetoothtezt.utils.Utils;

/**
 * Created by admin on 2016/7/21.
 */
public class CharacterisiticActivity extends Activity {

    UUID uuid;
    private BleCharacteristicAdapter mBleCharacterAdapter;
    private ListView characteristic;
    private BleService bleService;
    public BluetoothGattService gattService;

    //服务
    public final ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bleService = ((BleService.LocalBinder) iBinder).getService();
            gattService = bleService.mBluetoothGatt.getService(uuid);
            final ArrayList<HashMap<String, String>> charNames = new ArrayList<HashMap<String, String>>();
            final List<BluetoothGattCharacteristic> gattChars = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic cha : gattChars) {
                HashMap<String, String> currentcharData = new HashMap<String, String>();
                String uuidstr = cha.getUuid().toString();
                currentcharData.put("Name", Utils.attributes.containsKey(uuidstr) ? Utils.attributes.get(uuidstr) : "UNKNOWN CHARACTERISTIC");
                charNames.add(currentcharData);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBleCharacterAdapter.addCharNames(charNames);
                    mBleCharacterAdapter.addChars(gattChars);
                    mBleCharacterAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bleService = null;
        }
    };

    public BroadcastReceiver recei = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                bleService.connect(DeviceConnect.bleAddress);
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.characteristic_list);
        //得到UUID数值
        uuid = (UUID) getIntent().getExtras().get("serviceUUID");
        initView();
        //绑定服务
        bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
        //注册广播
        registerReceiver(recei, makeIntentFilter());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定
        unbindService(conn);
        unregisterReceiver(recei);
    }

    //初始化布局
    private void initView() {
        //characteristic_list = (List) findViewById(R.id.characteristic_list);//特征列表
        characteristic = (ListView) findViewById(R.id.characteristic_list);
        //适配器
        mBleCharacterAdapter = new BleCharacteristicAdapter(this);
        characteristic.setAdapter(mBleCharacterAdapter);
        characteristic.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent mIntent = new Intent(CharacterisiticActivity.this, ChangeCharDataActivity.class);//跳转到数据显示界面
                UUID charUuid = bleService.mBluetoothGatt.getService(uuid).getCharacteristics().get(i).getUuid();
                mIntent.putExtra("charUUID", charUuid);
                mIntent.putExtra("serUUID", uuid);
                startActivity(mIntent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Characterisitic Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://androd.com.bluetoothtezt/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Characterisitic Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://androd.com.bluetoothtezt/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public static IntentFilter makeIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.BATTERY_LEVEL_AVAILABLE);
        return intentFilter;
    }
}
