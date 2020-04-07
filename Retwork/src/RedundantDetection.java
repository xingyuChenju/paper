import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/**
 * case study 1: redundant reader detection
 */
public class RedundantDetection extends AlienUtil{
    static int[] results;

    public static void main(String[] args)throws Exception {
        for (int iii = 0; iii < 3; iii++) {
            long timeCost;
            String resultString;
            ArrayList<VirtualReader> readerList = getVirtualReaders(2, 2, 2);
            timeCost = System.currentTimeMillis();
            for (VirtualReader reader : readerList)
                reader.query();

            resultString = (System.currentTimeMillis() - timeCost) + " ";
            results = new int[readerList.size()];
            timeCost = System.currentTimeMillis();
            int[][] graph = Retwork.getTopology(readerList);
            for (int[] aa : graph) {
                for (int aaa : aa)
                    System.out.print(aaa + " ");
                System.out.println();
            }
            for (int i = 0; i < graph.length; i++) {
                readerList.get(i).setSession("AB");
                for (int j = 0; j < graph.length; j++) {
                    if (graph[i][j] == 1)
                        readerList.get(j).setSession("BA",10);
                }
                if (!readerList.get(i).detectCollision("AB")) {
                    results[i] = 1;
                    System.out.print("reader : " + i + " is redundant");
                }
            }
            timeCost = System.currentTimeMillis() - timeCost;
            System.out.print("time_cost ：");
            System.out.println(timeCost);
            int fp = 0;
            int fn = 0;

            for (int i = 0; i < graph.length; i++) {
                Set<String> result = new HashSet<>();
                for (int j = 0; j < graph.length; j++) {
                    if (graph[i][j] == 1)
                        result.addAll(readerList.get(j).getTagsEPC());
                }
                result.retainAll(readerList.get(i).getTagsEPC());
                System.out.println("reader : " + i);
                System.out.println(result.size());
                System.out.println(readerList.get(i).getTagsEPC().size());
                if (result.size() == readerList.get(i).getTagsEPC().size()) {
                    System.out.print("reader : " + i + " is redundant");
                    if (results[i] == 0) {
                        System.out.println("redundant reader : " + i + " is missed");
                        fn++;
                    }
                } else {
                    if (results[i] == 1) {
                        fp++;
                    }
                }
            }

            Set<String> sss = new HashSet<>();
            for (VirtualReader reader : readerList)
                sss.addAll(reader.getTagsEPC());
            System.out.println("numbers of tags ：" + sss.size());
            System.out.println("numbers of readers  : " + readerList.size());
            System.out.println("fn : " + fn);
            System.out.println("fp : " + fp);
            writeFile(resultString + timeCost + " " + sss.size() + " " + readerList.size() + " " +
                    fn + " " + fp + "\n", path + "RedundantDetect\\result6antennas");

        }
    }
}
