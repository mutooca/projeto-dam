package ao.uan.fc.dam.mobile;

import android.app.Application;

import ao.uan.fc.dam.mobile.api.RetrofitClient;

public class MobileApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
    }
}
