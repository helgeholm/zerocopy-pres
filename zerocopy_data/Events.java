package zerocopy_data;

import java.nio.ByteBuffer;
import java.util.Iterator;

public class Events implements Iterable<Event> {
    private EventIterator iterator = new EventIterator();

    public Events read(final ByteBuffer data) {
        this.iterator.read(data);
        return this;
    }

    @Override
    public Iterator<Event> iterator() {
        return iterator.reset();
    }

    class EventIterator implements Iterator<Event> {
        private Event currentEvent;
        private ByteBuffer data;
        private int pos;
        private int entryEnd;

        public void read(final ByteBuffer data) {
            this.data = data;
            this.currentEvent = new Event(data);
        }

        public EventIterator reset() {
            this.pos = Util.indexOf(data, "\"events\": [".getBytes()) + 12;
            this.entryEnd = this.scanPastEntry();
            return this;
        }

        private int scanPastEntry() {
            final byte[] arr = this.data.array();
            int pos = this.pos;
            final int to = this.data.limit();
            int curlyDepth = 0;
            for (; pos < to; ++pos) {
                byte c = arr[pos];
                if (c == '{')
                    curlyDepth += 1;
                if (c == '}') {
                    curlyDepth -= 1;
                    if (curlyDepth == 0) {
                        return pos + 1;
                    }
                }
            }
            return -1;
        }

        @Override
        public boolean hasNext() {
            return this.entryEnd != -1;
        }

        @Override
        public Event next() {
            this.currentEvent.read(this.data, pos, entryEnd);
            this.pos = this.entryEnd;
            this.entryEnd = this.scanPastEntry();
            return this.currentEvent;
        }
    }
}
