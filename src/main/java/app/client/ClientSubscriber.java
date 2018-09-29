package app.client;

import java.io.Serializable;
import java.util.concurrent.Flow;

public class ClientSubscriber implements Flow.Subscriber<String>, Serializable {

    private Flow.Subscription subscription;

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        System.out.println("SUP");
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(String item) {
        System.out.println(item);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Eror");

    }

    @Override
    public void onComplete() {
        System.out.println("don");
    }
}
