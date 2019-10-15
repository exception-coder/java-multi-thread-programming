import org.junit.Test;

public class DemoTest {

    class MyThread extends Thread {

        MyThread() {
//            System.out.println("init--begin");
//            System.out.println("Thread.currentThread().getName(): " + Thread.currentThread().getName());
//            System.out.println("this.getName(): " + this.getName());
//            System.out.println("init--end");
        }

        @Override
        public void run() {
            super.run();
            try {
                for (int i = 0; i < 500000; i++) {
                    if (this.isInterrupted()) {
                        System.out.println(Thread.currentThread().getName() + " 已经是停止状态了！我要退出了！");
                        throw new InterruptedException("线程中断状态");
                    } else {
                        if (i % 100 == 0) {
                            System.out.println(i);
                        }
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("如果不抛出异常,那么线程并未停止,因为我并不在线程中断状态判断代码块中");

//            System.out.println(this.getName());
//            System.out.println("Thread.currentThread().getName(): " + Thread.currentThread().getName());
//            System.out.println("this.getName(): " + this.getName());
        }
    }


    @Test
    public void test1() {
        printThreadName();
        MyThread myThread = new MyThread();
        myThread.start();
        System.out.println("执行结束");
    }

    @Test
    public void test2() {
        try {
            for (int i = 0; i < 5; i++) {
                new Thread(() -> {
                    try {
                        printThreadName();
                        Thread.sleep(500 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            Thread.sleep(5 * 500 * 5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 线程随机性展现
     */
    @Test
    public void test3() {

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                printThreadName(i);
            }
        });
        thread1.start();

        for (int i = 0; i < 10000; i++) {
            printThreadName(i);
        }

    }


    /**
     * 线程数据独立及共享展示
     */
    @Test
    public void test4() {
        Thread thread1 = new Thread("A") {
            private int count = 5;

            @Override
            public void run() {
                super.run();
                while (count > 0) {
                    count--;
                    printThreadName(count);
                }
            }
        };

        Thread thread2 = new Thread("B") {
            private int count = 5;

            @Override
            public void run() {
                super.run();
                while (count > 0) {
                    count--;
                    printThreadName(count);
                }
            }

        };
        Thread thread3 = new Thread("C") {
            private int count = 5;

            @Override
            public void run() {
                super.run();
                while (count > 0) {
                    count--;
                    printThreadName(count);
                }
            }

        };

        thread1.start();
        thread2.start();
        thread3.start();

        try {

            Runnable runnable = new Runnable() {
                private int count = 5;

                @Override
                synchronized public void run() {
                    count--;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    printThreadName(count);
                }
            };
            new Thread(runnable, "A1") {
            }.start();

            new Thread(runnable, "B1") {
            }.start();

            new Thread(runnable, "C1") {
            }.start();

            new Thread(runnable, "D1") {
            }.start();

            new Thread(runnable, "E1") {
            }.start();


            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void interruptedTest() throws InterruptedException {
        MyThread myThread = new MyThread();
        myThread.start();
        Thread.sleep(1000);
        myThread.interrupt();

        System.out.println("myThread 线程是否停止: " + myThread.isInterrupted());


/**
 *
 * interrupted，Thread静态方法，测试当前线程是否已经中断，并且线程中断状态由该方法清除
 *  测试当前线程是否中断，此时当前线程为主线程：main
 */

        System.out.println("线程是否停止1: " + myThread.interrupted());
        System.out.println("线程是否停止2: " + Thread.interrupted());
        System.out.println("线程是否停止3: " + Thread.interrupted());

        Thread.currentThread().interrupt();
        System.out.println("线程是否停止4: " + myThread.interrupted());
        System.out.println("线程是否停止5: " + Thread.interrupted());
        System.out.println("线程是否停止6: " + Thread.interrupted());

    }


    /**
     * interrupt() 和 sleep() 方法不可同时使用
     */
    @Test
    public void sleepAndInterruptTest() {

        Thread myThread1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        myThread1.start();
        myThread1.interrupt();

        Thread myThread2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        myThread2.interrupt();
        myThread2.start();


    }


    /**
     * stop() 方法与 java.lang.ThreadDeath 异常
     */
    @Test
    public void stopTest() {
        Thread myThread = new Thread(() -> {
            try {
                Thread.currentThread().stop();
            } catch (ThreadDeath e) {
                e.printStackTrace();
            }
        });
        myThread.start();
    }

    @Test
    public void suspendAndResumeTest() throws InterruptedException {
        Thread thread = new Thread() {

            @Override
            public void run() {
                super.run();
                synchronized (DemoTest.class) {
                    long start = System.currentTimeMillis();
                    long end = start;
                    while (end - start < 3000) {
                        end = System.currentTimeMillis();
                    }

                }

            }


        };
        System.out.println(thread.getName() + "线程启动");
        thread.start();
        Thread.sleep(1000);
        thread.suspend();
        Thread.sleep(1000);
        System.out.println(thread.getName() + "线程暂停");
//        thread.resume();
        Thread.sleep(1000);
        synchronized (DemoTest.class) {
            System.out.println(thread.getName() + "线程恢复");
            thread.resume();
        }

    }


    /**
     * 守护线程测试
     */
    @Test
    public void daemonThreadTest() throws InterruptedException{

        Thread thread = new Thread() {

            private int i = 0;

            @Override
            public void run() {
                super.run();
                try {
                    while (true) {
                        i++;
                        Thread.sleep(1000);
                        System.out.println(i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        thread.setDaemon(true);
        thread.start();
        Thread.sleep(6000);
        System.out.println("执行完成,thread对象不再打印，守护线程停止");

    }


    public static void main(String[] args) {
        Thread myThread = new Thread(() -> {
            try {
                Thread.currentThread().stop();
            } catch (ThreadDeath e) {
                e.printStackTrace();
            }
        });
        myThread.start();

    }

    private void printThreadName(Object object) {
        System.out.println("线程名称: " + Thread.currentThread().getName() + " 数据: " + object);
    }

    private void printThreadName() {
        System.out.println("线程名称: " + Thread.currentThread().getName());
    }
}
