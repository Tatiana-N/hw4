package org.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Log4j2
public class Race {

    @Getter
    private long distance;

    private List<F1Cars> participantCars = new java.util.ArrayList<>();
    private Map<F1Cars, Long> carsStartTime = new ConcurrentHashMap<>();
    private List<Team> teams = new java.util.ArrayList<>();
    CountDownLatch finish;

    public Race(long distance, Team[] participantCars) {
        this.distance = distance;
        teams.addAll(List.of(participantCars));
    }

    /**
     * Запускаем гонку
     */
    public void start() {
        for (Team team : teams) {
            team.prepareRace(this);
        }
        finish = new CountDownLatch(participantCars.size());
        //TODO даем команду на старт гонки
        synchronized (this){
            notifyAll();
        }
        //TODO блокируем поток до завершения гонки
        try {
            finish.await();
        } catch (InterruptedException e) {
            log.info("кто-то так и не доехал");
        }
    }


    //Регистрируем участников гонки
    public void register(F1Cars participantCar) {
        log.info("Регистрация");
        participantCars.add(participantCar);
    }


    public void start(F1Cars f1Cars) {
        carsStartTime.put(f1Cars, System.currentTimeMillis());
        //фиксация времени старта
    }

    public long finish(F1Cars participant) {
        log.info("{} финишировал", participant.getName());
        finish.countDown();
        //фиксация времени финиша
        return System.currentTimeMillis() - carsStartTime.get(participant); //длительность гонки у данного участника
    }

    public void printResults() {
        participantCars.sort(F1Cars::compareTo);
        log.info("Результат гонки:");
        int position = 0;
        for (F1Cars participant : participantCars) {
            log.info("Позиция: {} время:{} {}", position++, participant.getTime(), participant.getName());
        }
    }
}
