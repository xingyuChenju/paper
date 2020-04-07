import com.alien.enterpriseRFID.reader.*;
import com.alien.enterpriseRFID.tags.Tag;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/**
 *  Basic functions of Alien reader
 * */
public class AlienUtil {
    //IPs of readers
    static String ip1 = "192.168.1.100";
    static String ip2 = "192.168.1.101";
    static String ip3 = "192.168.1.102";
    static String path =  ".\\RESULT\\";

    public static ArrayList<VirtualReader> getVirtualReaders(ArrayList<String> ips)throws AlienReaderException {
        ArrayList<VirtualReader> virtualReaders = new ArrayList<VirtualReader>();
        for (String ip : ips) {
            AlienClass1Reader reader = initialize(ip);
            for (int i = 0; i < 2; i++)
                virtualReaders.add(new VirtualReader(reader, i));
        }
        return virtualReaders;
    }
    public static ArrayList<VirtualReader> getVirtualReaders(int antennaNumbers1,int antennaNumbers2,int antennaNumbers3)throws AlienReaderException {
        ArrayList<VirtualReader> virtualReaders = new ArrayList<VirtualReader>();
        AlienClass1Reader reader1 = initialize(ip1);
        AlienClass1Reader reader2 = initialize(ip2);
        AlienClass1Reader reader3 = initialize(ip3);
        for (int i = 0;i<antennaNumbers1;i++)
            virtualReaders.add(new VirtualReader(reader1,i));
        for (int i = 0;i<antennaNumbers2;i++)
            virtualReaders.add(new VirtualReader(reader2,i));
        for (int i = 0;i<antennaNumbers3;i++)
            virtualReaders.add(new VirtualReader(reader3,i));
        return virtualReaders;
    }


    public static AlienClass1Reader initialize(String ip)throws AlienReaderException{
        AlienClass1Reader reader = new AlienClass1Reader(); // Create reader object
        reader.setConnection(ip, 23);
        reader.setUsername("alien");
        reader.setPassword("password");
        reader.open();
        reader.setReaderFunction("Alien");
        reader.setPersistTime(-1);
        reader.setRFModulation("DRM");
        reader.setAcquireMode("INVENTORY");
        reader.setAcquireTime(0);
        reader.setRFAttenuations(0,0,0);
        reader.setTagListCustomFormat("ID:%k,  readnum:%r,  RSSI:%m");
        reader.setTagListFormat(AlienClass1Reader.CUSTOM_FORMAT);
        reader.setAcqG2SL("ALL");
        reader.setAcquireG2Session(2);
        reader.setAcquireG2Q(7);
        reader.doReaderCommand("AcqG2QMax = 12");
        reader.setAcquireG2OpsMode(AlienClass1Reader.OFF);
        reader.doReaderCommand("AcqG2AntennaCombine = OFF");
        return reader;
    }

    public static Set<String> getEPC(Tag[] tagList){
        Set<String> def = new HashSet<>();
        if (tagList == null) {
            System.out.println("No Tags Found");
        } else {
//            System.out.println("Tag111111(s) found:"+tagList.length);
            for (int i=0; i<tagList.length; i++) {
                Tag tag = tagList[i];
                def.add(tag.getTagID());
            }
        }
        return def;
    }

    public static void writeFile(String str,String root) {
        byte bt[] = new byte[1024];
        bt = str.getBytes();
        try {
            FileOutputStream in = new FileOutputStream(root+".txt", true);
            try {
                in.write(bt, 0, bt.length);
                in.flush();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
