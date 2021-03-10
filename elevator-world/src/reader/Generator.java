package reader;

public class Generator {
    public Long tFrom;
    public Long tTo;
    public Long oFrom;
    public Long oTo;
    public Long dFrom;
    public Long dTo;
    public Long period;

    public Generator(Long tFrom, Long tTo, Long oFrom, Long oTo, Long dFrom, Long dTo, Long period) {
        this.tFrom = tFrom;
        this.tTo = tTo;
        this.oFrom = oFrom;
        this.oTo = oTo;
        this.dFrom = dFrom;
        this.dTo = dTo;
        this.period = period;
    }
}
