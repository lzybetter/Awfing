package com.lzybetter.awfing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tencent.connect.share.QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

public class Share extends BaseActivity {

    private CircleImageView share_weixin,share_pengyouquan;
    private CircleImageView share_qq, share_kongjian;

    private String title,link,sub,imgAddress;

    private static IWXAPI api;
    private static Tencent mTencent;

    private static final String APP_ID_WECHAT = "wx19a3f33f068897f1";
    private static final String APP_ID_QQ = "101381613";

    private static final int WEIXIN = WXSceneSession;
    private static final int PENGYOUQUAN = WXSceneTimeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        getShare();

        share_weixin = (CircleImageView)findViewById(R.id.share_weixin);
        share_pengyouquan = (CircleImageView)findViewById(R.id.share_pengyouquan);
        share_qq = (CircleImageView)findViewById(R.id.share_qq);
        share_kongjian = (CircleImageView)findViewById(R.id.share_kongjian);

        share_weixin.setOnClickListener(new ClickListener());
        share_pengyouquan.setOnClickListener(new ClickListener());
        share_qq.setOnClickListener(new ClickListener());
        share_kongjian.setOnClickListener(new ClickListener());

    }

    private void getShare() {
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        link = intent.getStringExtra("link");
        sub = intent.getStringExtra("sub");
        imgAddress = intent.getStringExtra("imgAddress");
    }

    private void shareWeixin(int type){
        regToWx();
        Intent intent = getIntent();
        AsynImageLoader asynImageLoader = new AsynImageLoader();
        Bitmap bitmap = asynImageLoader.getBitmapFromLruCache(imgAddress,
                MyApplication.getmMemoryCaches());
        WXWebpageObject wxWebpageObject = new WXWebpageObject();
        wxWebpageObject.webpageUrl=link;
        WXMediaMessage message = new WXMediaMessage(wxWebpageObject);
        message.title = title;
        message.description = sub;
        message.thumbData = bmpToByteArray(bitmap);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = message;
        req.scene = type;
        api.sendReq(req);
    }

    private String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private static byte[] bmpToByteArray(Bitmap bmp) {
        if(bmp!=null){
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, output);
            byte[] result = output.toByteArray();
            return result;
        }else {
            return null;
        }
    }

    private void shareQQ(){
        regToQQ();
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, sub);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, link);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,imgAddress);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,"空军之翼");
        mTencent.shareToQQ(Share.this, params, new ShareListener());
    }

    private void shareQzone(){
        regToQQ();
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE,title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,sub);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,link);
        ArrayList<String> address = new ArrayList();
        address.add(imgAddress);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,address);
        mTencent.shareToQzone(Share.this,params,new ShareListener());
    }

    class ClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.share_weixin:
                    shareWeixin(WEIXIN);
                    finish();
                    break;
                case R.id.share_pengyouquan:
                    shareWeixin(PENGYOUQUAN);
                    finish();
                    break;
                case R.id.share_qq:
                    shareQQ();
                    break;
                case R.id.share_kongjian:
                    shareQzone();
                    break;
                default:
                    break;
            }
        }
    }

    class ShareListener implements IUiListener{

        @Override
        public void onComplete(Object o) {
            Toast.makeText(Share.this,"分享成功",Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(Share.this,"尚未安装qq",Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onCancel() {
            Toast.makeText(Share.this,"分享取消",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode,resultCode,data,new ShareListener());
    }

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this,APP_ID_WECHAT ,true);
        api.registerApp(APP_ID_WECHAT );
    }

    private void regToQQ(){
        mTencent = Tencent.createInstance(APP_ID_QQ, MyApplication.getContext());
    }
}
