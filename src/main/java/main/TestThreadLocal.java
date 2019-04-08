package main;

public class TestThreadLocal  {

    private Integer id;
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>();

    public TestThreadLocal(int init, String name) {

        id = threadLocal.get();
        if(id == null) {
            threadLocal.set(init);
        }
        id = threadLocal.get();
        System.out.println( "thread name " + name + " after set id = " + id);
        threadLocal.set(id+1);
    }

    public Integer getId() {
        System.out.println("getId " + id);
        return id;
    }
}
