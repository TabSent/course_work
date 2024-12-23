// src/service/DirectoryObserver.java
package service;

import java.util.ArrayList;
import java.util.List;

public class DirectoryObserver {
    private final List<Runnable> listeners = new ArrayList<>();

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }

    public void notifyListeners() {
        listeners.forEach(Runnable::run);
    }
}
