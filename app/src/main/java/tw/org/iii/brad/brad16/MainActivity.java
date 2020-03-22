package tw.org.iii.brad.brad16;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView mesg;
    private ImageView img;
    private String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, //要權限
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    permissions,   //有哪些權限想要取得,可塞入多樣
                    123);
        }else{
            init();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,     //要求權限的結果
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  //若取得權限
            init();
        }
    }

    private void init(){
        mesg = findViewById(R.id.mesg);
        img = findViewById(R.id.img);
    }

    public void test1(View view){
        StringRequest request = new StringRequest(  //request有4個參數
                Request.Method.GET,                 //1.方法為get
                "https://www.iii.org.tw",       //2.資料來源的URL
                new Response.Listener<String>() {   //3.Response.Listener
                    @Override
                    public void onResponse(String response) {
                        mesg.setText(response);
                    }
                },
                new Response.ErrorListener() {      //4.Response.ErrorListener
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        MainApp.queue.add(request); //在java底下有創另一個class is-a Application
    }

    public void test2(View view){
        StringRequest request = new StringRequest(
                Request.Method.GET,
                "https://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        mesg.setText(response);
                        parseJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        MainApp.queue.add(request);
    }

    private void parseJSON(String json){
        mesg.setText("");
        try {
            JSONArray root = new JSONArray(json);
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                mesg.append(row.getString("Name")+
                        ":"+row.get("Address")+"\n");
            }
        }catch(Exception e){
            Log.v("brad",e.toString());
        }
    }

    public void test3(View view) {
        ImageRequest request = new ImageRequest(    //之前為StringRequest,現為ImageRequest,建構帶的參數不同
                "https://i.pinimg.com/originals/e5/a9/e8/e5a9e877bcacdc5713d2a8f98412762d.png",//1.URL
                new Response.Listener<Bitmap>() {   //2.Response.Listener泛型Bitmap
                    @Override
                    public void onResponse(Bitmap response) {
                        img.setImageBitmap(response);
                    }
                },
                0,0,            //3.4.為指定的寬高;0,0則為原圖尺寸
                Bitmap.Config.ARGB_8888,            //5.調色盤
                null                    //6.Response.ErrorListener
        );

        MainApp.queue.add(request);                 //一樣queue去add request
    }

    public void test4(View view){
        JsonArrayRequest request = new JsonArrayRequest(//若知道要解的是JsonArray則可用此,但與test2的StringRequest相去無幾,只差一個參數
                "https://data.coa.gov.tw/Service/OpenData/ODwsv/ODwsvTravelFood.aspx",//1.URL
                new Response.Listener<JSONArray>() {    //2.Response.Listener泛型JSONArray
                    @Override
                    public void onResponse(JSONArray response) {
                        parseJSON2(response);
                    }
                },
                new Response.ErrorListener() {          //3.Response.ErrorListener
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        MainApp.queue.add(request);
    }

    private void parseJSON2(JSONArray root){
        mesg.setText("");
        try {
//            JSONArray root = new JSONArray(json);   且少掉這行
            for (int i=0; i<root.length(); i++){
                JSONObject row = root.getJSONObject(i);
                mesg.append(row.getString("Name")+
                        ":"+row.get("Address")+"\n");
            }
        }catch(Exception e){
            Log.v("brad",e.toString());
        }
    }


    public void test5(View view) {
        BradInputStreamRequest request = new BradInputStreamRequest(
                Request.Method.GET,
                "https://pdfmyurl.com/?url=https://www.pchome.com.tw",
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        savePDF(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("brad",error.toString());
                    }
                },
                null
        );
        request.setRetryPolicy(new DefaultRetryPolicy( //傳入已經實做的RetryPolicy
                20*1000,                //1.maximum等待的時間
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//2.3.都為預設
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        MainApp.queue.add(request);
    }

    private void savePDF(byte[] data){
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File saveFile = new File(downloadDir,"brad.pdf");
        try {
            BufferedOutputStream bout =
                    new BufferedOutputStream(new FileOutputStream(saveFile));
            bout.write(data);
            bout.flush();
            bout.close();
            Toast.makeText(this, "Save OK", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.v("brad",e.toString());
        }
    }
}
