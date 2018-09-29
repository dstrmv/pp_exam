package app.server;

import app.client.ClientSubscriber;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public class RemoteServerImpl implements RemoteServer {

    private SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
    private List<String> userNamesList = Collections.synchronizedList(new ArrayList<>());
    private List<Flow.Subscriber<String>> subscribers = Collections.synchronizedList(new ArrayList<>());
    private List<String> changes = Collections.synchronizedList(new ArrayList<>());
//
//    {
//
//        Thread publisherThread = new Thread(() -> {
//            while (true) {
//                try {
//                    publisher.submit(changes.remove(0));
//                } catch (IndexOutOfBoundsException e) {
//                    System.out.println("hello in init");
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException ex) {
//                        System.out.println("interrupted");
//                    }
//                }
//            }
//        });
//
////        publisherThread.setDaemon(true);
//        publisherThread.run();
//        System.out.println("INIT");
//
//    }


    @Override
    public String getRootPath() throws RemoteException {
        //return FileSystemView.getFileSystemView().getRoots()[0].getPath().toString();
        return File.listRoots()[0].getAbsolutePath();
    }

    @Override
    public void addUser(String userName) throws RemoteException {
        this.userNamesList.add(userName);
    }

    @Override
    public synchronized void addSubscriber(Flow.Subscriber<String> subscriber) throws RemoteException {
        this.subscribers.add(subscriber);
        publisher.subscribe(subscriber);
    }


    @Override
    public boolean containsUser(String userName) throws RemoteException {
        return this.userNamesList.contains(userName);
    }

    @Override
    public synchronized void addMessage(String s) throws RemoteException {
        this.changes.add(s);
    }
}
