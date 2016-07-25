package androd.com.bluetoothtezt;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androd.com.bluetoothtezt.adapter.BleServiceListAdapter;
import androd.com.bluetoothtezt.service.BleService;
import androd.com.bluetoothtezt.utils.Utils;
//服务列表布局内容

/**
 * Created by admin on 2016/7/21.
 */
public class DeviceConnect extends Activity {
    public static final String EXTRAS_DEVICE_NAME = "android.com.bluetoothtezt.device_name";
    public static final String EXTRAS_DEVICE_ADDRESS = "android.com.bluetoothtezt.device_address";
    public static final String FIND_DEVICE_ALARM_ON = "android.com.bluetoothtezt.FIND_DEVICE_ALARM_ON";
    public static final String CANCEL_DEVICE_ALARM = "android.com.bluetoothtezt.CANCEL_DEVICE_ALARM";
    private ListView serviceListview;
    public BleServiceListAdapter mBleServiceListAdapter;
    private SwipeRefreshLayout swagLayout;
    public BleService bleService;
    public BluetoothGatt mBluetoothGatt;
    public static String bleAddress;
    public List<BluetoothGattService> gattService;

    public BroadcastReceiver blecastRecevier = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplication(), "设备连接成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                String uuid = null;
                gattService = bleService.mBluetoothGatt.getServices();
                final ArrayList<HashMap<String, String>> serviceName = new ArrayList<HashMap<String, String>>();
                for (BluetoothGattService sec : gattService) {
                    HashMap<String, String> currentServiceData = new HashMap<String, String>();
                    uuid = sec.getUuid().toString();
                    currentServiceData.put("Name", Utils.attributes.containsKey(uuid) ? Utils.attributes.get(uuid) : "UNKNOW SERVICE");
                    serviceName.add(currentServiceData);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBleServiceListAdapter.addServiceName(serviceName);
                        mBleServiceListAdapter.addService(gattService);
                        mBleServiceListAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    };

    private final ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bleService = ((BleService.LocalBinder) iBinder).getService();
            if (!bleService.init()) {
                finish();
            }
            bleService.connect(bleAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bleService = null;
        }
    };

    //创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用同样的布局，将服务显示出来
        setContentView(R.layout.activity_main);
        initView();
        bindBleService();//绑定服务
        //注册广播
        registerReceiver(blecastRecevier, makeIntentFilter());
    }

    private void bindBleService() {
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    //初始化布局
    private void initView() {
        serviceListview = (ListView) findViewById(R.id.lv_deviceList);//服务listview
        //服务适配器
        mBleServiceListAdapter = new BleServiceListAdapter(this);
        swagLayout = (SwipeRefreshLayout) findViewById(R.id.swagLayout);
        swagLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//刷新
            @Override
            public void onRefresh() {
                mBleServiceListAdapter.clear();
                bleService.mBluetoothGatt.discoverServices();
                mBleServiceListAdapter.notifyDataSetChanged();
                swagLayout.setRefreshing(false);
            }
        });
        serviceListview.setAdapter(mBleServiceListAdapter);
        bleAddress = getIntent().getExtras().getString(EXTRAS_DEVICE_ADDRESS);
        serviceListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long id) {
                Intent servicesIntent = new Intent(DeviceConnect.this,
                        CharacterisiticActivity.class);//跳转到特征界面
                servicesIntent.putExtra("serviceUUID",
                        bleService.mBluetoothGatt.getServices().get(position)
                                .getUuid());
                startActivity(servicesIntent);
            }
        });
    }

    /*
    *
    * */
    public static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);//解除绑定
        unregisterReceiver(blecastRecevier);
    }
}
