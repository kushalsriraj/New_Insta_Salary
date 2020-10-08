package rutherfordit.com.instasalary.extras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import rutherfordit.com.instasalary.Fcn.MyService;

public class OnBootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("com.demo.FirebaseMessagingReceiveService");
        i.setClass(context,  MyService.class);
        context.startService(i);
    }

}
