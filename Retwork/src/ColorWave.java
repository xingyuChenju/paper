import com.alien.enterpriseRFID.reader.AlienReaderException;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * case study 2: inventory scheduling
 */

public class ColorWave extends AlienUtil {
    int readernum;
    static int colornum = 0;
    int[] colors;
    int[][] array;
    static Map<Integer, ArrayList> map;

    public ColorWave(int[][] array) {
        this.array = array;
        this.readernum = array.length;
        this.colors = new int[readernum];
        map = new HashMap<Integer, ArrayList>();
        if (readernum != 0) {
            for (int i = 0; i < readernum; i++) {
                colors[i] = setColor(array[i], i);
            }
        }
    }

    private int setColor(int[] neighbor_array, int j) {
        int color = 0;
        Set<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < neighbor_array.length; i++) {
            if (neighbor_array[i] == 1) {
                if (colors[i] != 0) {
                    set.add(colors[i]);
                }
            }
        }
        if (set.size() == colornum) {
            colornum++;
            color = colornum;
        } else {
            for (int i = 1; i <= colornum; i++) {
                if (!set.contains(i)) {
                    color = i;
                    break;
                }
            }
        }
        ArrayList<Integer> aa;
        if (map.containsKey(color)) {
            aa = (ArrayList<Integer>) map.get(color);
            aa.add(j);
        } else {
            aa = new ArrayList<Integer>();
            aa.add(j);
            map.put(color, aa);
        }
        return color;
    }

    public static void main(String[] args) throws Exception {
        for (int m = 0; m < 6; m++) {
            VirtualReader.attenuation = 20;
            ArrayList<VirtualReader> readerList = getVirtualReaders(3, 2, 3);
            int[][] a = Retwork.getTopology(readerList);
            for (int[] i : a) {
                for (int j : i) {
                    System.out.print(j + " ");
                }
                System.out.println();
            }
            new ColorWave(a);
            long start = System.currentTimeMillis();
            for (int i = 1; i < colornum; i++) {
                ExecutorService executorService = Executors.newCachedThreadPool();
                ArrayList<Integer> readerIDs = map.get(i);
                List<Query> quarries = new ArrayList<>();
                int k = 0;
                for (int readerID : readerIDs) {
                    VirtualReader reader = readerList.get(readerID);
                    k = k + 3;
                    quarries.add(new Query(reader, k));
                }
                List<Future<Void>> futures = executorService.invokeAll(quarries);
                for (Future<Void> future : futures)
                    future.get();
                executorService.shutdown();
            }
            long end = System.currentTimeMillis();
            long timeCost1 = end - start;
            System.out.println(timeCost1);
            long timeCost2 = System.currentTimeMillis();
            for (VirtualReader reader : readerList)
                reader.query();
            timeCost2 = System.currentTimeMillis() - timeCost2;
            System.out.println(timeCost2);
            colornum-=1;
            String kk = colornum + " " + timeCost1 + " " + timeCost2 + "" +
                    "\n";
            writeFile(kk, path + "ColorWave\\result8Colors");
        }
    }
}

class Query implements Callable<Void> {
    private VirtualReader reader;
    private int frequence;

    public Query(VirtualReader reader, int frequence) {
        this.reader = reader;
        this.frequence = frequence;
    }

    public Void call() {
        try {
            reader.setFrequence(frequence);
            reader.query();
        } catch (AlienReaderException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}