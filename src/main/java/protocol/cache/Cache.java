package protocol.cache;

import java.util.*;

public abstract class Cache<S,T> {

  private long maxCapacity;
  private Map<S,T> cache;

  public Cache(
          long maxCapacity) {

    this.maxCapacity = maxCapacity;
    this.cache = new HashMap<>();
  }

  public boolean add(S id, T entry) {

    if(cache.size() < maxCapacity || !cache.containsKey(id)) {

      cache.put(id, entry);
      return true;
    }
    else return false;
  }

  public List<T> getAll() {

    List<T> entryList = new ArrayList<>();

    if(!cache.isEmpty()) {

      cache.entrySet().forEach(entry -> {
        entryList.add(entry.getValue());
      });
    }

    return entryList;
  }

  public boolean deleteAll() {

    cache.clear();

    return cache.isEmpty();
  }

  public boolean isExpired() {

    return this.cache.isEmpty();
  }
}