package com.example.guxiuzhong.favoreffect.surfaceview;

import java.util.Random;

/**
 * Created by chenlei on 2016/5/20.
 */
public class FavorLayoutSurfaceviewUtils {

    private Random random = new Random();
    private String name = "";

    private String selfName = "";

    public  FavorLayoutSurfaceviewUtils(){

        switch (random.nextInt(3)) {
            case 0:
                selfName = "purple";
                break;
            case 1:
                selfName = "yellow";
                break;
            case 2:
                selfName = "green";
                break;
        }
    }


    public void randomHeart() {

        synchronized (this) {
            int num = random.nextInt(3);
            switch (num) {
                case 0:
                    name = "purple";
                    break;
                case 1:
                    name = "yellow";
                    break;
                case 2:
                    name = "green";
                    break;
            }
        }
    }

    public void randomSelfHeart() {
        this.name = selfName;
    }

    public String getName() {
        return name;
    }

}
