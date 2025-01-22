package zerocopy_data;

import java.nio.ByteBuffer;

public class Event {
    public ByteBuffer text;
    public ByteBuffer matchTime;

    public Event(ByteBuffer data) {
        text = ByteBuffer.wrap(data.array(), data.position(), data.limit() - data.position());
        matchTime = ByteBuffer.wrap(data.array(), data.position(), data.limit() - data.position());
    }

    public Event read(final ByteBuffer data, final int start, final int end) {
        int pos = start;
        int pTextB = Util.indexOf(data, pos, end, "\"text\":".getBytes()) + 9;
        int pTextE = Util.indexOf(data, pTextB, end, (byte) '"');
        text.limit(pTextE);
        text.position(pTextB);
        int pTimeB = Util.indexOf(data, pos, end, "\"match_time\":".getBytes()) + 15;
        int pTimeE = Util.indexOf(data, pTimeB, end, (byte) '"');
        matchTime.limit(pTimeE);
        matchTime.position(pTimeB);
        return this;
    }
}
