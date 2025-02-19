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
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import oz.stream.config.AppConfiguration;
import oz.stream.model.DocValuesList;
import oz.stream.model.MessageDto;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Oleg Zhurakousky, @rucko24
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class SendMessageService {

    public static final String PERFORMANCE_QUEUE = "performance-queue";
    private final StreamBridge streamBridge;
    private final TaskExecutor threadPoolTaskExecutor;
    private final AppConfiguration appConfiguration;
    private final ReadFileService readFileService;

    private static final AtomicLong COUNTER = new AtomicLong();
    private final ResponseTimeService responseTimeService = new ResponseTimeService();

    // Utilizamos un AtomicLong para trackear el último tiempo de envío global
    private final AtomicLong lastGlobalSendTime = new AtomicLong(0);

    @Transactional
    public void producer(String input) {
        final List<DocValuesList> docValueList = this.readFileService.getConfigurationMessage().getDocValuesListList();
        final var message = this.readFileService.getMessage();

        long totalDocuments = docValueList.stream()
                .mapToLong(DocValuesList::getDocCount)
                .sum();

        int cadaDocCountAMinutos = 60 * docValueList.size();
        int targetGlobalRate = (int) Math.ceil((double) totalDocuments / cadaDocCountAMinutos);

        long globalDelayPerMessage = Math.round(1000.0 / targetGlobalRate * 1_000_000); // en nanosegundos

        log.info("Configuración: Target Rate Global: {} msg/s, Delay entre mensajes: {} ns", targetGlobalRate, globalDelayPerMessage);

        // Inicializamos el tiempo de inicio
        lastGlobalSendTime.set(System.nanoTime());

        final CountDownLatch countDownLatch = new CountDownLatch(docValueList.size());
        docValueList.forEach(item -> {
            threadPoolTaskExecutor.execute(() -> {
                final long totalDocCountToProcess = item.getDocCount() / appConfiguration.getReplicasOrInstances();
                if (totalDocCountToProcess > 0) {
                    this.sendMessage(globalDelayPerMessage, totalDocCountToProcess, message);
                }
                countDownLatch.countDown();
            });
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Envío completado. Tiempo total: {} Total docCount: {}", responseTimeService.formatResponseTime(), COUNTER.get());
    }

    private void sendMessage(final long globalDelay, final long totalDocCountToProcess, String message) {
        log.info("Iniciando envío para docCount {} con delay global {} ns", totalDocCountToProcess, globalDelay);
        MessageDto messageDto = new MessageDto();
        messageDto.setMessage(message);

        for (int index = 0; index < totalDocCountToProcess; index++) {
            // Intentamos obtener el siguiente slot de tiempo disponible
            long currentSlot;
            long nextSlot;

            //CAS
            do {
                currentSlot = lastGlobalSendTime.get();
                nextSlot = currentSlot + globalDelay;
            } while (!lastGlobalSendTime.compareAndSet(currentSlot, nextSlot));

            // Esperamos hasta que sea nuestro turno
            while (System.nanoTime() < nextSlot) {
                Thread.onSpinWait();
            }

            Message<MessageDto> messageToSend = MessageBuilder.withPayload(messageDto)
                    .setHeader("timestamp_ms", System.currentTimeMillis())
                    .build();

            this.streamBridge.send(PERFORMANCE_QUEUE, messageToSend);
            COUNTER.incrementAndGet();
        }
    }
}
