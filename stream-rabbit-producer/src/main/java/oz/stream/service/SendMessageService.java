/*
 * Copyright 2021-2021 the original author or authors.
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
import java.util.concurrent.locks.StampedLock;

/**
 * @author Oleg Zhurakousky, rucko24
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

    //@Override
    @Transactional
    public void producer(String input) {

        final List<DocValuesList> docValueList = this.readFileService.getConfigurationMessage().getDocValuesListList();

        final var message = this.readFileService.getMessage();

        final CountDownLatch countDownLatch = new CountDownLatch(docValueList.size());

        docValueList.forEach(item -> {
            threadPoolTaskExecutor.execute(() -> {
                //Distribución de la carga (cantidad de mensajes):
                final long totalDocCountToProcess = (item.getDocCount() / appConfiguration.getReplicasOrInstances());

                //Control de la tasa de envío (delay entre mensajes):
                int applyToDocCount = appConfiguration.getCorePoolSize() * appConfiguration.getReplicasOrInstances();
                final long docCountForDelay = (item.getDocCount() / (applyToDocCount));

                if (docCountForDelay > 0) {
                    final long delay = 60_000 / (docCountForDelay);
                    this.sendMessage(input, delay, totalDocCountToProcess, message);
                }
                countDownLatch.countDown();
            });
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Envio completado Tiempo total: {} Total docCount: {}", responseTimeService.formatResponseTime(), COUNTER.get());

    }

    private void sendMessage(final String input, final long delay, final long docCount, String message) {

        log.info("Enviando mensaje con delay de {} ms para docCount {}", delay, docCount);

        MessageDto messageDto = new MessageDto();
        messageDto.setMessage(message);

        for (int index = 0; index < docCount; index++) {
            System.out.println("Uppercasing " + input + " " + Thread.currentThread().getName());

            Message<MessageDto> messageToSend = MessageBuilder.withPayload(messageDto)
                    .build();

            this.streamBridge.send(PERFORMANCE_QUEUE, messageToSend);

            if (input.equals("fail")) {
                System.out.println("throwing exception");
                throw new RuntimeException("Itentional");
            }

            COUNTER.incrementAndGet();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
