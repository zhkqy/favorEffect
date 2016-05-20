package com.example.guxiuzhong.favoreffect.surfaceview;

import com.example.guxiuzhong.favoreffect.R;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by chenlei on 2016/5/20.
 */
public class FavorLayoutSurfaceviewUtils {

    private Random random = new Random();
    private String name = "";
    private int value = 0;

    private TreeMap<String, Integer> randomHeartMap = new TreeMap<>();
    private TreeMap<String, Integer> selfRandomHeartMap = new TreeMap<>();
    public FavorLayoutSurfaceviewUtils(){
        selfRandomHeartMap.clear();
        randomHeartMap.clear();

        switch (random.nextInt(3)) {
            case 0:
                selfRandomHeartMap.put("purple", R.mipmap.xin_01);
                break;
            case 1:
                selfRandomHeartMap.put("yellow", R.mipmap.xin_02);
                break;
            case 2:
                selfRandomHeartMap.put("green", R.mipmap.xin_03);
                break;
        }
    }


    public void randomHeart() {

        synchronized (this) {
            randomHeartMap.clear();
            int num = random.nextInt(3);

            switch (num) {
                case 0:
                    randomHeartMap.put("purple", R.mipmap.xin_01);
                    break;
                case 1:
                    randomHeartMap.put("yellow", R.mipmap.xin_02);
                    break;
                case 2:
                    randomHeartMap.put("green", R.mipmap.xin_03);
                    break;
            }

            Set entries = randomHeartMap.entrySet();

            if (entries != null) {
                Iterator iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterator.next();
                    name = entry.getKey();
                    value = entry.getValue();
                }
            }
        }
    }

    public void randomSelfHeart() {
        synchronized (this) {
            int num = random.nextInt(3);
            Set entries = selfRandomHeartMap.entrySet();
            if (entries != null) {
                Iterator iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) iterator.next();
                    name = entry.getKey();
                    value = entry.getValue();
                }
            }
        }
    }


    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

}
