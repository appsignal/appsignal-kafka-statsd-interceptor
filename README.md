# Kafka StatsD Interceptor

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

Then run the tests: `gradle test`.
