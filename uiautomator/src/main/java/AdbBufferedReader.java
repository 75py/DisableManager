import java.io.IOException;
import java.io.Reader;

public class AdbBufferedReader extends Reader {

    private final Object lock = new Object();
    private Reader ins;
    private char[] buf;
    private int pos;
    private int end;
    private int mark = -1;
    private int markLimit = -1;
    private boolean lastWasCR;
    private boolean markedLastWasCR;

    public AdbBufferedReader(Reader ins) {
        this(ins, 8192);
    }

    public AdbBufferedReader(Reader ins, int size) {
        super(ins);
        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0");
        }
        this.ins = ins;
        buf = new char[size];
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (!isClosed()) {
                ins.close();
            }
        }
    }

    private int fillBuf() throws IOException {
        if (mark == -1 || (pos - mark >= markLimit)) {
            /* mark isn't set or has exceeded its limit. use the whole buffer */
            int result = ins.read(buf, 0, buf.length);
            if (result > 0) {
                mark = -1;
                pos = 0;
                end = result;
            }
            return result;
        }

        if (mark == 0 && markLimit > buf.length) {
            /* the only way to make room when mark=0 is by growing the buffer */
            int newLength = buf.length * 2;
            if (newLength > markLimit) {
                newLength = markLimit;
            }
            char[] newbuf = new char[newLength];
            System.arraycopy(buf, 0, newbuf, 0, buf.length);
            buf = newbuf;
        } else if (mark > 0) {
            /* make room by shifting the buffered data to left mark positions */
            System.arraycopy(buf, mark, buf, 0, buf.length - mark);
            pos -= mark;
            end -= mark;
            mark = 0;
        }

        /* Set the new position and mark position */
        int count = ins.read(buf, pos, buf.length - pos);
        if (count != -1) {
            end += count;
        }
        return count;
    }

    private boolean isClosed() {
        return buf == null;
    }

    @Override
    public void mark(int markLimit) throws IOException {
        if (markLimit < 0) {
            throw new IllegalArgumentException("markLimit < 0:" + markLimit);
        }
        synchronized (lock) {
            checkNotClosed();
            this.markLimit = markLimit;
            this.mark = pos;
            this.markedLastWasCR = lastWasCR;
        }
    }

    private void checkNotClosed() throws IOException {
        if (isClosed()) {
            throw new IOException("BufferedReader is closed");
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        synchronized (lock) {
            checkNotClosed();
            int ch = readChar();
            if (lastWasCR && ch == '\n') {
                ch = readChar();
            }
            lastWasCR = false;
            return ch;
        }
    }

    private int readChar() throws IOException {
        if (pos < end || fillBuf() != -1) {
            return buf[pos++];
        }
        return -1;
    }

    public static void checkOffsetAndCount(int arrayLength, int offset, int count) {
        if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
            throw new ArrayIndexOutOfBoundsException("length=" + "{sourceLength}" + "; regionStart=" + offset
                    + "; regionLength=" + count);
        }
    }

    @Override
    public int read(char[] buffer, int offset, int length) throws IOException {
        synchronized (lock) {
            checkNotClosed();
            checkOffsetAndCount(buffer.length, offset, length);
            if (length == 0) {
                return 0;
            }

            maybeSwallowLF();

            int outstanding = length;
            while (outstanding > 0) {
                // If there are chars ins the buffer, grab those first.
                int available = end - pos;
                if (available > 0) {
                    int count = available >= outstanding ? outstanding : available;
                    System.arraycopy(buf, pos, buffer, offset, count);
                    pos += count;
                    offset += count;
                    outstanding -= count;
                }

                /*
                 * Before attempting to read from the underlying stream, make
                 * sure we really, really want to. We won't bother if we're
                 * done, or if we've already got some chars and reading from the
                 * underlying stream would block.
                 */
                if (outstanding == 0 || (outstanding < length && !ins.ready())) {
                    break;
                }

                // assert(pos == end);

                /*
                 * If we're unmarked and the requested size is greater than our
                 * buffer, read the chars directly into the caller's buffer. We
                 * don't read into smaller buffers because that could result ins
                 * a many reads.
                 */
                if ((mark == -1 || (pos - mark >= markLimit)) && outstanding >= buf.length) {
                    int count = ins.read(buffer, offset, outstanding);
                    if (count > 0) {
                        outstanding -= count;
                        mark = -1;
                    }
                    break; // assume the source stream gave us all that it could
                }

                if (fillBuf() == -1) {
                    break; // source is exhausted
                }
            }

            int count = length - outstanding;
            if (count > 0) {
                return count;
            }
            return -1;
        }
    }

    final void chompNewline() throws IOException {
        while ((pos != end || fillBuf() != -1) && (buf[pos] == '\n' || buf[pos] == '\r')) {
            ++pos;
        }
    }

    private void maybeSwallowLF() throws IOException {
        if (lastWasCR) {
            chompNewline();
            lastWasCR = false;
        }
    }

    public String readLine() throws IOException {
        synchronized (lock) {
            checkNotClosed();

            maybeSwallowLF();

            // Do we have a whole line ins the buffer?
            for (int i = pos; i < end; ++i) {
                char ch = buf[i];
                if (ch == '\n' || ch == '\r') {
                    String line = new String(buf, pos, i - pos);
                    pos = i + 1;
                    lastWasCR = (ch == '\r');
                    return line;
                }
            }

            // Accumulate buffers ins a StringBuilder until we've read a whole line.
            StringBuilder result = new StringBuilder(end - pos + 80);
            result.append(buf, pos, end - pos);
            while (true) {
                pos = end;
                if (fillBuf() == -1) {
                    // If there's no more input, return what we've read so far, if anything.
                    return (result.length() > 0) ? result.toString() : null;
                }

                // Do we have a whole line ins the buffer now?
                for (int i = pos; i < end; ++i) {
                    char ch = buf[i];
                    if (ch == '\n' || ch == '\r') {
                        result.append(buf, pos, i - pos);
                        pos = i + 1;
                        lastWasCR = (ch == '\r');
                        return result.toString();
                    }
                }

                // Add this whole buffer to the line-ins-progress and try again...
                result.append(buf, pos, end - pos);
            }
        }
    }

    @Override
    public boolean ready() throws IOException {
        synchronized (lock) {
            checkNotClosed();
            return ((end - pos) > 0) || ins.ready();
        }
    }

    @Override
    public void reset() throws IOException {
        synchronized (lock) {
            checkNotClosed();
            if (mark == -1) {
                throw new IOException("Invalid mark");
            }
            this.pos = mark;
            this.lastWasCR = this.markedLastWasCR;
        }
    }

    @Override
    public long skip(long charCount) throws IOException {
        if (charCount < 0) {
            throw new IllegalArgumentException("charCount < 0: " + charCount);
        }
        synchronized (lock) {
            checkNotClosed();
            if (end - pos >= charCount) {
                pos += charCount;
                return charCount;
            }

            long read = end - pos;
            pos = end;
            while (read < charCount) {
                if (fillBuf() == -1) {
                    return read;
                }
                if (end - pos >= charCount - read) {
                    pos += charCount - read;
                    return charCount;
                }
                // Couldn't get all the characters, skip what we read
                read += (end - pos);
                pos = end;
            }
            return charCount;
        }
    }
}
