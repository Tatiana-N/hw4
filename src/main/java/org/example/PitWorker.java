package org.example;

import lombok.extern.log4j.Log4j2;

/**
 * Работник питстопа, меняет шину на прибывшей машине на своем месте
 */
@Log4j2
public class PitWorker extends Thread {

    //Место работника, он же номер колеса от 0 до 3
    private final int position;

    //Ссылка на сущность питстопа для связи
    private final PitStop pitStop;

    public PitWorker(int position, PitStop pitStop) {
        this.position = position;
        this.pitStop = pitStop;
    }

      @Override
      public void run() {
        while (!isInterrupted()) {
          F1Cars car = pitStop.getCar();
          if (car != null) {
            //TODO работник ждет машину на питстопе и меняет шину на своей позиции
            log.info("меняю колесо {} у машины {}", position, car.getName());
            car.getWheel(position).replaceWheel();
            //TODO работник сообщает о готовности
            pitStop.setWheal();
          }
        }
      }
    }

