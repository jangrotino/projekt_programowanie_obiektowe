package oop.model;

import oop.model.RectangularMap;
import oop.model.util.UpdateType;

public interface StatisticsListener {
    void statisticChanged(RectangularMap worldMap, String message);
}

