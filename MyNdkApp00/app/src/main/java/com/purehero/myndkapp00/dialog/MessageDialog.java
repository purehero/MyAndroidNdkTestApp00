package com.purehero.myndkapp00.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.purehero.myndkapp00.NativeLibrary;

public class MessageDialog extends Activity implements View.OnClickListener {

    private String dialogMessage = "";
    private int errorCode = -1;

    private final int BTN_ID_CONFIRM    = 0x1111;
    private final int LAYOUT_ID_LEFT    = 0x2222;
    private final int LAYOUT_ID_RIGHT   = 0x3333;
    private final int LAYOUT_ID_CENTER  = 0x4444;

    NativeLibrary nativeLibrary = new NativeLibrary();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        dialogMessage = intent.getExtras().getString("msg");
        errorCode = parserErrorCode();
        
        setContentView( makeContentView() );

        final int killTimeSec = 10;
        nativeLibrary.killMyProcess( killTimeSec );
        new Thread( new Runnable(){
            @Override
            public void run() {
                if( tvConfirmButton != null ) {
                    for (int i = 0; i < killTimeSec; i++) {
                        final String exitTimerMsg = String.format("확인(%d)", killTimeSec-i);
                        MessageDialog.this.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                tvConfirmButton.setText( exitTimerMsg );
                            }
                        });
                        try {
                            Thread.sleep( 1000 );
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }).start();
    }

    private int parserErrorCode() {
        int ret = -1;

        try {
            int s_idx = dialogMessage.indexOf("[");
            if (s_idx < 0) return -1;

            int e_idx = dialogMessage.indexOf("]", s_idx + 1);
            if (e_idx < 0) return -1;

            String error_code = dialogMessage.substring(s_idx + 1, e_idx );
            ret = Integer.valueOf(error_code);

            dialogMessage = dialogMessage.substring( e_idx + 1 );
        } catch( Exception e ) {
        }

        return ret;
    }

