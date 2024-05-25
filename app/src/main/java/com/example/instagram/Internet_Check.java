package com.example.instagram;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

public class Internet_Check extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //neu intnet.getAction == ConnectivityManager.CONNECTIVITY_ACTION: tuc la da lang nghe dc su thay doi trong network
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {

            if (internet_check(context)) {
                Toast.makeText(context, "Đã kết nối mạng", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Đã ngắt kết nối mạng", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Boolean internet_check(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//M:23 -> nhung android 6 tro len ta se check 1 kieu, version thap hon 1 kieu khac
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) {
                return false;
            }
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
        } else {//check cua android < 6
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }
}
