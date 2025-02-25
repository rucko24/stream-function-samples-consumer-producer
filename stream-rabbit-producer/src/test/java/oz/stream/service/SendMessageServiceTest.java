package oz.stream.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import oz.stream.config.AppConfiguration;
import oz.stream.model.DocValuesList;
import oz.stream.model.Valores;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//@ExtendWith(MockitoExtension.class)
class SendMessageServiceTest {

    // Instancia bajo prueba con mocks inyectados
    @InjectMocks
    private SendMessageService sendMessageService;

    // Mocks de las dependencias
    @Mock
    private StreamBridge streamBridge;

    @Mock
    private ReadFileService readFileService;

    @Mock
    private AppConfiguration appConfiguration;

    // ExecutorService real para simular concurrencia
    private ExecutorService executorService;

    //@Test
    //@DisplayName("Metodo test producer")
    public void testProducer() throws InterruptedException {
        executorService = Executors.newFixedThreadPool(5);

        // Configuramos los mocks
        when(appConfiguration.getReplicasOrInstances()).thenReturn(1);
        when(appConfiguration.getCorePoolSize()).thenReturn(2);

        final DocValuesList docValuesList = new DocValuesList();
        docValuesList.setDato("1");
        docValuesList.setDocCount(1000L);

        final DocValuesList docValuesListDos = new DocValuesList();
        docValuesList.setDato("2");
        docValuesList.setDocCount(1000L);

        final Valores valores = new Valores();
        valores.setDocValuesListList(List.of(
                docValuesList, // 1000 mensajes
                docValuesListDos  // 1000 mensajes
        ));

        when(readFileService.getConfigurationMessage()).thenReturn(valores);
        when(readFileService.getMessage()).thenReturn("Mensaje de prueba");

        // Ejecutamos el método bajo prueba
        sendMessageService.producer("input");

        // Esperamos a que las tareas concurrentes terminen
        Thread.sleep(2000); // Tiempo suficiente para que completen (ajustable)

        // Verificamos que se enviaron 2000 mensajes
        verify(streamBridge, times(2000)).send(eq(SendMessageService.PERFORMANCE_QUEUE), any());
    }
}