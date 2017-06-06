package ua.in.wercool.nrf51bt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by maska on 6/6/17.
 */

public class ServerConnectionHandler
{
    public String accessToken                               = null;
    public String refreshToken                              = null;

    HttpURLConnection urlConnection = null;
    DataOutputStream outputStream;

    public  ServerConnectionHandler()
    {

    }

    public int connect()
    {
        try
        {
            URL url = new URL("https://medproject-169320.appspot.com/api/auth/login");
            try
            {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches (false);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.connect();

                JSONObject jsonParam = new JSONObject();
                try
                {
                    jsonParam.put("email", "test@example.com");
                    jsonParam.put("password", "1234");

                    outputStream = new DataOutputStream(urlConnection.getOutputStream());
                    outputStream.writeBytes(jsonParam.toString());
                    outputStream.flush();
                    outputStream.close();

                    int statusCode = urlConnection.getResponseCode();

                    if (statusCode == HttpURLConnection.HTTP_OK)
                    {
                        // create JSON object from response content
                        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        JSONObject resultJSON = new JSONObject(getResponseText(inputStream));
                        accessToken = resultJSON.get("accessToken").toString();
                        refreshToken = resultJSON.get("refreshToken").toString();
                    }

                    return statusCode;
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return -1;
    }

    public void disconnect()
    {
        if (urlConnection != null)
        {
            urlConnection.disconnect();
        }
    }

    private static String getResponseText(InputStream inStream)
    {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }
}
