package lswc.omstlct;

public class RepTreeEdge {
    public LctNode source, target;
    long weight;

    @Override
    public String toString() {
        return "RepTreeEdge{" +
                "source=" + source.value +
                ", target=" + target.value +
                ", weight=" + weight +
                '}';
    }
}
