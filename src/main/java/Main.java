import jssc.SerialPort;
import jssc.SerialPortException;

public class Main {
    static SerialPort serialPort;
    static Commands commands;
    public static void main(String[] args) {
        init("COM5");

        sendCommand(commands.getCommand("ADDRESS"), (byte)1);
        sendCommand(commands.getCommand("HOME"), (byte)1);
        sendCommand(commands.getCommand("LEFT"), (byte)1);
        sendCommand(commands.getCommand("RIGHT"), (byte)1);
        sendCommand(commands.getCommand("UP", 5,5), (byte)1);
        sendCommand(commands.getCommand("DOWN"), (byte)1);
        sendCommand(commands.getCommand("UP_LEFT", 1 ,5), (byte)1);
        sendCommand(commands.getCommand("DOWN"), (byte)1);
        sendCommand(commands.getCommand("UP_RIGHT", 3, 2), (byte)1);
        sendCommand(commands.getCommand("ZOOM_TELE"), (byte)1);
        sendCommand(commands.getCommand("ZOOM_WIDE"), (byte)1);
    }

    //szybkosc pan
    //szybkosc til
    //lewo prawo 3-nic, 1-lewo, 2-prawo
    //gora dol 1-gora 2-dol 3-nic

    private static void sendCommand(byte[] command, byte destAdr) {
        try {
            serialPort.writeBytes(createData(command, (byte)0, destAdr));
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        sleep(2);
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
        sleep(2);
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
        sleep(2);
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
        System.out.println(sb.toString());
        System.out.println("Komenda " + sb.toString());
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
