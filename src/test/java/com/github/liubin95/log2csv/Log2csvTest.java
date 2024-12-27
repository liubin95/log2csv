package com.github.liubin95.log2csv;


import com.github.liubin95.log2csv.services.TextChangeHandler;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class Log2csvTest extends BasePlatformTestCase {
    private final String proxyRegular = "^\\[(?<startTime>[^]]*)] \"(?<METHOD>[^ ]*) (?<PATH>[^ ]*) (?<PROTOCOL>[^\"]*)\" (?<responseCode>[^ ]*) (?<responseFlags>[^ ]*) (?<responseCodeDetails>[^ ]*) (?<connectionTerminationDetails>[^ ]*) \"(?<upstreamTransportFailureReason>[^\"]*)\" (?<bytesReceived>[^ ]*) (?<bytesSent>[^ ]*) (?<DURATION>[^ ]*) (?<upstreamServiceTime>[^ ]*) \"(?<xForwardedFor>[^\"]*)\" \"(?<userAgent>[^\"]*)\" \"(?<xRequestId>[^\"]*)\" \"(?<AUTHORITY>[^\"]*)\" \"(?<upstreamHost>[^\"]*)\" (?<upstreamCluster>[^ ]*) (?<upstreamLocalAddress>[^ ]*) (?<downstreamLocalAddress>[^ ]*) (?<downstreamRemoteAddress>[^ ]*) (?<requestedServerName>[^ ]*) (?<routeName>[^ ]*)$";
    private final Pattern proxyPattern = Pattern.compile(this.proxyRegular);
    private final String springRegular = "^(?<time>[^ ]*) {2}(?<level>[A-Z]*) (?<pid>\\d*) --- \\[(?<appName>[^ ]*)] \\[(?<thread>[^]]*)] (?<class>[^ ]*) +: (?<msg>[^\\n]*)$";
    private final Pattern springPattern = Pattern.compile(this.springRegular);
    private List<String> proxyLog;
    private List<String> proxyLogBig;
    private List<String> springLog;

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // testData/spring.log
        this.springLog = Files.readAllLines(Paths.get(getTestDataPath(), "spring.log"));
        this.proxyLog = Files.readAllLines(Paths.get(getTestDataPath(), "proxy.log"));
        this.proxyLogBig = Files.readAllLines(Paths.get(getTestDataPath(), "proxy.20000.log"));
    }

    @Test
    public void testExtractNamedGroups() {
        final var namedGroups = TextChangeHandler.extractNamedGroups(this.proxyRegular);
        assertNotEmpty(namedGroups);
        assertEquals(24, namedGroups.size());
        assertEquals("startTime", namedGroups.get(0));
        assertEquals("routeName", namedGroups.get(23));
    }

    @Test
    public void testRegularProxy() {
        final var namedGroups = TextChangeHandler.extractNamedGroups(this.proxyRegular);
        for (String log : this.proxyLog) {
            var matcher = this.proxyPattern.matcher(log);
            if (matcher.matches()) {
                for (String groupName : namedGroups) {
                    System.out.println(groupName + ": " + matcher.group(groupName));
                }
            }
        }
    }

    @Test
    public void testRegularProxyFast() {
        final var namedGroups = TextChangeHandler.extractNamedGroups(this.proxyRegular);
        for (String log : this.proxyLogBig) {
            var matcher = this.proxyPattern.matcher(log);
            if (matcher.matches()) {
                for (String groupName : namedGroups) {
                    System.out.println(groupName + ": " + matcher.group(groupName));
                }
            }
        }
    }

    @Test
    public void testRegularSpring() {
        final var namedGroups = TextChangeHandler.extractNamedGroups(this.springRegular);
        for (String log : this.springLog) {
            var matcher = this.springPattern.matcher(log);
            if (matcher.matches()) {
                for (String groupName : namedGroups) {
                    System.out.println(groupName + ": " + matcher.group(groupName));
                }
            }
        }
    }

    @Test
    public void testRegularGroup() {
        final var log = this.springLog.get(0);

        var regular = "^(?<time>[^ ]*) ";
        var pattern = Pattern.compile(regular);
        var matcher = pattern.matcher(log);
        if (matcher.find()) {
            assertTrue(StringUtils.isNotBlank(matcher.group("time")));
            assertEquals(30, matcher.end());
        }

        regular = "^(?<time>[^ ]*) {2}(?<level>[a-z]*)";
        pattern = Pattern.compile(regular);
        matcher = pattern.matcher(log);
        if (matcher.find()) {
            assertTrue(StringUtils.isNotBlank(matcher.group("time")));
            assertTrue(StringUtils.isBlank(matcher.group("level")));
            assertEquals(31, matcher.end());
        }

        regular = "^(?<time>[^ ]*) {2}(?<level>[A-Z]*)";
        pattern = Pattern.compile(regular);
        matcher = pattern.matcher(log);
        if (matcher.find()) {
            assertTrue(StringUtils.isNotBlank(matcher.group("time")));
            assertTrue(StringUtils.isNotBlank(matcher.group("level")));
            assertEquals(35, matcher.end());
        }

    }
}
