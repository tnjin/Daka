package tq.afor.tjz.daka;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    ServiceConnection conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindSerivce();

    }
    private void bindSerivce(){
        Intent intent  = new Intent(ACCESSIBILITY_SERVICE);
        intent.setPackage(this.getPackageName());
        this.bindService(intent, conn=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("SSSS","成功启动");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("SSSS","成功结束");
            }
        }, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(conn);
        conn = null;
    }
}
