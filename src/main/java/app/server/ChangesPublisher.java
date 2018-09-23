package app.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Flow;

public class ChangesPublisher implements Flow.Publisher<String> {

    private List<String> changes;

    public ChangesPublisher() {
        this.changes = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void subscribe(Flow.Subscriber<? super String> subscriber) {

    }
}
