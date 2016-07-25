package androd.com.bluetoothtezt.adapter;

import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androd.com.bluetoothtezt.R;

/**
 * Created by admin on 2016/7/21.
 */
public class BleServiceListAdapter extends BaseAdapter {
    /*
    * private LayoutInflater mInflater;
	private ArrayList<BluetoothGattService> services;
	private ArrayList<HashMap<String, String>> serviceName;
    * */
    private LayoutInflater mInflater;
    private ArrayList<BluetoothGattService> services;//服务
    private ArrayList<HashMap<String, String>> serviceName;//服务名称

    public BleServiceListAdapter(Context context) {
        services = new ArrayList<BluetoothGattService>();
        this.mInflater = LayoutInflater.from(context);
    }

    //添加服务
    public void addService(List<BluetoothGattService> services) {
        this.services = (ArrayList<BluetoothGattService>) services;
    }

    //添加服务的名称
    public void addServiceName(List<HashMap<String, String>> servicesName) {
        this.serviceName = (ArrayList<HashMap<String, String>>) servicesName;
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int i) {
        return services.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewholder;
        if (view == null) {
            view = mInflater.inflate(R.layout.device_list, null);
            viewholder = new ViewHolder();
            viewholder.serviceName = (TextView) view.findViewById(R.id.devicelist_name);//服务名称
            viewholder.serviceUUID = (TextView) view.findViewById(R.id.devicelist_address);//uuid
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }
        viewholder.serviceName.setText(serviceName.get(i).get("name"));
        SpannableString span = new SpannableString(services.get(i).getUuid().toString());
        viewholder.serviceUUID.setText(span);
        return view;
    }

    public class ViewHolder {
        TextView serviceName;//服务名称
        TextView serviceUUID;//服务UUID
    }

    //清空
    public void clear() {
        services.clear();
    }
}
