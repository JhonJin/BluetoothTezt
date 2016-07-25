package androd.com.bluetoothtezt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import androd.com.bluetoothtezt.service.BleService;

/**
 * Created by admin on 2016/7/22.
 */
public class ChangeCharDataActivity extends Activity {

    private Button mData;//数据转换成16进制
    private ScrollView mScrollview;
    private TextView notify_result;
    public String text_data = null;
    public String reslut = null;
    public String resul = null;
    public BleService bleService;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Button readButton;
    private Button notifyButton;
    private RadioGroup radioGroup;
    public UUID serUuid;
    public UUID charUuid;
    public BluetoothGattCharacteristic gattChar;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_char);
        //初始化布局
        initView();
        //绑定服务
        bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
        //注册广播
        registerReceiver(rec, makeIntentFilter());
    }

    //解除广播
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(rec);
        unbindService(conn);
    }

    //服务
    private ServiceConnection conn = new ServiceConnection() {
        @SuppressLint("NewApi")
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            // TODO Auto-generated method stub
            bleService = ((BleService.LocalBinder) service).getService();
            gattChar = bleService.mBluetoothGatt.getService(serUuid)
                    .getCharacteristic(charUuid);
            bleService.mBluetoothGatt.readCharacteristic(gattChar);
            if (gattChar.getDescriptors().size() != 0) {
                BluetoothGattDescriptor des = gattChar.getDescriptors().get(0);
                des.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                bleService.mBluetoothGatt.writeDescriptor(des);
            }
            int prop = gattChar.getProperties();
            if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                bleService.mBluetoothGatt.setCharacteristicNotification(
                        gattChar, false);
            }
            if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                bleService.mBluetoothGatt.setCharacteristicNotification(
                        gattChar, false);
            }
            if ((prop & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                bleService.mBluetoothGatt.setCharacteristicNotification(
                        gattChar, false);
            }
            if ((prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                bleService.mBluetoothGatt.setCharacteristicNotification(
                        gattChar, true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub
            bleService = null;
        }
    };


    //广播接收者
    BroadcastReceiver rec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("", "--------------rec被执行了---------------------");
            String action = intent.getAction();
            if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {
                reslut = intent.getExtras().getString(BleService.EXTRA_DATA);
                Log.e("", "result===" + reslut);
                if (text_data != null) {
                    text_data = text_data + reslut;
                } else {
                    text_data = reslut;
                }
                Log.e("", "text_data====" + text_data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notify_result.setText(text_data);
                    }
                });
            }
        }
    };

    private void initView() {
        mData = (Button) findViewById(R.id.rb_hex);//数据转换
        mScrollview = (ScrollView) findViewById(R.id.scrollview);
        notify_result = (TextView) findViewById(R.id.et_notify_resualt);//数据
        mData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (text_data != null) {
                    text_data = str2HexStr(text_data);//将数据转换成16进制数据
                }
                notify_result.setText(text_data);
                Log.e("", "notify_result=" + text_data);
            }
        });
        charUuid = UUID.fromString(getIntent().getExtras().get("charUUID")
                .toString());
        serUuid = UUID.fromString(getIntent().getExtras().get("serUUID")
                .toString());
    }

    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    public static byte[] str2Byte(String hexStr) {
        int b = hexStr.length() % 2;
        if (b != 0) {
            hexStr = "0" + hexStr;
        }
        String[] a = new String[hexStr.length() / 2];
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            a[i] = hexStr.substring(2 * i, 2 * i + 2);
        }
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(a[i], 16);
        }
        return bytes;
    }

    public static IntentFilter makeIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_CHAR_READED);
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.BATTERY_LEVEL_AVAILABLE);
        intentFilter.addAction(BleService.ACTION_GATT_RSSI);
        return intentFilter;
    }
}
