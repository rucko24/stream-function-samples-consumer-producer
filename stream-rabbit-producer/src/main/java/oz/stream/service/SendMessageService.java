/*
 * Copyright 2021-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oz.stream.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import oz.stream.config.AppConfiguration;
import oz.stream.model.DocValuesList;
import oz.stream.model.MessageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @author Oleg Zhurakousky, @rucko24
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class SendMessageService {

    public static final String PERFORMANCE_QUEUE = "performance-queue";
    private final StreamBridge streamBridge;
    private final Scheduler scheduler;
    private final AppConfiguration appConfiguration;
    private final ReadFileService readFileService;

    private static final AtomicLong COUNTER = new AtomicLong();
    private final ResponseTimeService responseTimeService = new ResponseTimeService();

    @Transactional
    public void producer(String input) {

        final List<DocValuesList> docValueList = this.readFileService.getConfigurationMessage().getDocValuesListList();
        final String message = this.readFileService.getMessage();

        final long totalMessages = getTotalMessages(docValueList);

        final long globalDelayPerMessage = this.globalDelayPerMessage(docValueList, totalMessages);

        // Número de threads deseado (puedes obtenerlo de una configuración)
        final int numThreads = appConfiguration.getCorePoolSize(); // Ejemplo: 5
        // Calcular mensajes por thread
        final long messagesPerThread = totalMessages / numThreads;
        final long remainder = totalMessages % numThreads; // Mensajes sobrantes

        final CountDownLatch countDownLatch = new CountDownLatch(numThreads);

        Flux.range(0, numThreads)
                .flatMap(index -> {
                    final long messagesForThisThread = messagesPerThread + (index < remainder ? 1 : 0);
                    return this.sendMessage(globalDelayPerMessage, messagesForThisThread, message, countDownLatch);
                })
                .subscribe();

        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        log.info("Envío completado. Tiempo total: {} Total docCount: {}", responseTimeService.formatResponseTime(), COUNTER.get());

    }

    private long getTotalMessages(List<DocValuesList> docValueList) {
        // Calcular el total de mensajes a enviar
        return docValueList.stream()
                .mapToLong(item -> item.getDocCount() / appConfiguration.getReplicasOrInstances())
                .sum();
    }

    private long globalDelayPerMessage(List<DocValuesList> docValueList, final long totalDocuments) {
        //cadaDocCountAMinutos => Total de minutos por cada doc_count
        final int totalDocCountEnMinutos = 60 * docValueList.size();
        final double targetGlobalRate = (double) totalDocuments / totalDocCountEnMinutos;
        final double targetRatePerThread = targetGlobalRate / this.appConfiguration.getCorePoolSize();
        final long globalDelayPerMsg = Math.round(1000.0 / targetRatePerThread); // en millis
        var formatTargetRatePerThread = BigDecimal.valueOf(targetRatePerThread).setScale(2, RoundingMode.HALF_EVEN);
        log.info("Configuración Target Rate Global: [{}] msg/s, Target Rate Per Thread: [{}] msg/s, Delay between msg: [{}] ns", targetGlobalRate, formatTargetRatePerThread, globalDelayPerMsg);
        return globalDelayPerMsg;
    }

    private Mono<Void> sendMessage(final long globalDelayPerMessage, final long totalDocCountToProcess,
                                      String messagePayload, final CountDownLatch countDownLatch) {
        log.info("Iniciando envío de {} mensajes con un delay de {} ms", totalDocCountToProcess, TimeUnit.NANOSECONDS.toMillis(globalDelayPerMessage));
        MessageDto messageDto = new MessageDto();
        messageDto.setMessage(messagePayload);

        return Flux.range(0, (int) totalDocCountToProcess)
                .delayElements(Duration.ofMillis(globalDelayPerMessage))
                .publishOn(this.scheduler)
                .doOnNext(this.getTimestampMs(messageDto))
                .then()
                .doOnTerminate(countDownLatch::countDown);

    }

    private Consumer<Integer> getTimestampMs(MessageDto messageDto) {
        return onNext -> {

            Message<MessageDto> messageToSend = MessageBuilder.withPayload(messageDto)
                    .setHeader("timestamp_ms", System.currentTimeMillis())
                    .build();

            this.streamBridge.send(PERFORMANCE_QUEUE, messageToSend);
            COUNTER.incrementAndGet();

        };
    }
}
