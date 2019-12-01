package com.example.felix.qanswer.Other;

/**
 * CallbackInterface
 * Interface that every Activity have to implement if the Activiy wants to receive data from the AsyncTask
 *
 * @author David
 */

public interface CallbackInterface
{
    /**
     * Gets called in the onPostExecute method in the ServerTask
     *
     * @param returnValue
     * @param request
     * @author David
     */
    void processFinish(String returnValue, String request);
}
