package oop.model;

import oop.model.util.Genom;
import oop.model.util.UpdateType;

public interface StatisticsListener {
        void updateStatistics(UpdateType updateType, int value);
        void updateStatistics(UpdateType updateType, Genom genom);
}
