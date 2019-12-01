import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.Scanner;

public class MainCLI {
    static SerialPort serialPort;
    static Commands commands;
    static byte address = 1;
    public static void main(String[] args) {
        init("COM5");
        String cmd = null;
        while(true){
            Scanner reader = new Scanner(System.in);  // Reading from System.in
            System.out.println("Enter a command: ");
            cmd= reader.nextLine();
            if(cmd.toLowerCase().equals("exit"))
                break;
            String[] arr = cmd.split(" ");
            if(arr.length > 2){
                sendCommand(commands.getCommand(arr[0].toUpperCase(), Integer.parseInt(arr[1]), Integer.parseInt(arr[2])), (byte) address);
            }
            else if(arr.length > 1){
                if(arr[0].equals("ADR_CONSOLE")){
                    address = Byte.valueOf(arr[1]);
                    System.out.println("USING ADRES " + address);
                } else {
                    sendCommand(commands.getCommand(arr[0].toUpperCase(), Integer.parseInt(arr[1])), (byte) address);
                }
            } else{
//                System.out.println(arr[0]);
                sendCommand(commands.getCommand(cmd.toUpperCase()), address);
            }
        }


    }

    private static void sendCommand(byte[] command, byte destAdr) {
        try {
            serialPort.writeBytes(createData(command, (byte)0, destAdr));
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        sleep(1);
        byte[] response;
        String message = "";
        try {
            response = ViscoResponseReader.readResponse(serialPort);
            StringBuilder sb = new StringBuilder();
            for (byte b : response){
                sb.append(String.format("%02X ", b));
            }

            if(sb.charAt(3) == '4'){
                message = "ACK";
            } else if (sb.charAt(3) == '5'){
                message = "Command completion";
            } else if (sb.charAt(3) == '6'){
                message = "Error";
            } else {
                message = "Wrong adress";
            }
//            System.out.println(message + " " + sb.toString());
            System.out.println(message);
        }
        catch (ViscoResponseReader.TimeoutException e) {
            e.printStackTrace();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        try {
            response = ViscoResponseReader.readResponse(serialPort);
            StringBuilder sb = new StringBuilder();
            for (byte b : response){
                sb.append(String.format("%02X ", b));
            }

            if(sb.charAt(3) == '4'){
                message = "ACK";
            } else if (sb.charAt(3) == '5'){
                message = "Command completion";
            } else if (sb.charAt(3) == '6'){
                message = "Error";
            }
//            System.out.println(message + " " + sb.toString());
            System.out.println(message);
        }
        catch (ViscoResponseReader.TimeoutException e) {
//            e.printStackTrace();
        } catch (SerialPortException e) {
//            e.printStackTrace();
        }
        sleep(1);
    }

    private static void init(String comName) {
        serialPort = new SerialPort(comName);
        commands = new Commands();
        try {
            serialPort.openPort();
            serialPort.setParams(9600,8,1,0);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public static byte[] createData(byte[] commandData, byte sourceAdr, byte destinationAdr){
        byte[] cmdData = new byte[commandData.length + 1 + 1];
        byte head = (byte)(128 | (sourceAdr & 7) << 4 |destinationAdr & 15);
        byte tail = -1;
        System.arraycopy(commandData, 0, cmdData, 1, commandData.length);
        cmdData[0] = head;
        cmdData[commandData.length + 1]= tail;
        StringBuilder sb = new StringBuilder();
        for (byte b : cmdData){
            sb.append(String.format("%02X ", b));
        }
//        System.out.println(sb.toString());
//        System.out.println("Komenda " + sb.toString());
        return cmdData;
    }

    public static void sleep(int time){
        try{
            Thread.sleep((long) time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
