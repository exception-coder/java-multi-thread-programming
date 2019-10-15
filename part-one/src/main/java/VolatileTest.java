
public class VolatileTest {

    static class MyThread extends Thread{
        private volatile boolean isRunning = true;

        private void setRunning(boolean isRunning){
            this.isRunning = isRunning;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("任务开始执行");
            while (isRunning){
            }
            System.out.println("任务执行结束");
        }

        public static void main(String[] args) throws InterruptedException {
            MyThread myThread = new MyThread();
            myThread.start();
            myThread.sleep(1000);
            myThread.setRunning(false);
            System.out.println("赋值 isRunning 为false 完成");
        }
    }


}
