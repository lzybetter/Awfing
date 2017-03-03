package com.lzybetter.awfing;

/**
 * Created by lzybetter on 2017/2/18.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
