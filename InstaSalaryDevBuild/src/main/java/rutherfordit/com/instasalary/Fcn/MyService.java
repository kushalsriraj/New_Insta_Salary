package rutherfordit.com.instasalary.Fcn;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import rutherfordit.com.instasalary.R;
import rutherfordit.com.instasalary.activities.SplashScreenActivity;

public class MyService extends FirebaseMessagingService {
    public MyService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage.getNotification().getBody());
        Log.d("From", "From: " + remoteMessage.getFrom());
        Log.d("Body", "Notification Message Body: " + remoteMessage.getNotification().getBody());

    }

    private void sendNotification(String messageingBody){
        Intent intent=new Intent(this, SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaluturi= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder notificationBuilder=new Notification.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.instalogo);
        notificationBuilder.setContentTitle("GrantLending");
        notificationBuilder.setContentText(messageingBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaluturi);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }


}
