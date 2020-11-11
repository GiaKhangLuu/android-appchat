package com.sinhvien.appchatsocketio.helper;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton vsInstance;
    private RequestQueue requestQueue;

    private VolleySingleton(Context context) {
        if(requestQueue == null ) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if(vsInstance == null) {
            vsInstance = new VolleySingleton(context);
        }
        return vsInstance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
