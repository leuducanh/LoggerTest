package main;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {



    public static void main(String[] args) {

//        CustomThread customThread = new CustomThread(1," Thread bat dau tu 1 ");
//        CustomThread customThread1 = new CustomThread(50," Thread bat dau tu 50 ");
//
//        customThread.start();
//        customThread1.start();

            final long MICROS_PER_DAY = 24 * 60 * 60 * 1000 * 1000;
            final long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
            System.out.println(MICROS_PER_DAY / MILLIS_PER_DAY);

    }
}
