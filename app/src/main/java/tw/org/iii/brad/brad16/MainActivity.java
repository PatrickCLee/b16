package tw.org.iii.brad.brad16;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView mesg;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


}
