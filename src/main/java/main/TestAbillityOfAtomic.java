package main;

import java.util.concurrent.atomic.AtomicInteger;

public class TestAbillityOfAtomic {
    static AtomicInteger atomicInteger = new AtomicInteger(Integer.MAX_VALUE - 100);

    public static void main(String[] args) {
        for(long i = 0;i < 2147483648l;i++) {
            atomicInteger.getAndIncrement();
            System.out.println(atomicInteger.get());
        }
    }
}
