package org.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.Random;

/**
 * Поток болида
 */
@Log4j2
public class F1Cars extends Thread implements Comparable<F1Cars> {

    /**
     * Идентификатор болида
     */
    private final long carId;

    /**
     * Ссылка на питстоп команды
     */
    private final PitStop pitStop;

    /**
     * Ссылка на гонку
     */
    private Race race;

    /**
     * Массив колес
     */
    private Wheel wheels[] = new Wheel[]{new Wheel(), new Wheel(), new Wheel(), new Wheel()};

    /**
     * Счетчик пройденной дистанции
     */
    private long currentDistance = 0;

    /**
     * Дистанция, которую необходимо пройти для заверешения гонки
     */
    private long targetDistance = 0;

    /**
     * ГПСЧ
     */
    private Random random;

    /**
     * Время гонки, заполняется на финише
     */
    @Getter
    private long time = 0;

    public F1Cars(long carId, PitStop pitStop) {
        super("F1Car[" + carId + "]");
        this.carId = carId;
        this.pitStop = pitStop;
        random = new Random();

    }

    /**
     * Подготовка к гонке
     *
     * @param race
     */
    public void prepareRace(Race race) {
        this.race = race;
        this.targetDistance = race.getDistance();
        this.start();
    }

    /**
     * Логика потока болида:
     * ждем старта гонки
     * выполняем цикл гонки, пока не достигнем цели
     * финишируем и заверщаем работу
     */
    @Override
    public void run() {
        synchronized (race){
            try {
                race.wait();
            } catch (InterruptedException e) {
                log.warn("гонка не состоялась");
                return;
            }
        }
        log.info(this.getName() + " Стартовал");
        race.start(this);
        while (currentDistance < targetDistance) {
            try {
                moveToTarget();
            } catch (InterruptedException e) {
                log.warn("выбыл из гонки");
                break;
            }
        }
        this.time = race.finish(this);

    }

    /**
     * Цикл гонки
     * 1) Проверяем необходимость заезда на питстоп
     * 2) Перемещаемся 1000 миллисекунд с случайной скоростью
     */
    private void moveToTarget() throws InterruptedException {
        if (isNeedPit()) {
            pitStop.pitline(this);
        }
        long speed = getNextSpeed();

        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (Wheel wheel : wheels) {
            wheel.travel(speed);
        }
        currentDistance += speed;
    }

    //Требуется замена если хотя бы 1 шина с остатоком меньше 25%
    private boolean isNeedPit() {
        for (Wheel wheel : wheels) {
            if (wheel.getStatus() < 25) {
                return true;
            }
        }
        return false;
    }


    /**
     * Для сортировки результатов
     *
     * @param o the object to be compared.
     * @return
     */
    @Override
    public int compareTo(F1Cars o) {
        return Long.compare(this.time, o.getTime());
    }

    /**
     * Получаем случайное значение от 50 до 150
     *
     * @return
     */
    private long getNextSpeed() {
        return 150 - random.nextInt(101);
    }

    /**
     * Передача ссылки на колесо по номеру позиции
     *
     * @param position
     * @return
     */
    public Wheel getWheel(int position) {
        return wheels[position];
    }

    @Override
    public String toString() {
        return "F1Cars{" +
            "carId=" + carId +
            ", wheels=" + Arrays.toString(wheels) +
            ", currentDistance=" + currentDistance +
            ", targetDistance=" + targetDistance +
            '}';
    }
}
