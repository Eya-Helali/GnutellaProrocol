package protocol;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestApplication {

  Thread t;

  public TestApplication() {

    initWorker();
  }

  private void initWorker() {

    Runnable worker = () -> {

      Socket soc = null;

      try {

        soc = new Socket("localhost", 10000);

      }catch (Exception e) {
        //
      }

      while(!Thread.currentThread().isInterrupted()) {

        try {

          soc.setKeepAlive(true);
          Thread.sleep(2000);
          System.out.println(soc);

        }catch(Exception e) {

          //
        }
      }
    };

    t = new Thread(worker);
    t.start();
  }

  public static void main(String[] args) {

    TestApplication test = new TestApplication();
  }
}