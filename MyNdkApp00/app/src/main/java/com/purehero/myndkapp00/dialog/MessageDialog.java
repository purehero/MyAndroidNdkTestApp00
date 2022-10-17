package com.purehero.myndkapp00.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

    TextView tvConfirmButton = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int type = intent.getIntExtra( "type", 0 );
        dialogMessage = intent.getStringExtra( "msg" );

        setContentView( makeContentView( type ) );
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

    private View makeContentView( int type ) {
        View ret = null;

        switch( type ) {
            case 1 :
                ret = new DialogType01( this ).makeContentView();
                break;

            case 0 :
                errorCode = parserErrorCode();
                ret = new DialogType00( this, this ).makeContentView();

                final int killTimeSec = 10;
                new Thread( new Runnable(){
                    @Override
                    public void run() {
                        if( tvConfirmButton != null ) {
                            for (int i = 0; i < killTimeSec; i++) {
                                final String exitTimerMsg = String.format("%s(%d)", getResources().getString( android.R.string.yes ), killTimeSec-i);
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

                            finish();
                            exitProcess();
                        }
                    }
                }).start();
                break;

            default :
                break;
        }

        if( ret != null ) {
            ret.setBackgroundColor( 0x80000000 );
        }

        return ret;
    }

    class DialogType01 extends DialogType00 {
        private DialogType01(Context context, View.OnClickListener listener) {
            super(context, listener);
        }

        public DialogType01( Context context ) {
            this(context, null);
        }

        @Override
        protected View makeDialogLayout() {
            LinearLayout layout = new LinearLayout( context );
            layout.setOrientation( LinearLayout.VERTICAL );
            layout.setPadding( 50, 35, 50, 35 );

            TextView tvDialogMessage = new TextView( context );
            // tvDialogMessage.setText( dialogMessage );
            tvDialogMessage.setText( String.format( "%s", dialogMessage.replace(". ", ".\n") ));
            tvDialogMessage.setGravity( Gravity.CENTER );
            tvDialogMessage.setTextColor( Color.BLACK );
            tvDialogMessage.setTypeface( tvDialogMessage.getTypeface(), Typeface.BOLD );
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            params.topMargin        = 50;
            params.bottomMargin     = 50;
            layout.addView( tvDialogMessage, params );

            return layout;
        }
    }

    class DialogType00 {
        protected final Context context;
        private final View.OnClickListener listener;

        public DialogType00( Context context, View.OnClickListener listener ) {
            this.context = context;
            this.listener = listener;
        }

        public View makeContentView() {
            RelativeLayout layout = new RelativeLayout( context );

            RelativeLayout leftLayout = new RelativeLayout( context );
            RelativeLayout rightLayout = new RelativeLayout( context );
            RelativeLayout centerLayout = new RelativeLayout( context );

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

        protected void makeRightLayout(RelativeLayout layout, RelativeLayout.LayoutParams params) {
            layout.setId( LAYOUT_ID_RIGHT );
            params.addRule( RelativeLayout.ALIGN_PARENT_RIGHT );
            params.width = 50;
        }

        protected void makeLeftLayout(RelativeLayout layout, RelativeLayout.LayoutParams params) {
            layout.setId( LAYOUT_ID_LEFT );
            params.addRule( RelativeLayout.ALIGN_PARENT_LEFT );
            params.width = 50;
        }

        protected void makeCenterLayout(RelativeLayout layout, RelativeLayout.LayoutParams params) {
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
            border.setStroke(3, 0xFF000000 );
            border.setCornerRadius( 30 );
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable(border);
            } else {
                layout.setBackground(border);
            }
        }

        protected View makeDialogLayout() {
            LinearLayout layout = new LinearLayout( context );
            layout.setOrientation( LinearLayout.VERTICAL );
            layout.setPadding( 50, 35, 50, 35 );

            makeDialogTitleMessageLayout( layout );
            makeErrorCodeMessageLayout( layout );
            makeDialogAppIconLayout( layout );
            makeDialogMessageLayout( layout );
            makeDialogConfirmLayout( layout );

            return layout;
        }

        protected void makeDialogConfirmLayout(LinearLayout layout) {
            tvConfirmButton = new TextView( context );
            tvConfirmButton.setText( context.getResources().getString( android.R.string.yes ) );
            tvConfirmButton.setTextColor( Color.BLUE );
            tvConfirmButton.setId( BTN_ID_CONFIRM );
            if( listener != null ) {
                tvConfirmButton.setOnClickListener( listener );
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            params.rightMargin      = 50;
            params.topMargin        = 100;
            params.bottomMargin     = 50;
            params.gravity          = Gravity.RIGHT | Gravity.BOTTOM;
            layout.addView( tvConfirmButton, params );
        }

        protected void makeDialogMessageLayout(LinearLayout layout) {
            TextView tvDialogMessage = new TextView( context );
            //tvAppName.setText( String.format( "\n\n\"%s\"\n\n잠시 후 종료됩니다.\n\n", getApplicationName( this )));
            tvDialogMessage.setText( String.format( "%s", dialogMessage.replace(". ", ".\n") ));
            tvDialogMessage.setGravity( Gravity.CENTER );
            tvDialogMessage.setTextColor( Color.BLACK );
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            params.topMargin        = 100;
            params.bottomMargin     = 50;
            layout.addView( tvDialogMessage, params );

            TextView tvExitTimerMessage = new TextView( context );
            tvExitTimerMessage.setText( "잠시 후 종료됩니다." );
            tvExitTimerMessage.setGravity( Gravity.CENTER );
            tvExitTimerMessage.setTextColor( Color.BLACK );
            layout.addView( tvExitTimerMessage );
        }

        protected void makeDialogTitleMessageLayout(LinearLayout layout) {
            TextView tvDialogTitle = new TextView( context );
            tvDialogTitle.setText( "위협이 감지되었습니다." );
            tvDialogTitle.setGravity( Gravity.CENTER );
            tvDialogTitle.setTextColor( Color.RED );
            tvDialogTitle.setTextSize( 2, 20 );
            tvDialogTitle.setTypeface( tvDialogTitle.getTypeface(), Typeface.BOLD );
            tvDialogTitle.setPadding( 0, 25, 0, 0 );
            layout.addView( tvDialogTitle );
        }

        protected void makeErrorCodeMessageLayout(LinearLayout layout) {
            if( errorCode == -1 ) return;

            TextView tvErrorCode = new TextView( context );
            tvErrorCode.setText( String.format( "[%d]", errorCode ));
            //tvErrorCode.setTextColor( Color.BLUE );
            tvErrorCode.setGravity( Gravity.CENTER );
            tvErrorCode.setTextSize( 2, 17 );
            tvErrorCode.setTypeface( tvErrorCode.getTypeface(), Typeface.BOLD );
            tvErrorCode.setPadding( 0, 10, 0, 75 );

            layout.addView( tvErrorCode );
        }

        protected void makeDialogAppIconLayout(LinearLayout layout) {
            RelativeLayout relativeLayout = new RelativeLayout( context );
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT );
            params.addRule( RelativeLayout.CENTER_IN_PARENT );

            ImageView imageView = new ImageView( context );
            try {
                imageView.setBackground( context.getPackageManager().getApplicationIcon( context.getPackageName()));
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
    }



    @Override
    public void onClick(View view) {
        switch( view.getId()) {
            case BTN_ID_CONFIRM :
                finish();
                exitProcess();
                break;
        }
    }

    private void exitProcess() {
        NativeLibrary nativeLibrary = new NativeLibrary();
        nativeLibrary.killMyProcess( 0 );
    }

    private String getApplicationName( Context context ) {
        ApplicationInfo applicationInfo = context.getApplicationContext().getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static void showAlertDialog(Context context, int type, Object msg ) {
        final String message = (String)msg;
        try {
            if(message.length() > 0) {
                Intent intent = new Intent();
                intent.setClass( context, MessageDialog.class );
                intent.setAction( "controller" );
                intent.putExtra( "type", type );
                intent.putExtra( "msg", message );
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity( intent );

                NativeLibrary nativeLibrary = new NativeLibrary();
                nativeLibrary.killMyProcess( 5 );
            }
        } catch (SecurityException e ) {
        } catch (IllegalStateException e) {
        } catch (Exception e ) {
        }
    }
}
