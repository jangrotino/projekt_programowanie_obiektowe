package oop.model;

public record SimulationParameters(int height,
                                   int width,
                                   int startPlantNumber,
                                   int plantsPerDay,
                                   int singlePlantEnergy,
                                   int startAnimalNumber,
                                   int startAnimalEnergy,
                                   int copulatingCost,
                                   int livingCost,
                                   int wellFedValue,
                                   int minMutationNum,
                                   int maxMutationNum,
                                   int genomLength) {}