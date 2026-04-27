java
package com.packt.cookbook.ch05_streams;

import com.packt.cookbook.ch05_streams.api.SpeedModel;
import com.packt.cookbook.ch05_streams.api.TrafficUnit;
import com.packt.cookbook.ch05_streams.api.Vehicle;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TrafficDensity3Test {

    @Test
    public void testTrafficByLane() {
        TrafficDensity3 trafficDensity3 = new TrafficDensity3();
        Stream<TrafficUnit> stream = Stream.generate(() -> new TrafficUnit(new Vehicle(), 0.5));
        int trafficUnitsNumber = 10;
        double timeSec = 1.0;
        SpeedModel speedModel = (vehicle, traction, time) -> 50.0;
        double[] speedLimitByLane = {40.0, 60.0, 80.0};

        Integer[] result = trafficDensity3.trafficByLane(stream, trafficUnitsNumber, timeSec, speedModel, speedLimitByLane);

        assertEquals(3, result.length);
    }

    @Test
    public void testTrafficByLaneEmptyStream() {
        TrafficDensity3 trafficDensity3 = new TrafficDensity3();
        Stream<TrafficUnit> stream = Stream.empty();
        int trafficUnitsNumber = 10;
        double timeSec = 1.0;
        SpeedModel speedModel = (vehicle, traction, time) -> 50.0;
        double[] speedLimitByLane = {40.0, 60.0, 80.0};

        Integer[] result = trafficDensity3.trafficByLane(stream, trafficUnitsNumber, timeSec, speedModel, speedLimitByLane);

        assertArrayEquals(new Integer[]{0, 0, 0}, result);
    }

    @Test
    public void testTrafficByLaneNoTrafficUnits() {
        TrafficDensity3 trafficDensity3 = new TrafficDensity3();
        Stream<TrafficUnit> stream = Stream.generate(() -> new TrafficUnit(new Vehicle(), 0.5));
        int trafficUnitsNumber = 0;
        double timeSec = 1.0;
        SpeedModel speedModel = (vehicle, traction, time) -> 50.0;
        double[] speedLimitByLane = {40.0, 60.0, 80.0};

        Integer[] result = trafficDensity3.trafficByLane(stream, trafficUnitsNumber, timeSec, speedModel, speedLimitByLane);

        assertArrayEquals(new Integer[]{0, 0, 0}, result);
    }

    @Test
    public void testTrafficByLaneSingleLane() {
        TrafficDensity3 trafficDensity3 = new TrafficDensity3();
        Stream<TrafficUnit> stream = Stream.generate(() -> new TrafficUnit(new Vehicle(), 0.5));
        int trafficUnitsNumber = 10;
        double timeSec = 1.0;
        SpeedModel speedModel = (vehicle, traction, time) -> 50.0;
        double[] speedLimitByLane = {40.0};

        Integer[] result = trafficDensity3.trafficByLane(stream, trafficUnitsNumber, timeSec, speedModel, speedLimitByLane);

        assertArrayEquals(new Integer[]{10}, result);
    }

    @Test
    public void testTrafficByLaneDifferentSpeeds() {
        TrafficDensity3 trafficDensity3 = new TrafficDensity3();
        Random random = new Random();
        Stream<TrafficUnit> stream = Stream.generate(() -> new TrafficUnit(new Vehicle(), random.nextDouble()));
        int trafficUnitsNumber = 100;
        double timeSec = 1.0;
        SpeedModel speedModel = (vehicle, traction, time) -> 30.0 + (traction * 50.0);
        double[] speedLimitByLane = {40.0, 60.0};

        Integer[] result = trafficDensity3.trafficByLane(stream, trafficUnitsNumber, timeSec, speedModel, speedLimitByLane);

        assertEquals(2, result.length);
    }

    @Test
    public void testTrafficByLaneLargeNumberOfLanes() {
        TrafficDensity3 trafficDensity3 = new TrafficDensity3();
        Stream<TrafficUnit> stream = Stream.generate(() -> new TrafficUnit(new Vehicle(), 0.5));
        int trafficUnitsNumber = 10;
        double timeSec = 1.0;
        SpeedModel speedModel = (vehicle, traction, time) -> 50.0;
        double[] speedLimitByLane = new double[5];
        Arrays.fill(speedLimitByLane, 50.0);

        Integer[] result = trafficDensity3.trafficByLane(stream, trafficUnitsNumber, timeSec, speedModel, speedLimitByLane);

        assertEquals(5, result.length);
    }
}