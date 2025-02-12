# stream-function-samples

Estamos usando Docker compose para levantar 3 replicas en local tanto del producer como consumer

```yml

services:
  producer:
    build: .  # Construye la imagen desde el Dockerfile
    deploy:
      replicas: 3  # Lanza 3 instancias automáticamente
      resources:
        limits:
          cpus: "1.0"
          memory: "1g"
    volumes:
      - "/home/rubn/logs:/home/rubn/logs" # Carpeta compartida de logs
    ports:
      - "0:8083"  # Exponemos el puerto
```

## Hardware

- 1 CPU 
- 1 GB de ram
- 3 Threads configurables por cada instancia/replica de productor
- Concurrencias ajustable por parte del consumer

## Broker rabbitMQ

Ahora mismo tanto producer/consumer envian datos al broker en el NAS

Por medio de esta config en el application.yml

```yml
spring:
  cloud:
    function:
      definition: consumer
    stream:
      default-binder: rabbit
      rabbit:
        bindings:
          consumer-in-0:
            consumer:
              prefetch: 1
      binders:
        rabbit:
          type: rabbit
          environment:
            spring:
              rabbitmq:
                host: ${URL_RABBIT}
                port: 5672
                username: ${USER}
                password: ${PASSWORD}
      bindings:
        consumer-in-0:  # Canal de entrada (Consumidor)
          destination: performance-queue
          group: my-consumer-group
          consumer:
            concurrency: 5 # Numero de consumidores concurrentes
            prefetch: 1
```

![image](https://github.com/user-attachments/assets/3f99b733-91f8-4f3d-9fea-ee72c69ee8b3)


## El prefetch

Para esta prueba el prefetch se dejara en uno, porque en caso de que una instancia se caiga, no se perderan mensajes, queremos que todos los mensajes los procese el consumer.
