package oz.stream.service;

import org.springframework.util.StopWatch;

/**
 * Response time service
 */
public class ResponseTimeService {

    public static final String NANOS = "(nanos)";
    public static final String MS = "(ms) ";
    public static final String SEC = "(sec) ";
    private final StopWatch stopWatch = new StopWatch();

    public ResponseTimeService() {
        this.stopWatch.start();
    }

    public String formatResponseTime() {
        this.stopWatch.stop();

        var sec = Math.round(stopWatch.getTotalTimeSeconds());
        var min = Math.round((float) sec / 60);
        var ms = Math.round(stopWatch.getTotalTimeMillis());
        var nanos = Math.round(stopWatch.getTotalTimeNanos());

        if (min == 0 && sec == 0 && ms == 0) {

            return nanos + NANOS;

        } else if (min == 0 && sec == 0) {

            return ms + MS + nanos + NANOS;

        } else if (min == 0) {

            return sec + SEC + ms + MS + nanos + NANOS;

        }

        return min + "(min) " + sec + SEC + ms + MS + nanos + NANOS;
    }
}