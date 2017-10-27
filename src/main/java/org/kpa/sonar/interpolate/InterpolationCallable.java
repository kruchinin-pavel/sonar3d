package org.kpa.sonar.interpolate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.math3.analysis.interpolation.InterpolatingMicrosphere;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.kpa.game.Point3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public class InterpolationCallable implements Callable<List<Point3d>>, JsonWork {
    private static final Logger logger = LoggerFactory.getLogger(InterpolationCallable.class);
    private List<double[]> tasks = new ArrayList<>();
    private final List<Point3d> result = new ArrayList<>();
    private String inputDataFileName;
    private String outputDataFileName;

    private Data inputData;
    private Data outputData;
    private ExecutorService executorService;

    public InterpolationCallable() {
    }

    public InterpolationCallable(List<Double> zVals, List<Double> xVals, Data data, Data outputData) {
        this.inputData = data;
        this.outputData = outputData;
        for (double zVal : zVals) {
            for (double xVal : xVals) {
                tasks.add(new double[]{xVal, zVal});
            }
        }
    }


    @Override
    public List<Point3d> call() throws Exception {
        run(getOutput()::put);
        return result;
    }

    private ThreadLocal<InterpolatingMicrosphere> sphere = ThreadLocal
            .withInitial(() -> new InterpolatingMicrosphere(2, 50, 1, 0, -0,
                    new UnitSphereRandomVectorGenerator(2)));

    public void run(Consumer<Point3d> consumer) {
        AtomicInteger taskCounter = new AtomicInteger();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int index = result.size(); index < tasks.size(); index++) {
            double[] p = tasks.get(index);
            taskCounter.incrementAndGet();
            executorService.submit(() -> {
                double res = sphere.get().value(p, getInput().getPts(), getInput().getVals(), 1., 1.);
                consumer.accept(new Point3d(p[0], res, p[1]));
                logger.info("Left tasks: {}", taskCounter.decrementAndGet());
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @JsonIgnore
    public Data getInput() {
        if (inputData == null) {
            inputData = Data.getFromFile(inputDataFileName);
        }
        return inputData;
    }

    @JsonIgnore
    public Data getOutput() {
        if (outputData == null) {
            outputData = Data.getFromFile(outputDataFileName);
        }
        return outputData;
    }

    public static InterpolationCallable create(List<Double> zVals, List<Double> xVals, Data data, Data outputData) {
        return new InterpolationCallable(zVals, xVals, data, outputData);
    }

    public List<double[]> getTasks() {
        return tasks;
    }

    public void setTasks(List<double[]> tasks) {
        this.tasks = tasks;
    }

    public String getInputDataFileName() {
        if (inputDataFileName == null) {
            return inputData.getFileName();
        }
        return inputDataFileName;
    }

    public void setInputDataFileName(String inputDataFileName) {
        this.inputDataFileName = inputDataFileName;
    }

    public String getOutputDataFileName() {
        if (outputDataFileName == null) {
            return outputData.getFileName();
        }
        return outputDataFileName;
    }

    public void setOutputDataFileName(String outputDataFileName) {
        this.outputDataFileName = outputDataFileName;
    }
}
