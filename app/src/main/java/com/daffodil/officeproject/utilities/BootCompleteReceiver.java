package com.daffodil.officeproject.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Daffodil on 12/19/2019.
 */

public class BootCompleteReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        Intent service = new Intent(context, MsgPushService.class);
        context.startService(service);

    }


}
