package cc.nufe.tools.listener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 接口使用
 */
public class RequestListenerUser {

    private RequestCallback requestListener;

    public void setRequestListener(String str,RequestCallback requestListener) {
        this.requestListener = requestListener;
        useRequestListener(str);
    }

    public void useRequestListener(String str) {
        //这里面执行操作
        request(str);
    }

    private void request(String Url) {

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request
                .Builder()
                .url(Url)//要访问的链接
                .build();

        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requestListener.error(e.getMessage());
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String result = response.body().string();
                requestListener.success(result);
            }
        });
    }

}
