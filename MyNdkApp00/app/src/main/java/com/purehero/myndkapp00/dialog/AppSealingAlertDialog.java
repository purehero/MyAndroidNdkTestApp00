package com.purehero.myndkapp00.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.purehero.myndkapp00.Utils;

public class AppSealingAlertDialog extends Activity {

    private String  dialogMessage = "";
    private int     dialogType = 0;
    private boolean bShowToast = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        dialogType      = intent.getIntExtra( "type", 0 );
        bShowToast      = intent.getBooleanExtra("showToast", false );
        dialogMessage   = intent.getStringExtra( "msg" );

        setContentView( makeContentView() );
    }

    public static final int DIALOG_TYPE_TOAST           = 0;    // 기본 Toast type
    public static final int DIALOG_TYPE_ALERT           = 1;    //
    public static final int DIALOG_TYPE_ALERT_TIMER     = 2;    //

    private View makeContentView() {
        View ret = null;
        switch( dialogType ) {
            case DIALOG_TYPE_TOAST : ret = new DialogTypeToast(this ).makeContentView(); break;
            case DIALOG_TYPE_ALERT : ret = new DialogTypeAlert(this ).makeContentView(); break;
            case DIALOG_TYPE_ALERT_TIMER: ret = new DialogTypeAlertTimer(this, 10 ).makeContentView(); break;
            default : break;
        }

        final int opacityRate = (int)( 45.0/*불투명도(%)*/ * 2.55 ) << 24;
        if( ret != null ) {
            ret.setBackgroundColor( opacityRate );
        }
        return ret;
    }

    private class DialogTypeAlertTimer extends DialogTypeAlert implements Runnable {
        public DialogTypeAlertTimer(Context context) {
            super(context);
        }

        private int killTimeSec = 10;
        public DialogTypeAlertTimer(Context context, int killTimeSec ) {
            super(context);
            this.killTimeSec = killTimeSec;
        }

        @Override
        protected View makeDialogLayout() {
            View ret = super.makeDialogLayout();
            new Thread( this ).start();

            return ret;
        }

        @Override
        public void run() {
            if( tvConfirmButton != null ) {
                for (int i = 0; i < killTimeSec; i++) {
                    final String exitTimerMsg = String.format("%s(%d)", getResources().getString( android.R.string.yes ), killTimeSec-i);
                    AppSealingAlertDialog.this.runOnUiThread(new Runnable(){
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
                Utils.killMyProcess(0);
            }
        }
    }

    private class DialogTypeAlert extends DialogTypeBase {
        final int BTN_ID_CONFIRM = 0x1111;
        protected TextView tvConfirmButton = null;

        public DialogTypeAlert(Context context) {
            super(context);
        }

        @Override
        protected View makeDialogLayout() {
            LinearLayout layout = new LinearLayout( context );
            layout.setOrientation( LinearLayout.VERTICAL );
            layout.setPadding( 50, 50, 50, 0 );

            makeDialogAppIconLayout( layout );
            makeDialogTitleMessageLayout( layout );
            makeErrorCodeMessageLayout( layout );
            makeDialogMessageLayout( layout );
            makeDialogConfirmLayout( layout );

            return layout;
        }

        @Override
        public void onClick(View view) {
            finish();
            Utils.killMyProcess(0);
        }

        protected void makeDialogConfirmLayout(LinearLayout layout) {
            tvConfirmButton = new TextView( context );
            tvConfirmButton.setText( getResources().getString( android.R.string.yes ));
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

        protected void makeDialogMessageLayout(LinearLayout layout) {
            TextView tvDialogMessage = new TextView( context );
            tvDialogMessage.setText( String.format( "%s", dialogMessage.replace( ". (", ". \n(").replace(". ", ".\n") ));
            tvDialogMessage.setGravity( Gravity.CENTER );
            tvDialogMessage.setTextColor( Color.BLACK );
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            params.topMargin        = 50;
            layout.addView( tvDialogMessage, params );
        }

        protected void makeErrorCodeMessageLayout(LinearLayout layout) {
            int errorCode = parserErrorCode();
            if( errorCode == -1 ) return;

            TextView tvErrorCode = new TextView( context );
            tvErrorCode.setText( String.format( "[%d]", errorCode ));
            tvErrorCode.setTextColor( Color.BLACK );
            tvErrorCode.setGravity( Gravity.CENTER );
            tvErrorCode.setTextSize( 2, 17 );
            // tvErrorCode.setTypeface( tvErrorCode.getTypeface(), Typeface.BOLD );
            tvErrorCode.setPadding( 0, 10, 0, 10 );

            layout.addView( tvErrorCode );
        }

        protected void makeDialogTitleMessageLayout(LinearLayout layout) {
            TextView tvDialogTitle = new TextView( context );
            tvDialogTitle.setText( "Warning" );
            tvDialogTitle.setGravity( Gravity.CENTER );
            tvDialogTitle.setTextColor( Color.BLACK );
            tvDialogTitle.setTextSize( 2, 25 );
            tvDialogTitle.setTypeface( tvDialogTitle.getTypeface(), Typeface.BOLD );
            tvDialogTitle.setPadding( 0, 25, 0, 25 );
            layout.addView( tvDialogTitle );
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

        protected int parserErrorCode() {
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
    };

    private class DialogTypeToast extends DialogTypeBase implements Runnable {
        public DialogTypeToast(Context context) {
            super(context);
        }

        @Override
        protected View makeDialogLayout() {
            LinearLayout layout = new LinearLayout( context );
            layout.setOrientation( LinearLayout.VERTICAL );
            layout.setPadding( 50, 35, 50, 35 );

            TextView tvDialogMessage = new TextView( context );
            tvDialogMessage.setText( String.format( "%s", dialogMessage.replace( ". (", ". \n(").replace(". ", ".\n") ));
            tvDialogMessage.setGravity( Gravity.CENTER );
            tvDialogMessage.setTextColor( Color.BLACK );
            tvDialogMessage.setTypeface( tvDialogMessage.getTypeface(), Typeface.BOLD );
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            params.topMargin        = 50;
            params.bottomMargin     = 50;
            layout.addView( tvDialogMessage, params );

            new Thread( this ).start();

            return layout;
        }

        @Override
        public void run() {
            try {
                Thread.sleep( 3000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            finish();
            Utils.killMyProcess(0);
        }
    };

    private class DialogTypeBase implements View.OnClickListener {
        protected final Context context;

        private final int LAYOUT_ID_LEFT = 0x2222;
        private final int LAYOUT_ID_RIGHT = 0x3333;
        private final int LAYOUT_ID_CENTER = 0x4444;

        public DialogTypeBase(Context context) {
            this.context = context;
        }

        public final View makeContentView() {
            RelativeLayout layout = new RelativeLayout(context);

            RelativeLayout leftLayout = new RelativeLayout(context);
            RelativeLayout rightLayout = new RelativeLayout(context);
            RelativeLayout centerLayout = new RelativeLayout(context);

            RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            makeLeftLayout(leftLayout, leftParams);
            makeRightLayout(rightLayout, rightParams);
            makeCenterLayout(centerLayout, centerParams);

            layout.addView(leftLayout, leftParams);
            layout.addView(rightLayout, rightParams);
            layout.addView(centerLayout, centerParams);

            return layout;
        }

        private void makeRightLayout(RelativeLayout layout, RelativeLayout.LayoutParams params) {
            layout.setId(LAYOUT_ID_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.width = 50;
        }

        private void makeLeftLayout(RelativeLayout layout, RelativeLayout.LayoutParams params) {
            layout.setId(LAYOUT_ID_LEFT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.width = 50;
        }

        private void makeCenterLayout(RelativeLayout layout, RelativeLayout.LayoutParams params) {
            layout.setId(LAYOUT_ID_CENTER);

            // center layout 배치
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            params.addRule(RelativeLayout.LEFT_OF, LAYOUT_ID_RIGHT);
            params.addRule(RelativeLayout.RIGHT_OF, LAYOUT_ID_LEFT);

            // dialog layout 추가
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(makeDialogLayout(), layoutParams);

            // 테두리 그리기
            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(3, 0xFF000000);
            border.setCornerRadius(30);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                layout.setBackgroundDrawable(border);
            } else {
                layout.setBackground(border);
            }
        }

        protected View makeDialogLayout() {
            LinearLayout layout = new LinearLayout(context);
            return layout;
        }

        @Override
        public void onClick(View view) {
        }
    }

    public static void showAlertDialog(Context context, int type, Object msg ) {
        final String message = (String)msg;
        try {
            if(message.length() > 0) {
                Intent intent = new Intent();
                intent.setClass( context, AppSealingAlertDialog.class );
                intent.setAction( "controller" );
                intent.putExtra( "type", type );
                intent.putExtra( "msg", message );

                context.startActivity( intent );
            }
        } catch (SecurityException e ) {
        } catch (IllegalStateException e) {
        } catch (Exception e ) {
        }
    }
}
