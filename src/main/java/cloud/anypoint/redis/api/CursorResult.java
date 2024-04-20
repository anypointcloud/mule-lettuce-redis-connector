package cloud.anypoint.redis.api;

public class CursorResult<T> {
    private int cursor;
    private Iterable<T> result;

    public CursorResult(int cursor, Iterable<T> result) {
        this.cursor = cursor;
        this.result = result;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public Iterable<T> getResult() {
        return result;
    }

    public void setResult(Iterable<T> result) {
        this.result = result;
    }
}
