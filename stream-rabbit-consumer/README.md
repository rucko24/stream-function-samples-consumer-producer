### Example of producer transaction with RabbitMQ and Spring Cloud Stream

This example demonstrates producer transaction with Spring Cloud Stream and RabbitMQ.
Once started you can use RabbitMq dashboard or any other available means to send messages to `sendMessageService-in-0` exchange.
Messages should successfully reach the queue bound to sendMessageService-out destination (see `application.properties` for more details).
To simulate failure simply send string message with value `fail`. You can see that the message will never reach the queue 
bound to the output exchange even though the exception happens after the send call.