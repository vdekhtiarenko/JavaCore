import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/** A small mutable character sequence with snapshot-based undo. */
public final class CustomStringBuilder {
    private char[] value = new char[16];
    private int length;
    private final Deque<String> snapshots = new ArrayDeque<>();

    public CustomStringBuilder() {}

    public CustomStringBuilder(String initialValue) {
        String text = Objects.requireNonNull(initialValue, "initialValue");
        ensureCapacity(text.length());
        text.getChars(0, text.length(), value, 0);
        length = text.length();
    }

    public CustomStringBuilder append(String text) {
        Objects.requireNonNull(text, "text");
        saveSnapshot();
        ensureCapacity(length + text.length());
        text.getChars(0, text.length(), value, length);
        length += text.length();
        return this;
    }

    public CustomStringBuilder append(char character) {
        saveSnapshot();
        ensureCapacity(length + 1);
        value[length++] = character;
        return this;
    }

    public CustomStringBuilder insert(int index, String text) {
        Objects.requireNonNull(text, "text");
        checkPosition(index);
        saveSnapshot();
        ensureCapacity(length + text.length());
        System.arraycopy(value, index, value, index + text.length(), length - index);
        text.getChars(0, text.length(), value, index);
        length += text.length();
        return this;
    }

    public CustomStringBuilder delete(int start, int end) {
        if (start < 0 || end > length || start > end) {
            throw new IndexOutOfBoundsException("Invalid range: " + start + ".." + end);
        }
        if (start == end) return this;
        saveSnapshot();
        System.arraycopy(value, end, value, start, length - end);
        length -= end - start;
        return this;
    }

    public CustomStringBuilder reverse() {
        if (length < 2) return this;
        saveSnapshot();
        for (int left = 0, right = length - 1; left < right; left++, right--) {
            char character = value[left];
            value[left] = value[right];
            value[right] = character;
        }
        return this;
    }

    /** Восстанавливает состояние, предшествовавшее последнему изменению; возвращает false, если история пуста. */
    public boolean undo() {
        if (snapshots.isEmpty()) return false;
        String previous = snapshots.pop();
        ensureCapacity(previous.length());
        previous.getChars(0, previous.length(), value, 0);
        length = previous.length();
        return true;
    }

    public int length() { return length; }

    public char charAt(int index) {
        if (index < 0 || index >= length) throw new IndexOutOfBoundsException(index);
        return value[index];
    }

    @Override
    public String toString() { return new String(value, 0, length); }

    private void saveSnapshot() { snapshots.push(toString()); }

    private void checkPosition(int index) {
        if (index < 0 || index > length) throw new IndexOutOfBoundsException(index);
    }

    private void ensureCapacity(int required) {
        if (required <= value.length) return;
        int capacity = Math.max(required, value.length * 2 + 2);
        char[] expanded = new char[capacity];
        System.arraycopy(value, 0, expanded, 0, length);
        value = expanded;
    }
}
