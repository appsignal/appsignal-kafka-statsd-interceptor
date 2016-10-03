# Kafka StatsD Interceptor

[![Build Status](https://travis-ci.org/appsignal/appsignal-kafka-statsd-interceptor.svg?branch=master)](https://travis-ci.org/appsignal/appsignal-kafka-statsd-interceptor)

This project attempts to create a Kafka Interceptor that sends metrics to StatsD.

https://cwiki.apache.org/confluence/display/KAFKA/KIP-42%3A+Add+Producer+and+Consumer+Interceptors

## Building the interceptor

The interceptor runs on Java 1.8. Download and install it from the Oracle
website (yuck :-):

https://java.com/en/download

Then install Gradle, that's a combination of Bundler and Rake which
downloads dependencies and runs builds and tests.

```
brew install gradle
```

Then run the tests: `gradle test`.
And build: `gradle build`.

## Enable the interceptor

1. Make sure the build `.jar` is in your classpath
2. Add the interceptor(s) to your properties:

```
producer.interceptor.classes=com.appsignal.kafka.StatsdProducerInterceptor
consumer.interceptor.classes=com.appsignal.kafka.StatsdConsumerInterceptor
```

## Configure the interceptor

Accepted configuration:

```
statsd.host   (defaults to "localhost")
statsd.port   (defaults to 8125)
statsd.prefix (defaults to "")
```

For either the `consumer` or the `producer` interceptor set the config:

```
# Producer
producer.interceptor.statsd.host=localhost
producer.interceptor.config.statsd.host=8125
producer.interceptor.statsd.prefix=kafka

# Consumer
consumer.interceptor.statsd.host=localhost
consumer.interceptor.config.statsd.host=8125
consumer.interceptor.statsd.prefix=kafka

```
