import com.alien.enterpriseRFID.reader.AlienReaderException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Retwork extends AlienUtil {
    static int[][] graph;   /*record Retwork result*/
    static int[][] graph2; /*record ground truth*/

    /*
     * The topology is obtained through Retwork.
     */
    public static int[][] getTopology(ArrayList<VirtualReader> readers) throws AlienReaderException {
        int[][] topologyGraph = new int[readers.size()][readers.size()];
        for (int i = 0; i < readers.size() - 1; i++) {
            for (int j = i + 1; j < readers.size(); j++) {
                // select two readers
                VirtualReader reader1 = readers.get(i);
                VirtualReader reader2 = readers.get(j);
                // one sets its tags to B
                reader1.setSession("BA");
                // the other sets its tags to A
                reader2.setSession("AB");
                // the first reader checks A tags within its coverage.
                // If it finds a tag reply, that means there is an edge
                // between the two readers.
                if (reader1.detectCollision("AB")) {
                    topologyGraph[i][j] = 1;
                    topologyGraph[j][i] = 1;
                }
            }
        }
        return topologyGraph;
    }

    /*
    * The ground truth is obtained through inventory.
    */
    public static int[][] getGroundTruth(ArrayList<VirtualReader> readers) throws AlienReaderException {
        int[][] groundTruth = new int[readers.size()][readers.size()];
        for (int i = 0; i < readers.size(); i++)
            readers.get(i).query();
        for (int i = 0; i < readers.size() - 1; i++) {
            for (int j = i + 1; j < readers.size(); j++) {
                VirtualReader reader1 = readers.get(i);
                VirtualReader reader2 = readers.get(j);
                Set<String> set = new HashSet<>();
                set.addAll(reader1.getTagsEPC());
                set.retainAll(reader2.getTagsEPC());
                if (set.size() > 20) {
                    groundTruth[i][j] = 1;
                    groundTruth[j][i] = 1;
                }else if (set.size() > 0) {
                    groundTruth[i][j] = -1;
                    groundTruth[j][i] = -1;
                }
            }
        }
        return groundTruth;
    }
    public static void main(String[] args) throws Exception {
            // these variables are used to validate the time efficiency
            long a;
            long b;
            long cost;
            long cost1;
            // these variables are used to validate the correctness
            int fn = 0;
            int fp = 0;
            int all = 0;
            ArrayList<VirtualReader> readerlist = getVirtualReaders(1, 2, 1);
            graph = new int[readerlist.size()][readerlist.size()];
            graph2 = new int[readerlist.size()][readerlist.size()];
            System.out.println("start");
            int count = 0;
            // obtain the time cost of the ground truth
            a = System.currentTimeMillis();
            graph2 = getGroundTruth(readerlist);
            b = System.currentTimeMillis();
            cost1 = b - a;
            System.out.println("time cost: " + cost1);
            cost = 0;
            // obtain the time cost of Retwork
            a = System.currentTimeMillis();
            graph = getTopology(readerlist);
            b = System.currentTimeMillis();
            cost += b - a;
            System.out.print("time cost：");
            System.out.println(b - a);
            System.out.println("---------------------");
            // validate the correctness
            w:
            for (int ii = 0; ii < graph.length - 1; ii++) {
                for (int i = 1; i < graph[ii].length; i++) {
                    if (graph[ii][i] != graph2[ii][i]) {
                        count++;
                        break w;
                    }
                }
            }
            for (int ii = 0; ii < graph.length - 1; ii++) {
                for (int i = ii + 1; i < graph[ii].length; i++) {
                    if (graph[ii][i] != graph2[ii][i]) {
                        if (graph2[ii][i] == -1) {
                        } else if (graph[ii][i] == 1) {
                            System.out.println("reader : " + ii + " reader : " + i);
                            fp++;
                        } else {
                            System.out.println("reader : " + ii + " reader : " + i);
                            fn++;
                        }
                    }
                    all++;
                }
            }
            // print results
            Set<String> tagAll = new HashSet<>();
            for (VirtualReader reader : readerlist) {
                System.out.println(reader.getTagsEPC().size());
                tagAll.addAll(reader.getTagsEPC());
            }
            // number of all cases
            System.out.println("all : " + all);
            // print the number of false negative cases
            System.out.println("fn : " + fn);
            // print the number of false positive cases
            System.out.println("fp : " + fp);
            File file = new File(path);
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            }
            // write result to file
            String result = tagAll.size() + " " + cost1 + " " + cost  + " " + all + " " + fn + " " + fp+"\n";
            writeFile(result, path + "result4" + "");
    }
}
