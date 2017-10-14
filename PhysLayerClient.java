/************************************************************************************
 *	file: PhysLayerClient.java
 *	author: Daniel Spencer
 *	class: CS 380 - computer networks
 *
 *	assignment: Project2
 *	date last modified: 10/13/2017
 *
 *	purpose: Simulate the physical layer of communication. Accepting Signals to form
 *              the preamble and converting 5 bit NZRI encoded to 4bit.
 *
 ************************************************************************************/
import java.io.*;
import java.net.Socket;
import java.util.HashMap;

public class PhysLayerClient{

    public static void main(String[] args) throws IOException{
        byte[] bytes = new byte[32];
        double preamble = 0;
        String[] hByte = new String[64];
        boolean lastSignal = false;
        HashMap<String, String> t = new HashMap<>();
        t.put("11110","0000");
        t.put("01001","0001");
        t.put("10100","0010");
        t.put("10101","0011");
        t.put("01010","0100");
        t.put("01011","0101");
        t.put("01110","0110");
        t.put("01111","0111");
        t.put("10010","1000");
        t.put("10011","1001");
        t.put("10110","1010");
        t.put("10111","1011");
        t.put("11010","1100");
        t.put("11011","1101");
        t.put("11100","1110");
        t.put("11101","1111");
        try (Socket socket = new Socket("18.221.102.182", 38002)) {
            System.out.println("Connected to server.");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            PrintStream out = new PrintStream(socket.getOutputStream());

            for(int index = 0; index < 64; index++){
                int signal = in.read();
                //System.out.print(signal + " ");
                preamble += signal;
            }
            preamble /= 64;

            System.out.println("Baseline established from preamble: " + preamble);


            for(int index = 0; index < 64; index++){
                String fBits = "";
                for(int j = 0; j < 5; j++){
                    boolean signal = in.read() > preamble;
                    if(lastSignal == signal){
                        fBits += "0";
                    }
                    else{
                        fBits += "1";
                    }
                    lastSignal = signal;
                }

                hByte[index] = t.get(fBits);
            }
            System.out.print("Received 32 bytes: ");

            for(int index = 0; index < 32; index++){
                String fHbyte = hByte[2*index];
                String sHbyte = hByte[2*index+1];

                System.out.printf("%X", Integer.parseInt(fHbyte, 2));
                System.out.printf("%X", Integer.parseInt(sHbyte, 2));

                String fullByte = fHbyte + sHbyte;
                bytes[index] = (byte)Integer.parseInt(fullByte, 2);
            }

            System.out.println();
            out.write(bytes);

            if(in.read() == 1){
                System.out.println("Response good.");
            }
            else{
                System.out.println("bad Response");
            }
        }
        System.out.println("Disconnected from server");
    }



}

