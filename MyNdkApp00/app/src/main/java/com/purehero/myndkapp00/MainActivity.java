package com.purehero.myndkapp00;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.purehero.myndkapp00.databinding.ActivityMainBinding;
import com.purehero.myndkapp00.dialog.MessageDialog;
import com.purehero.myndkapp00.dialog.MessageService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyApp00";

    private NativeLibrary nativeLibrary = new NativeLibrary();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d( TAG, "MainActivity::onCreate()");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText( nativeLibrary.stringFromJNI());

        int btnIDs[] = { R.id.MyButton00, R.id.MyButton01, R.id.MyButton02 };
        for( int btnID : btnIDs ) {
            Button btn = findViewById( btnID );
            if( btn != null ) {
                btn.setOnClickListener( this );
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch( view.getId()) {
            case R.id.MyButton00:
                MessageDialog.showAlertDialog(
                        this,
                        1,
                        "[70007] [Auto Clicker for IDLE Game] 툴이 감지되었습니다. 해당 툴 삭제 후 앱을 다시 실행해 주시기 바랍니다. (설정의 앱관리에서 삭제할 수 있습니다)");
                break;
            case R.id.MyButton01:
                MessageDialog.showAlertDialog(
                        this,
                        0,
                        "[70007] [Auto Clicker for IDLE Game] 툴이 감지되었습니다. 해당 툴 삭제 후 앱을 다시 실행해 주시기 바랍니다. (설정의 앱관리에서 삭제할 수 있습니다)");
                break;
            case R.id.MyButton02:
                Log.d( TAG, "Button clicked : " + ((Button) view ).getText());
                break;
        }
    }
}