import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BooleanSupplier;

import org.json.JSONObject;
import org.json.JSONTokener;

import zerocopy_data.Events;
import zerocopy_data.Util;
import zerocopy_data.Event;

public class Bench {
    public static void main(String[] args) throws InterruptedException, IOException {
        var inData = ByteBuffer.wrap(Files.readAllBytes(Paths.get("haugesund-moss-2024-12-08.json")));
        var outData = ByteBuffer.wrap(new byte[10000]);

        var before = System.currentTimeMillis();
        BooleanSupplier timeout = () -> System.currentTimeMillis() - before >= 1000;
        if (args.length > 0) {
            long ops;
            if (args[args.length - 1].equals("zc")) {
                System.out.println("Benchmarking Zero Copy");
                ops = benchZc(inData, outData, timeout);
            } else {
                System.out.println("Benchmarking Parse");
                ops = benchParse(inData, outData, timeout);
            }
            System.out.println(String.format("Operations done in 1 second: %,d", ops));
        } else {
            System.out.println("Demo Mode");
            benchZc(inData, outData, () -> System.currentTimeMillis() - before >= 10);
            outData.flip();
            Util.write(System.out, outData);
            System.out.flush();
        }
    }

    static long benchZc(ByteBuffer in, ByteBuffer out, BooleanSupplier done) {
        long count = 0;
        in.mark();
        Events events = new Events();
        while (!done.getAsBoolean()) {
            in.reset();
            out.clear();
            events.read(in);
            for (Event event : events) {
                out.put(event.matchTime);
                out.putChar('-');
                out.put(event.text);
                out.putChar('\n');
            }
            count += 1;
        }
        return count;
    }

    static long benchParse(ByteBuffer in, ByteBuffer out, BooleanSupplier done) {
        long count = 0;
        in.mark();
        while (!done.getAsBoolean()) {
            in.reset();
            out.clear();
            var outChars = out.asCharBuffer();
            JSONTokener tokener = new JSONTokener(new ByteArrayInputStream(in.array()));
            JSONObject root = new JSONObject(tokener);
            var events = root.getJSONArray("events");
            for (var i = 0; i < events.length(); i++) {
                var event = events.getJSONObject(i);
                var matchTime = event.getString("match_time");
                var text = event.getString("text");
                outChars.append(matchTime);
                outChars.append("-");
                outChars.append(text);
                outChars.append("\n");
            }
            count += 1;
        }
        return count;
    }

}