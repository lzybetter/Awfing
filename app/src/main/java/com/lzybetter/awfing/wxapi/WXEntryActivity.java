package com.lzybetter.awfing.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.lzybetter.awfing.BaseActivity;
import com.lzybetter.awfing.Content_Show;
import com.lzybetter.awfing.MyApplication;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by lzybetter on 2017/2/28.
 */

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static IWXAPI api;
    private static final String APP_ID_WECHAT = "wx19a3f33f068897f1";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regToWx();
        api.handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {

        String result = null;

        switch (baseResp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "分享取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = "不支持错误";
                break;
            default:
                result = "发送返回";
                break;
        }
        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
        Log.d("test","done");
        finish();
    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this,APP_ID_WECHAT ,true);
        api.registerApp(APP_ID_WECHAT );
    }
}
