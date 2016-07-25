package androd.com.bluetoothtezt.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androd.com.bluetoothtezt.R;

/**
 * Created by admin on 2016/7/22.
 */
public class BleCharacteristicAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;
    private ArrayList<BluetoothGattCharacteristic> mBluetoothGattCharacteristic;//特征值
    private ArrayList<HashMap<String, String>> mBluetoothGattCharacteristicName;//特征名

    public BleCharacteristicAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mBluetoothGattCharacteristic = new ArrayList<BluetoothGattCharacteristic>();
        mBluetoothGattCharacteristicName = new ArrayList<HashMap<String, String>>();
    }

    @Override
    public int getCount() {
        return mBluetoothGattCharacteristic.size();
    }

    @Override
    public Object getItem(int i) {
        return mBluetoothGattCharacteristic.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewholder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.device_list, null);
            viewholder = new ViewHolder();
            viewholder.characteristicUUID = (TextView) view.findViewById(R.id.devicelist_address);//值
            viewholder.characteristicName = (TextView) view.findViewById(R.id.devicelist_name);//特征名称
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }

        viewholder.characteristicName.setText(mBluetoothGattCharacteristicName.get(i).get("name"));//特征名称
        SpannableString span = new SpannableString(mBluetoothGattCharacteristic.get(i).getUuid().toString());
        viewholder.characteristicUUID.setText(span);
        return view;
    }

    public class ViewHolder {
        TextView characteristicUUID;
        TextView characteristicName;
    }

    public void addChars(List<BluetoothGattCharacteristic> characteristics) {
        this.mBluetoothGattCharacteristic = (ArrayList<BluetoothGattCharacteristic>) characteristics;
    }

    public void addCharNames(List<HashMap<String, String>> charNames) {
        this.mBluetoothGattCharacteristicName = (ArrayList<HashMap<String, String>>) charNames;
    }
}
