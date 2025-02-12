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
- Concurrencias ajudatable por parte del consumer