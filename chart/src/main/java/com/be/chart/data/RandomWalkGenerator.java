package com.be.chart.data;

import java.util.Random;

public class RandomWalkGenerator {
    private final Random random;
    private double last;
    private int index;
    private double bias = 0.01;

    public RandomWalkGenerator(){
        random = new Random();
    }

    public RandomWalkGenerator(int seed) {
        random = new Random(seed);
    }

    public void reset() {
        index = 0;
        last = 0;
    }

    public RandomWalkGenerator setBias(double bias){
        this.bias = bias;
        return this;
    }

    public DoubleSeries getRandomWalkSeries(int count) {
        final DoubleSeries result = new DoubleSeries(count);

        // Generate a slightly positive biased random walk
        // y[i] = y[i-1] + random,
        // where random is in the range -0.5, +0.5
        for(int i = 0; i < count; i++) {
            double next = last + (random.nextDouble() - 0.5 + bias);
            last = next;
            result.add((double)index++, next);
        }

        return result;
    }
}