    private View makeContentView() {
        RelativeLayout layout = new RelativeLayout( this );

        RelativeLayout leftLayout = new RelativeLayout( this );
        RelativeLayout rightLayout = new RelativeLayout( this );
        RelativeLayout centerLayout = new RelativeLayout( this );

        RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT );
        RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT );
        RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT );

        makeLeftLayout( leftLayout, leftParams );
        makeRightLayout( rightLayout, rightParams );
        makeCenterLayout( centerLayout, centerParams );

        layout.addView( leftLayout, leftParams );
        layout.addView( rightLayout, rightParams );
        layout.addView( centerLayout, centerParams );

        return layout;
    }

    private void makeRightLayout(RelativeLayout layout, RelativeLayout.LayoutParams params) {
        layout.setId( LAYOUT_ID_RIGHT );
        params.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
        params.width = 50;
    }

    private void makeLeftLayout(RelativeLayout layout, RelativeLayout.LayoutParams params) {
        layout.setId( LAYOUT_ID_LEFT );
        params.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
        params.width = 50;
    }

    private void makeCenterLayout(RelativeLayout layout, RelativeLayout.LayoutParams params) {
        layout.setId( LAYOUT_ID_CENTER );

        // center layout 배치
        params.addRule( RelativeLayout.CENTER_IN_PARENT );
        params.addRule( RelativeLayout.LEFT_OF, LAYOUT_ID_RIGHT );
        params.addRule( RelativeLayout.RIGHT_OF, LAYOUT_ID_LEFT );

        // dialog layout 추가
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT );
        layoutParams.addRule( RelativeLayout.CENTER_IN_PARENT );
        layout.addView( makeDialogLayout(), layoutParams );

        // 테두리 그리기
        GradientDrawable border = new GradientDrawable();
        border.setColor( 0xFFFFFFFF );
        border.setStroke(2, 0xFF000000 );
        border.setCornerRadius( 30 );
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            layout.setBackgroundDrawable(border);
        } else {
            layout.setBackground(border);
        }
    }

    private View makeDialogLayout() {
        LinearLayout layout = new LinearLayout( this );
        layout.setOrientation( LinearLayout.VERTICAL );
        layout.setPadding( 50, 35, 50, 35 );

        makeDialogTitleMessageLayout( layout );
        makeErrorCodeMessageLayout( layout );
        makeDialogAppIconLayout( layout );
        makeDialogMessageLayout( layout );
        makeDialogConfirmLayout( layout );

        return layout;
    }

    TextView tvConfirmButton = null;
    private void makeDialogConfirmLayout(LinearLayout layout) {
        tvConfirmButton = new TextView( this );
        tvConfirmButton.setText("확인");
        tvConfirmButton.setTextColor( Color.BLUE );
        tvConfirmButton.setId( BTN_ID_CONFIRM );
        tvConfirmButton.setOnClickListener( this );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
        params.rightMargin      = 50;
        params.topMargin        = 100;
        params.bottomMargin     = 50;
        params.gravity          = Gravity.RIGHT | Gravity.BOTTOM;
        layout.addView( tvConfirmButton, params );
    }

    private void makeDialogMessageLayout(LinearLayout layout) {
        TextView tvAppName = new TextView( this );
        //tvAppName.setText( String.format( "\n\n\"%s\"\n\n잠시 후 종료됩니다.\n\n", getApplicationName( this )));
        tvAppName.setText( String.format( "%s", dialogMessage.replace(". ", ".\n") ));
        tvAppName.setGravity( Gravity.CENTER );
        tvAppName.setTextColor( Color.BLACK );
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
        params.topMargin        = 100;
        params.bottomMargin     = 50;
        layout.addView( tvAppName, params );

        TextView tvExitTimerMessage = new TextView( this );
        tvExitTimerMessage.setText( "잠시 후 종료됩니다." );
        tvExitTimerMessage.setGravity( Gravity.CENTER );
        tvExitTimerMessage.setTextColor( Color.BLACK );
        layout.addView( tvExitTimerMessage );
    }

    private void makeDialogTitleMessageLayout(LinearLayout layout) {
        TextView tvDialogTitle = new TextView( this );
        tvDialogTitle.setText( "위협이 감지되었습니다." );
        tvDialogTitle.setGravity( Gravity.CENTER );
        tvDialogTitle.setTextColor( Color.RED );
        tvDialogTitle.setTextSize( 2, 20 );
        tvDialogTitle.setTypeface( tvDialogTitle.getTypeface(), Typeface.BOLD );
        tvDialogTitle.setPadding( 0, 25, 0, 0 );
        layout.addView( tvDialogTitle );
    }

    private void makeErrorCodeMessageLayout(LinearLayout layout) {
        if( errorCode == -1 ) return;

        TextView tvErrorCode = new TextView( this );
        tvErrorCode.setText( String.format( "[%d]", errorCode ));
        //tvErrorCode.setTextColor( Color.BLUE );
        tvErrorCode.setGravity( Gravity.CENTER );
        tvErrorCode.setTextSize( 2, 17 );
        tvErrorCode.setTypeface( tvErrorCode.getTypeface(), Typeface.BOLD );
        tvErrorCode.setPadding( 0, 10, 0, 75 );

        layout.addView( tvErrorCode );
    }

    private void makeDialogAppIconLayout(LinearLayout layout) {
        RelativeLayout relativeLayout = new RelativeLayout( this );
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT );
        params.addRule( RelativeLayout.CENTER_IN_PARENT );

        ImageView imageView = new ImageView( this );
        try {
            imageView.setBackground( this.getPackageManager().getApplicationIcon( this.getPackageName()));
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT );
            imageParams.width = 240;
            imageParams.height = 240;
            imageView.setLayoutParams( imageParams );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        relativeLayout.addView( imageView, params );
        layout.addView( relativeLayout );
    }

    @Override
    public void onClick(View view) {
        switch( view.getId()) {
            case BTN_ID_CONFIRM :
                // finish();
                nativeLibrary.killMyProcess( 0 );
                break;
        }
    }

    private String getApplicationName( Context context ) {
        ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}
