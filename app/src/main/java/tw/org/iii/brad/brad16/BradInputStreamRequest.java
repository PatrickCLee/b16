package tw.org.iii.brad.brad16;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

public class BradInputStreamRequest extends Request<byte[]> {
    private Response.Listener<byte[]> listener;
    private Map<String,String> responseHeader;
    private Map<String,String> params;

    public BradInputStreamRequest(//原來帶的只有3個參數,改寫為 增加第3個Response.Listener,因我們要用listener這個詞,故再把呼叫super的參數名改掉
            int method,             //再增加第5個參數
            String url,
            Response.Listener<byte[]> listener,
            @Nullable Response.ErrorListener errorListener,
            Map<String,String> params) {

        super(method, url, errorListener);
        this.listener = listener;
        this.params = params;
    }

    @Override
    public Map<String, String> getParams() {    //看似沒改寫,但此處回傳的是我們定義的泛型類別,若我們定義不同的泛型類別則此處也會更改
        return params;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        responseHeader = response.headers;
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        listener.onResponse(response);
    }
}
