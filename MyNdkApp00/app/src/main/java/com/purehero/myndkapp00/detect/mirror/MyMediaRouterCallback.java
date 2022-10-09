package com.purehero.myndkapp00.detect.mirror;

import android.content.Context;
import android.media.MediaRouter;
import android.util.Log;
import android.widget.Toast;

public class MyMediaRouterCallback extends MediaRouter.Callback {
    private static final String TAG = "MyApp00";

    private final MediaRouter mediaRouter;
    private final Context context;

    public MyMediaRouterCallback( Context context ) {
        this.context = context;

        mediaRouter = (MediaRouter) context.getSystemService( Context.MEDIA_ROUTER_SERVICE );
        mediaRouter.addCallback( MediaRouter.ROUTE_TYPE_LIVE_VIDEO | MediaRouter.ROUTE_TYPE_LIVE_AUDIO, this );
        Log.d( TAG, "MyMediaRouterCallback created" );
    }

    public void release() {
        try {
            mediaRouter.removeCallback(this);
        } catch ( Exception e ) {
        }

        Log.d( TAG, "MyMediaRouterCallback release" );
    }

    @Override
    public void onRouteSelected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
        showToastMessage( "onRouteSelected ( %d:%s )", i, routeInfo.toString());
    }

    @Override
    public void onRouteUnselected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
        showToastMessage( "onRouteUnselected ( %d:%s )", i, routeInfo.toString());
    }

    @Override
    public void onRouteAdded(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
        showToastMessage( "onRouteAdded ( %s )", routeInfo.toString());
    }

    @Override
    public void onRouteRemoved(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
        showToastMessage( "onRouteRemoved ( %s )", routeInfo.toString());
    }

    @Override
    public void onRouteChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
        showToastMessage( "onRouteChanged ( %s )", routeInfo.toString());
    }

    @Override
    public void onRouteGrouped(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo, MediaRouter.RouteGroup routeGroup, int i) {
        showToastMessage( "onRouteGrouped \n%s\n%s\n%d )", routeInfo.toString(), routeGroup.toString(), i );
    }

    @Override
    public void onRouteUngrouped(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo, MediaRouter.RouteGroup routeGroup) {
        showToastMessage( "onRouteUngrouped \n%s\n%s )", routeInfo.toString(), routeGroup.toString() );
    }

    @Override
    public void onRouteVolumeChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
        showToastMessage( "onRouteVolumeChanged ( %s )", routeInfo.toString());
    }

    private Toast toast = null;
    private void showToastMessage( String format, Object... objs ) {
        String msg = String.format( format, objs );

        if( toast == null ) {
            toast = Toast.makeText( context, msg, Toast.LENGTH_LONG );
        } else {
            toast.cancel();
            toast.setText( msg );
        }
        toast.show();

        Log.d( TAG, msg );
    }
}
