import com.alien.enterpriseRFID.reader.AlienClass1Reader;
import com.alien.enterpriseRFID.reader.AlienReaderException;
import com.alien.enterpriseRFID.tags.Tag;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *  Basic functions of Retwork
 * */
class VirtualReader {
    private AlienClass1Reader reader;
    public static int ACKTIME = 50;
    public static int attenuation = 0;
    public static int TURN = 0;
    private Set<String> tagsEPC = new HashSet<>();
    private Tag[] tagList;
    String antennaNum;
    public static Tag[] error;
    public static Tag[] result;
    public Set<String> getError(){
        return AlienUtil.getEPC(error);
    }
    public Set<String> getResult(){
        return AlienUtil.getEPC(result);
    }
    public Set<String> getTagsEPC() {
        return this.tagsEPC;
    }
    public VirtualReader(AlienClass1Reader reader, int antennaNum) {
        this.reader = reader;
        this.antennaNum = String.valueOf(antennaNum);
    }

    public void setSession(String session) throws AlienReaderException {
        setSession(session,attenuation);
    }
    public void setSession(String session,int antennuation)throws AlienReaderException{
        reader.clearTagList();
        reader.setAcquireG2Cycles(1);
        reader.setRFAttenuation(antennuation);
        reader.setAntennaSequence(antennaNum);
        reader.setAcquireG2Selects(3);
        reader.setAcquireG2Count(0);
        reader.setAcqG2Mask(1,32,0,"");
        reader.setAcqG2MaskAction(session);
        reader.setAcquireG2Target(session.substring(0,1));
        reader.getCustomTagList();
    }

    public boolean detectCollision(String session) throws AlienReaderException {
        reader.clearTagList();
        reader.setRFAttenuation(attenuation);
        reader.setAcquireG2Cycles(1);
        reader.setAntennaSequence(antennaNum);
        reader.setAcquireG2Selects(0);
        reader.setAcquireG2Count(1); //读到就结束
        reader.setAcqG2Mask(1,32,0,"");
        reader.setAcqG2MaskAction(session);
        reader.setAcquireG2Target(session.substring(0,1));
        reader.setAcquireTime(ACKTIME);
        result = reader.getCustomTagList();
        Set<String> EPCResult = getResult();
//        Set<String> ErrorResult = getError();
//        EPCResult.removeAll(ErrorResult);
        return !EPCResult.isEmpty();
    }

    // Inventory tags
    public void query()throws AlienReaderException{
//        for(int iii = 0;iii<3;iii++){
        int maskLength = 2;
        for (int i = 0;i<maskLength*maskLength;i++){
            int q = maskLength;
            int index = i;
            String a ="";
            while (q!=0){
                if (index%2 == 0){
                    a = "0" + a;
                }else {
                    a = "1" + a;
                }
                index/=2;
                q/=2;
            }
            maskBit(a,getReader());
        }
        System.out.println(tagsEPC.size());
    }

    public void maskBit(String mask,AlienClass1Reader reader) throws AlienReaderException{
        reader.clearTagList();
        reader.setRFAttenuation(attenuation);
        reader.setAcquireG2Cycles(1);
        reader.setAntennaSequence(antennaNum);
        reader.setAcquireG2Selects(3);
        reader.setAcquireG2Count(1);
        reader.setAcquireTime(0);
//        reader.setAcqG2Mask(1,126,mask.length(),mask);
        reader.setG2MaskBits(128-mask.length(),mask);
        reader.setAcqG2MaskAction("AB");
        reader.setAcquireG2Target("A");
        Tag[] tagList22 = reader.getCustomTagList();
        tagsEPC.addAll(AlienUtil.getEPC(tagList22));
    }
    public AlienClass1Reader getReader() {
        return reader;
    }

    public Tag[] getTagList(){
        return this.tagList;
    }

    public void setFrequence(int level) throws AlienReaderException {
        reader.doReaderCommand("freq="+level);
    }
}
