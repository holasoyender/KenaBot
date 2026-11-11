/*
 * File:          Proxy.java
 * Path:          v4\proxy\src\main\java\xyz\kenabot\proxy\Proxy.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-08-12 17:59:53 +02:00
 *
 * SHA-256: f6ab67fc8052d127f6cc58972782712479172085a267747ace8bd4316a59048d
 *
 */


package xyz.kenabot.proxy;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Proxy implements Runnable {

    public static void main(String[] args) {
        Proxy lyricsProxy = new Proxy(System.getenv("PORT") != null ? Integer.parseInt(System.getenv("PORT")) : 80);
        lyricsProxy.listen();
    }


    private ServerSocket serverSocket;
    private volatile boolean running = true;
    static HashMap<String, File> cache;
    static ArrayList<Thread> servicingThreads;
    public Proxy(int port) {

        cache = new HashMap<>();
        servicingThreads = new ArrayList<>();

        new Thread(this).start();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..");
            running = true;
        }

        catch (SocketException se) {
            System.out.println("Socket Exception when connecting to client");
            se.printStackTrace();
        }
        catch (SocketTimeoutException ste) {
            System.out.println("Timeout occured while connecting to client");
        }
        catch (IOException io) {
            System.out.println("IO exception when connecting to client");
        }
    }

    public void listen(){

        while(running){
            try {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new RequestHandler(socket));
                servicingThreads.add(thread);

                thread.start();
            } catch (SocketException e) {
                System.out.println("Server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void closeServer(){
        System.out.println("\nClosing Server..");
        running = false;
        try{
            for(Thread thread : servicingThreads){
                if(thread.isAlive()){
                    System.out.print("Waiting on "+  thread.getId()+" to close..");
                    thread.join();
                    System.out.println(" closed");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try{
            System.out.println("Terminating Connection");
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("Exception closing proxy's server socket");
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        String command;
        while(running){
            System.out.println("Type \"close\" to close server.");
            command = scanner.nextLine();

            if(command.equals("close")){
                running = false;
                closeServer();
            }

            else {
                System.out.println("\n" + command + " was not recognised \n");
            }
        }
        scanner.close();
    }

}