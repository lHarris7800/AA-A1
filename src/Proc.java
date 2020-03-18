/**
 * Implementation of process ADT
 *
 * Process acts as an immutable data class
 *
 * @author Luke Harris
 */
public class Proc {
    private String procLabel;
    private int vt;

    public Proc(String procLabel,int vt){
        this.procLabel = procLabel;
        this.vt = vt;
    }

    public String procLabel(){ return procLabel; }

    public int vt(){ return vt; }
}
