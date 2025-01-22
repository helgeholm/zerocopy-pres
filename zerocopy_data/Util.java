package zerocopy_data;

import java.io.PrintStream;
import java.nio.ByteBuffer;

public class Util {
    public static void write(final PrintStream ps, final ByteBuffer bb) {
        ps.write(bb.array(), bb.position(), bb.limit() - bb.position());
    }

    public static int indexOf(final ByteBuffer haystack, final int from, final int to, final byte bee) {
        final byte[] arr = haystack.array();

        int pos = from;
        for (; pos < to; ++pos)
            if (arr[pos] == bee)
                return pos;
        return -1;
    }

    public static int indexOf(final ByteBuffer haystack, byte[] needle) {
        return indexOf(haystack, haystack.position(), haystack.limit(), needle);
    }

    public static int indexOf(final ByteBuffer haystack, final int from, final int to, byte[] needle) {
        final byte[] arr = haystack.array();
        final int blen = needle.length - 1;

        int pos = from;
        int j = 0;

        for (; pos < to; ++pos) {
            byte c = arr[pos];
            if (c == needle[j]) {
                if (j == blen) {
                    return pos - j;
                } else
                    j += 1;
            } else if (j != 0) {
                pos -= j;
                j = 0;
            }
        }

        return -1;
    }
}
