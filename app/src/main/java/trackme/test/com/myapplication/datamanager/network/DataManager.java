package trackme.test.com.myapplication.datamanager.network;

import android.content.Context;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import trackme.test.com.myapplication.AppController;
import trackme.test.com.myapplication.util.PreferencesHelper;

public class DataManager {

    private String TAG = DataManager.class.getSimpleName();
    private static DataManager instance = null;
    private final Context context;

    private final PreferencesHelper preferencesHelper;


    private DataManager(Context context) {
        this.context = context;
        preferencesHelper = PreferencesHelper.getInstance(context);

    }

    public static DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }


    public void jsonObjectRequest(int method, final String url, final JSONObject body, final VolleyJsonCallBack callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError(error);

            }
        })

        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Content-Type", "application/json;charset=utf-8");
                headers.put("charset", "utf-8");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void jsonObjectRequestWithHeaders(int method, final String url, final JSONObject body, final VolleyJsonCallBack callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onSuccess(response);


            }
        }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError(error);

            }
        }

        )

        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");

                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    // do stuff
                }
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Map<String, String> headers = response.headers;
                    String jsonString =
                            new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    JSONObject jsonObject = new JSONObject(jsonString);

                    return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        try {
            if (method == com.android.volley.Request.Method.PUT || method == com.android.volley.Request.Method.POST) {
                jsonObjectRequest.setShouldCache(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppController.getInstance().

                addToRequestQueue(jsonObjectRequest);
    }

    public void jsonObjectRequestWithHeadersNoTimeout(int method, final String url, final JSONObject body, final VolleyJsonCallBack callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(null, method, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.onSuccess(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                callback.onError(error);

            }
        })

        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");


                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    // do stuff
                }
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public interface VolleyJsonCallBack {
        void onSuccess(JSONObject object);

        void onError(VolleyError error);
    }

    public interface VolleyJsonArrayCallBack {
        void onSuccess(JSONArray object);

        void onError(VolleyError error);
    }


    //********************************************************************SharedPreference related methods***********************************************************************************************
    public void writeString(String key, String value) {
        preferencesHelper.writeString(key, value);
    }

    public String readString(String key) {
        return preferencesHelper.readString(key);
    }

    public void writeBoolean(String key, boolean value) {
        preferencesHelper.writeBoolean(key, value);
    }

    public boolean readBoolean(String key) {
        return preferencesHelper.readBoolean(key);
    }

    public void writeInt(String key, int value) {
        preferencesHelper.writeInt(key, value);
    }

    public int readInt(String key) {
        return preferencesHelper.readInt(key);
    }

    public void writeLong(String key, long value) {
        preferencesHelper.writeLong(key, value);
    }

    public Long readLong(String key, long defaultValue) {
        return preferencesHelper.readLong(key, defaultValue);
    }

    public void removePref(String key) {
        preferencesHelper.removePref(key);
    }

    public void clearSharedPref() {
        preferencesHelper.clear();
    }
/******************************************************************************************************************************************************/

}
