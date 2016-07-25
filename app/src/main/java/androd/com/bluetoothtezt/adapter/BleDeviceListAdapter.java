package androd.com.bluetoothtezt.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androd.com.bluetoothtezt.R;
//设备列表适配器

/**
 * Created by admin on 2016/7/21.
 */
public class BleDeviceListAdapter extends BaseAdapter {
    /**
     * private LayoutInflater mInflater;
     * private ArrayList<BluetoothDevice> mLeDevices;
     * private ArrayList<Integer> RSSIs;
     * private ArrayList<String> scanRecords;
     */
    private LayoutInflater mInflater;
    private ArrayList<BluetoothDevice> mLeDevices;
    private ArrayList<String> scanRecords;

    public BleDeviceListAdapter(Context context) {
        mLeDevices = new ArrayList<BluetoothDevice>();
        scanRecords = new ArrayList<String>();
        this.mInflater = LayoutInflater.from(context);
    }

    //添加设备
    public void addDevice(BluetoothDevice device, String scanRecord) {
        if (!mLeDevices.contains(device)) {
            this.mLeDevices.add(device);
            this.scanRecords.add(scanRecord);
        } else {
            for (int i = 0; i < mLeDevices.size(); i++) {
                BluetoothDevice deviceBle = mLeDevices.get(i);
                if (device.getAddress().equals(deviceBle.getAddress())) {
                    scanRecords.set(i, scanRecord);
                }
            }
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewholder;//复用
        if (view == null) {
            view = mInflater.inflate(R.layout.device_list, null);
            viewholder = new ViewHolder();
            viewholder.devicelist_name = (TextView) view.findViewById(R.id.devicelist_name);
            viewholder.devicelist_address = (TextView) view.findViewById(R.id.devicelist_address);
            viewholder.devicelist_scanRecord = (TextView) view.findViewById(R.id.devicelist_scanRecord);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }
        String name = mLeDevices.get(i).getName();//得到设备的名称
        if (name != null) {
            viewholder.devicelist_name.setText(name);
        } else {
            viewholder.devicelist_name.setText("UNKNOW SERVICE");
        }
        String address = mLeDevices.get(i).getAddress();//得到设备的地址
        viewholder.devicelist_address.setText("equipment ADDRESS" + address);
        String broadcast = scanRecords.get(i);//广播包
        viewholder.devicelist_scanRecord.setText("BROADCAST PACKAGER" + broadcast);
        return view;
    }

    public class ViewHolder {
        TextView devicelist_name;//设备名称
        TextView devicelist_address;//设备MAC地址
        TextView devicelist_scanRecord;//设备广播
    }

    //清空设备
    public void clear() {
        mLeDevices.clear();
        scanRecords.clear();
        this.notifyDataSetChanged();
    }
}
