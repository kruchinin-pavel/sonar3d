package org.kpa.sonar.map;

import org.junit.Test;

public class InterpolatorTest {
    @Test
    public void interpolatorTest() {
        Interpolator interpolator = new Interpolator();
        interpolator.read(10,"/org/kpa/sonar/Tracks.xml");

    }

}