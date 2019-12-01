import jssc.SerialPort;
import jssc.SerialPortException;

import java.util.*;

public class MainMakro {
    static SerialPort serialPort;
    static Commands commands;

    static Map<String, List<String>> macros = new HashMap<>();

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



            if(arr.length > 1){
                if(arr[0].equals("MAKRO")){
                    if(arr[1].equals("SET")){
                        List<String> newCommand = new ArrayList<>();
                        while(true){
                            System.out.println("Add command to makro " + arr[2] + ": ");
                            cmd= reader.nextLine();
                            if(cmd.toLowerCase().equals("end"))
                                break;
                            newCommand.add(cmd);
                        }
                        macros.put(arr[2], newCommand);
                    }
                    else if(arr[1].equals("GET")){
                        List<String> commandsList = macros.get(arr[2]);
                        for (String command : commandsList){
                            System.out.println(command);
                            sendCommand(commands.getCommand(command), (byte)1);
                        }
                    }
                }
                else {
//                    System.out.println(arr[0] + " " + arr[1] + " " + arr[2]);
                    sendCommand(commands.getCommand(arr[0].toUpperCase(), Integer.parseInt(arr[1]), Integer.parseInt(arr[2])), (byte)1);
                }
            } else{
//                System.out.println(arr[0]);
                sendCommand(commands.getCommand(cmd.toUpperCase()), (byte)1);
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
            }
            System.out.println(message + " " + sb.toString());
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
            System.out.println(message + " " + sb.toString());
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
