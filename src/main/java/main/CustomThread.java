package main;

import java.util.ArrayList;
import java.util.List;

public class CustomThread extends Thread {

    int initId;
    String threadName;

    List<TestThreadLocal> testThreadLocalList;

    public CustomThread(int initId, String threadName) {
        this.initId = initId;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        super.run();
        testThreadLocalList = new ArrayList<TestThreadLocal>();
        TestThreadLocal testThreadLocal = new TestThreadLocal(initId,threadName);
        TestThreadLocal testThreadLocal1 = new TestThreadLocal(initId,threadName);
        TestThreadLocal testThreadLocal2 = new TestThreadLocal(initId,threadName);
        TestThreadLocal testThreadLocal3 = new TestThreadLocal(initId,threadName);

        testThreadLocalList.add(testThreadLocal);
        testThreadLocalList.add(testThreadLocal1);
        testThreadLocalList.add(testThreadLocal2);
        testThreadLocalList.add(testThreadLocal3);

        testThreadLocalList.forEach(x -> {
            System.out.println(threadName + " " + x.getId());
        });
    }
}
