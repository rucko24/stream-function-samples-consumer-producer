package oz.stream.service;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

//@ExtendWith(MockitoExtension.class)
@Log4j2
class SendMessageServiceTest {

    // Instancia bajo prueba con mocks inyectados
//    @InjectMocks
//    private SendMessageService sendMessageService;
//
//    // Mocks de las dependencias
//    @Mock
//    private StreamBridge streamBridge;
//
//    @Mock
//    private ReadFileService readFileService;
//
//    @Mock
//    private AppConfiguration appConfiguration;
//
//    // ExecutorService real para simular concurrencia
//    private ExecutorService executorService;

    //@Test
    //@DisplayName("Metodo test producer")
    public void testProducer() throws InterruptedException {
//        executorService = Executors.newFixedThreadPool(5);
//
//        // Configuramos los mocks
//        when(appConfiguration.getReplicasOrInstances()).thenReturn(1);
//        when(appConfiguration.getCorePoolSize()).thenReturn(2);
//
//        final DocValuesList docValuesList = new DocValuesList();
//        docValuesList.setDato("1");
//        docValuesList.setDocCount(1000L);
//
//        final DocValuesList docValuesListDos = new DocValuesList();
//        docValuesList.setDato("2");
//        docValuesList.setDocCount(1000L);
//
//        final Valores valores = new Valores();
//        valores.setDocValuesListList(List.of(
//                docValuesList, // 1000 mensajes
//                docValuesListDos  // 1000 mensajes
//        ));
//
//        when(readFileService.getConfigurationMessage()).thenReturn(valores);
//        when(readFileService.getMessage()).thenReturn("Mensaje de prueba");
//
//        // Ejecutamos el método bajo prueba
//        sendMessageService.producer("input");
//
//        // Esperamos a que las tareas concurrentes terminen
//        Thread.sleep(2000); // Tiempo suficiente para que completen (ajustable)
//
//        // Verificamos que se enviaron 2000 mensajes
//        verify(streamBridge, times(2000)).send(eq(SendMessageService.PERFORMANCE_QUEUE), any());
    }

    public Scheduler scheduler() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        final ThreadFactory threadFactory = runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setName("RabbitProducerMessage-" + atomicInteger.incrementAndGet());
            return thread;
        };
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(3, threadFactory));
    }

    @Test
    @SneakyThrows
    @DisplayName("Intento de envio de mensajes de manera reactiva")
    void reactiveMessageSender() {

        final var counter = new AtomicInteger(0);
        final var countDownLatch = new CountDownLatch(3);

        Flux.range(0, 3)
                //.subscribeOn(this.scheduler())
                .flatMap(mapper -> Flux.range(0, 10)
                        .delayElements(Duration.ofMillis(33))
                        .publishOn(this.scheduler())
                        .doOnNext(onNext -> {
                            counter.incrementAndGet();
                            log.info("Send message {}", "ABC");
                            //return Mono.empty();
                        })
                        .doOnTerminate(countDownLatch::countDown)
                )
                .subscribe();

        countDownLatch.await();

        log.info("Total message: {}", counter.get());

    }


}