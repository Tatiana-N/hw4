package org.example;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import static java.lang.Thread.sleep;

@Log4j2
public class Wheel {

    @Getter
    private int status = 100;


    public void replaceWheel() {
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        status = 100;
    }

    public void travel(long speed) {
        status -= Math.floor((25 * speed / 130f) + Math.random() * 5f);
    }

    @Override
    public String toString() {
        return "Wheel{" +
            "status=" + status +
            '}';
    }
}
