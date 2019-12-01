import java.util.*;

public class Commands {
    public static Map<String, byte[]> list;

    Set<String> speedable = new HashSet(Arrays.asList("DOWN", "UP", "RIGHT", "LEFT", "UP_LEFT" , "UP_RIGHT"));

    public Commands(){
        list = new HashMap<>();
        list.put("ADDRESS", new byte[]{48,1});
        list.put("HOME", new byte[]{1,6,4});
        list.put("DOWN", new byte[]{1,6,1,5,5,3,2});
        list.put("UP", new byte[]{1,6,1,5,5,3,1});
        list.put("RIGHT", new byte[]{1,6,1,5,5,2,3});
        list.put("LEFT", new byte[]{1,6,1,5,5,1,3});
        list.put("UP_RIGHT", new byte[]{1,6,1,5,5,2,1});
        list.put("UP_LEFT", new byte[]{1,6,1,5,5,1,1});
        list.put("ZOOM_TELE", new byte[]{1,4,7,2});
        list.put("ZOOM_WIDE", new byte[]{1,4,7,3});
        list.put("ADR_SET", new byte[]{48, 1});
    }
    //szybkosc pan
    //szybkosc til
    //lewo prawo        3-nic, 1-lewo, 2-prawo
    //gora dol     1-gora 2-dol 3-nic
    public byte[] getCommand(String commandName,int speedTilt, int speedPan){
        byte[] command = list.get(commandName);
        if(speedable.contains(commandName)){
            command[3] = (byte)speedTilt;
            command[4] = (byte)speedPan;
        }
        byte[] rt = new byte[command.length];
        System.arraycopy(command, 0, rt, 0, command.length );
        return rt;
    }

    public byte[] getCommand(String commandName,int adr){
        byte[] command = list.get(commandName);
        byte[] rt = new byte[command.length];
        System.arraycopy(command, 0, rt, 0, command.length );
        if(commandName.equals("ADR_SET")){
            rt[1] = (byte)adr;
//            System.out.println(rt[1]);
        }
        return rt;
    }


    public byte[] getCommand(String commandName){
        return list.get(commandName);
    }

}
