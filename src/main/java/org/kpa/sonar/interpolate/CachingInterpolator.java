package org.kpa.sonar.interpolate;

import com.google.common.base.Preconditions;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.impl.DefaultCamelContext;
import org.kpa.util.io.JsonDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class CachingInterpolator implements AutoCloseable {
    private DefaultCamelContext ctx;
    private final Path inputDirectory;
    private ProducerTemplate producer;
    public static final String REPLY = "direct:mine";
    private AtomicBoolean started = new AtomicBoolean();
    private static final Logger logger = LoggerFactory.getLogger(CachingInterpolator.class);
    private ExecutorService service;


    public CachingInterpolator(Path inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public void start() throws Exception {
        Preconditions.checkArgument(started.compareAndSet(false, true), "Already started");
        ctx = new DefaultCamelContext();
        ctx.addService(this);
        ctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file://" + inputDirectory + "/?" +
                        //"include=*&" +
                        "delete=true&" +
                        "charset=" + Charset.defaultCharset()).process(CachingInterpolator.this::requestCome);
            }
        });
        service = Executors.newFixedThreadPool(1);
//        service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        ctx.start();
    }

    private void requestCome(Exchange inExch) throws IOException {
        final String requestFileName = inExch.getIn().getBody(GenericFile.class).getAbsoluteFilePath();
        logger.info("Task come come: {}", requestFileName);
        Object msg = JsonDto.read(new FileReader(requestFileName), JsonWork.class);
        if (msg instanceof InterpolationCallable) {
//            service.submit(() -> {
            try {
                InterpolationCallable task = (InterpolationCallable) msg;
                logger.info("Interpolating {}", requestFileName);
                task.call();
                if (task.getOutput().isChanged()) {
                    logger.info("Storing {}", task.getOutputDataFileName());
                    JsonDto.write(task.getOutputDataFileName(), task.getOutput());
                }
            } catch (Exception e) {
                logger.error("Error processing file {}: {}", requestFileName, e);
                throw new RuntimeException(e);
            }
//            });
        } else {
            logger.warn("Ignored work: {}", requestFileName);
        }
    }

    @Override
    public void close() throws Exception {
        if (ctx != null) ctx.stop();
        if (service != null) service.shutdownNow();
    }
}
