package examples;

import com.dvdphobia.blueflex.service.AppService;
import com.dvdphobia.blueflex.utils.AppLogger;

public class HelloService extends AppService {
    private final AppLogger log = new AppLogger("HelloService");

    public HelloService() {
        super("Hello Service", "Example", "1.0.0", "Simple hello world service");
    }

    @Override
    public void start() {
        super.start();
        log.info("Hello from BlueFlex Framework!");
    }
}