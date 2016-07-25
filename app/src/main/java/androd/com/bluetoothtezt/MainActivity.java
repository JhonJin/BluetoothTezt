package androd.com.bluetoothtezt;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androd.com.bluetoothtezt.adapter.BleDeviceListAdapter;
import androd.com.bluetoothtezt.utils.Utils;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    ListView listview;
    SwipeRefreshLayout swagLayout;
    BleDeviceListAdapter mBleDeviceListAdapter;//设备适配器
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getActionBar().setTitle("设备列表");
        initView();
        getBleAdapter();
        getScanResult();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.startLeScan(mLeScanCallback);//开始扫描设备
            }
        }).start();
    }

    public void initView() {
        mBleDeviceListAdapter = new BleDeviceListAdapter(this);
        listview = (ListView) findViewById(R.id.lv_deviceList);//设备列表
        swagLayout = (SwipeRefreshLayout) findViewById(R.id.swagLayout);//下拉刷新组件
        swagLayout.setVisibility(View.VISIBLE);
        swagLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBleDeviceListAdapter.clear();
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                swagLayout.setRefreshing(false);
            }
        });
        listview.setAdapter(mBleDeviceListAdapter);
        setListItemListener();//点击跳转到DeviceConnect
    }

    private void setListItemListener() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {//直接跳转到数据显示界面......
                // TODO Auto-generated method stub
                BluetoothDevice device = mBleDeviceListAdapter
                        .getDevice(position);
                final Intent intent = new Intent(MainActivity.this,
                        DeviceConnect.class);
                intent.putExtra(DeviceConnect.EXTRAS_DEVICE_NAME,
                        device.getName());
                intent.putExtra(DeviceConnect.EXTRAS_DEVICE_ADDRESS,
                        device.getAddress());
                startActivity(intent);//跳转页面DeviceConnect
            }
        });
    }

    public void getBleAdapter() {
        final BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public void getScanResult() {
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice bluetoothDevice, int i, final byte[] scanRecord) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBleDeviceListAdapter.addDevice(bluetoothDevice, Utils.bytesToHex(scanRecord));
                        mBleDeviceListAdapter.notifyDataSetChanged();
                        invalidateOptionsMenu();
                    }
                });
            }
        };
    }

    //销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleDeviceListAdapter.clear();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mBluetoothAdapter.cancelDiscovery();
//        this.notifyAll();
    }
}
