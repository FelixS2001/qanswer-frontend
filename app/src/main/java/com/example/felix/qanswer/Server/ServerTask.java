package com.example.felix.qanswer.Server;

import android.os.AsyncTask;

import com.example.felix.qanswer.Other.CallbackInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ServerTask
 * Access to the Server with the URLs
 *
 * @author David Huemer
 */

public class ServerTask extends AsyncTask<String, Void, String>
{

    public CallbackInterface callbackInterface = null;
    String request;

    /**
     * Fetching the data with a HTTP-Connection using an AsyncTask
     *
     * @param strings
     * @return
     * @author David
     */
    @Override
    protected String doInBackground(String... strings)
    {

        String result = "";
        String urlString = strings[0];
        request = strings[1];

        try
        {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK)
            {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = br.readLine();
                while (line != null)
                {
                    result += line;
                    line = br.readLine();
                }
            }
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Returning the data by calling the Callback-Method which every activity which wants to get data has to implement
     *
     * @param result
     * @author David
     */
    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        callbackInterface.processFinish(result, request);
    }
}
