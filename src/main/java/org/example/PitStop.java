package org.example;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;

@Log4j2
public class PitStop extends Thread {

  PitWorker[] workers = new PitWorker[4];
  Exchanger<F1Cars> exchanger = new Exchanger<>();
  CyclicBarrier cyclicBarrier = new CyclicBarrier(5, () -> {
    log.info("замена колес произведена");
  });

    public PitStop() {
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new PitWorker(i, this);
            workers[i].start();
        }
    }

  // TODO условие: на питстоп может заехать только 1 пилот
  public synchronized void pitline(F1Cars f1Cars) throws InterruptedException {
    log.info(f1Cars + " на пит-стопе");
 
    for (int i = 0; i < workers.length; i++) {
      // TODO каждую шину меняет отдельный PitWowker поток
      exchanger.exchange(f1Cars);
    }
    // TODO держим поток до момента смены всех шин

    try {
      cyclicBarrier.await();
    } catch (BrokenBarrierException e) {
      e.printStackTrace();
    }
    // TODO отпускаем машину
    log.info(f1Cars + " покинул пит-стоп");
    }


    @Override
    public void run() {
        while(!isInterrupted()){
            //синхронизируем поступающие болиды и работников питстопа при необходимости
        }
    }

  public F1Cars getCar() {
    //TODO Блокируем поток до момента поступления машины на питстоп и возвращаем ее
    try {
      return exchanger.exchange(null);
    } catch (InterruptedException e) {
      log.error("error");
      return null;
    }
  }

  public void setWheal() {
    try {
      // TODO дожидаемся когда все PitWorker завершат свою работу над машиной
      cyclicBarrier.await();
    } catch (InterruptedException | BrokenBarrierException e) {
      log.warn("замена колес пошла не по плану");
    }
  }
}
