package protocol.utils;

import GUI.config.AppConfig;

import java.util.List;
import java.util.Random;

public class RandomizeUtils {

  private static RandomizeUtils randomizeUtils = new RandomizeUtils();
  private int seed;
  private Random rand;

  private RandomizeUtils() {
    seed = AppConfig.INITIAL_RANDOM_SEED;
  }

  public static RandomizeUtils getInstance( ) {
    return randomizeUtils;
  }

  public void initRandom() {
    rand = new Random(seed);
  }

  public void changeSeed() {
    ++seed;
  }

  public int getRandomInteger(int max) {

    return rand.nextInt(max);
  }

  public <T> T getRandomElementFromList(List<T> list) {

    int r = rand.nextInt(list.size()-1);
    return list.get(r);
  }

  public String getRandomUID() {

    return String.valueOf(rand.nextLong());
  }
}