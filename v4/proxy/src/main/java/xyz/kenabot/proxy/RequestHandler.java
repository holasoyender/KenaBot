/*
 * File:          RequestHandler.java
 * Path:          v4\proxy\src\main\java\xyz\kenabot\proxy\RequestHandler.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-08-12 17:59:53 +02:00
 *
 * SHA-256: cf93c92dff4749e327e2400f8e9f162a79eaa4dcafd3caa9ab9d297cfa0b077f
 *
 */


package xyz.kenabot.proxy;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import javax.imageio.ImageIO;

public class RequestHandler implements Runnable {

    Socket clientSocket;
    BufferedReader proxyToClientBr;
    BufferedWriter proxyToClientBw;

    public RequestHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
        try{
            this.clientSocket.setSoTimeout(2000);
            proxyToClientBr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            proxyToClientBw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        String requestString;
        try{
            requestString = proxyToClientBr.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading request from client");
            return;
        }

        System.out.println("Request Received " + requestString);
        String request = requestString.substring(0,requestString.indexOf(' '));
        String urlString = requestString.substring(requestString.indexOf(' ')+1);
        urlString = urlString.substring(0, urlString.indexOf(' '));

        if(!urlString.startsWith("http")){
            String temp = "http://";
            urlString = temp + urlString;
        }

        // Check request type
        if(request.equals("CONNECT")){
            System.out.println("HTTPS Request for : " + urlString + "\n");
            handleHTTPSRequest(urlString);
        }
        else{
            System.out.println("HTTP GET for : " + urlString + "\n");
            if(urlString.equals("http:///")) {
                return;
            }
            sendNonCachedToClient(urlString);
        }
    }
    private void sendNonCachedToClient(String urlString){

        try{

            int fileExtensionIndex = urlString.lastIndexOf(".");
            String fileExtension;

            fileExtension = urlString.substring(fileExtensionIndex);

            String fileName = urlString.substring(0,fileExtensionIndex);

            fileName = fileName.substring(fileName.indexOf('.')+1);

            fileName = fileName.replace("/", "__");
            fileName = fileName.replace('.','_');

            if(fileExtension.contains("/")){
                fileExtension = fileExtension.replace("/", "__");
                fileExtension = fileExtension.replace('.','_');
                fileExtension += ".html";
            }

            fileName = fileName + fileExtension;

            // Check if file is an image
            URL remoteURL = new URL(urlString);
            if((fileExtension.contains(".png")) || fileExtension.contains(".jpg") ||
                    fileExtension.contains(".jpeg") || fileExtension.contains(".gif")){
                // Create the URL
                BufferedImage image = ImageIO.read(remoteURL);

                if(image != null) {
                    String line = """
                            HTTP/1.0 200 OK
                            Proxy-agent: ProxyServer/1.0
                            \r
                            """;
                    proxyToClientBw.write(line);
                    proxyToClientBw.flush();

                    ImageIO.write(image, fileExtension.substring(1), clientSocket.getOutputStream());
                } else {
                    System.out.println("Sending 404 to client as image wasn't received from server"
                            + fileName);
                    String error = """
                            HTTP/1.0 404 NOT FOUND
                            Proxy-agent: ProxyServer/1.0
                            \r
                            """;
                    proxyToClientBw.write(error);
                    proxyToClientBw.flush();
                    return;
                }
            }

            else {

                HttpURLConnection proxyToServerCon = (HttpURLConnection)remoteURL.openConnection();
                proxyToServerCon.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                proxyToServerCon.setRequestProperty("Content-Language", "en-US");
                proxyToServerCon.setUseCaches(false);
                proxyToServerCon.setDoOutput(true);

                BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerCon.getInputStream()));


                String line = """
                        HTTP/1.0 200 OK
                        Proxy-agent: ProxyServer/1.0
                        \r
                        """;
                proxyToClientBw.write(line);


                while((line = proxyToServerBR.readLine()) != null){
                    proxyToClientBw.write(line);
                }

                proxyToClientBw.flush();

                proxyToServerBR.close();
            }

            if(proxyToClientBw != null){
                proxyToClientBw.close();
            }
        }

        catch (Exception e){
            //e.printStackTrace();
        }
    }
    private void handleHTTPSRequest(String urlString){
        // Extract the URL and port of remote
        String url = urlString.substring(7);
        String[] pieces = url.split(":");
        url = pieces[0];
        int port  = Integer.parseInt(pieces[1]);

        try{
            for(int i=0;i<5;i++){
                proxyToClientBr.readLine();
            }

            InetAddress address = InetAddress.getByName(url);

            Socket proxyToServerSocket = new Socket(address, port);
            proxyToServerSocket.setSoTimeout(5000);

            String line = """
                    HTTP/1.0 200 Connection established\r
                    Proxy-Agent: ProxyServer/1.0\r
                    \r
                    """;
            proxyToClientBw.write(line);
            proxyToClientBw.flush();

            BufferedWriter proxyToServerBW = new BufferedWriter(new OutputStreamWriter(proxyToServerSocket.getOutputStream()));
            BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerSocket.getInputStream()));
            ClientToServerHttpsTransmit clientToServerHttps =
                    new ClientToServerHttpsTransmit(clientSocket.getInputStream(), proxyToServerSocket.getOutputStream());

            Thread httpsClientToServer = new Thread(clientToServerHttps);
            httpsClientToServer.start();

            try {
                byte[] buffer = new byte[4096];
                int read;
                do {
                    read = proxyToServerSocket.getInputStream().read(buffer);
                    if (read > 0) {
                        clientSocket.getOutputStream().write(buffer, 0, read);
                        if (proxyToServerSocket.getInputStream().available() < 1) {
                            clientSocket.getOutputStream().flush();
                        }
                    }
                } while (read >= 0);
            }
            catch (SocketTimeoutException ignored) {

            }
            catch (IOException e) {
                e.printStackTrace();
            }


            // Close Down Resources
            proxyToServerSocket.close();

            proxyToServerBR.close();

            proxyToServerBW.close();

            if(proxyToClientBw != null){
                proxyToClientBw.close();
            }


        } catch (SocketTimeoutException e) {
            String line = """
                    HTTP/1.0 504 Timeout Occurred after 10s
                    User-Agent: ProxyServer/1.0
                    \r
                    """;
            try{
                proxyToClientBw.write(line);
                proxyToClientBw.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        catch (Exception e){
            System.out.println("Error on HTTPS : " + urlString );
            e.printStackTrace();
        }
    }

    static class ClientToServerHttpsTransmit implements Runnable{

        InputStream proxyToClientIS;
        OutputStream proxyToServerOS;
        public ClientToServerHttpsTransmit(InputStream proxyToClientIS, OutputStream proxyToServerOS) {
            this.proxyToClientIS = proxyToClientIS;
            this.proxyToServerOS = proxyToServerOS;
        }

        @Override
        public void run(){
            try {
                byte[] buffer = new byte[4096];
                int read;
                do {
                    read = proxyToClientIS.read(buffer);
                    if (read > 0) {
                        proxyToServerOS.write(buffer, 0, read);
                        if (proxyToClientIS.available() < 1) {
                            proxyToServerOS.flush();
                        }
                    }
                } while (read >= 0);
            }
            catch (SocketTimeoutException ste) {
                // TODO: handle exception
            }
            catch (IOException e) {
                System.out.println("Proxy to client HTTPS read timed out");
                e.printStackTrace();
            }
        }
    }
}