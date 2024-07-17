package lswc;

public class StreamingEdge {
    public int source, target;
    public long timeStamp;

    public StreamingEdge(int source, int target, long timeStamp) {
        this.source = source;
        this.target = target;
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "StreamingEdge{" +
                "source=" + source +
                ", target=" + target +
                ", timeStamp=" + timeStamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamingEdge that = (StreamingEdge) o;
        return source == that.source && target == that.target && timeStamp == that.timeStamp;
    }

    @Override
    public int hashCode() {
        int result = source;
        result = 31 * result + target;
        result = 31 * result + Long.hashCode(timeStamp);
        return result;
    }
}
