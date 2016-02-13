/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;


/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 30.01.2016.
 * This is the custom RPC implementation used to communicate with the backend
 */
public class RPC {
    CloseableHttpClient client;
    private String userPassword64 ;
    private boolean tls;

    /**
     * Initialize the RPC interface
     * @param user The username to use
     * @param password The password to use
     * @param tls Encrypt the traffic with tls
     */
    public RPC (String user,String password,boolean tls){
        this.userPassword64 = new sun.misc.BASE64Encoder().encode((user + ":" + password).getBytes());
        this.tls = tls;
        if (tls) {
            try {
                this.client = HttpClients.custom()
                        .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContexts.custom()
                                        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                        .build()
                                )
                        ).build();
            } catch (Exception e) {
                System.err.println(e);
                client = null;
            }
        } else {
            this.client = HttpClients.custom().build();
        }
    }

    /**
     * Make a request to the backend
     * @param address The address to connect to
     * @param port THe port to connect to
     * @param content The request message
     * @return The request answer
     */
    public String getRequestAnswer(String address, long port, String content){
        String prot = "http";
        if (this.tls) prot = "https";
        HttpPost request = new HttpPost( prot+"://"+address+":"+port);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Authorization", "Basic " + userPassword64);
        try {
            request.setEntity( new StringEntity( content ) );
            HttpResponse response = client.execute( request );
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String ret = rd.readLine();
            rd.close();
            return ret;
        } catch (Exception e) {
            System.err.println(e);
            return "";
        }
    }

    /**
     * Check if we are able to connect
     * @param address The address to connect to
     * @param port THe port to connect to
     * @return True if the connection works, false if not
     */
    public boolean goodConnection(String address, long port){
        Random rnd = new Random();
        int rndInt = rnd.nextInt(256);
        try {
            JSONObject result = new JSONObject(getRequestAnswer(address, port, "{\"method\":\"getinfo\",\"params\":[],\"id\":"+rndInt+"}"));
            if (result == null) return false;
            if (!result.has("id")) return false;
            try {
                return result.getInt("id") == rndInt;
            }
            catch (Exception e){
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }

}
