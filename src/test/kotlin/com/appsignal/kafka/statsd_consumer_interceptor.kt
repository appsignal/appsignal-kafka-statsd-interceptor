package com.appsignal.kafka

import io.kotlintest.specs.ShouldSpec

import org.junit.Test as test
import org.junit.Before as before

import java.net.InetAddress
import java.util.ArrayList

import com.timgroup.statsd.NonBlockingStatsDClient

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.TopicPartition

class StatsdConsumerInterceptorTest : ShouldSpec() {
  val interceptor = StatsdConsumerInterceptor()
  val server = DummyStatsDServer(17254)

  val config = hashMapOf(
    "interceptor.statsd.host" to "localhost",
    "interceptor.statsd.port" to 17254,
    "interceptor.statsd.prefix" to ""
  )

  val ip = InetAddress.getLocalHost();
  val hostname = ip.getHostName();

  override fun beforeAll() {
    interceptor.configure(config)
  }

  override fun afterAll() {
    server.stop()
  }

  init {
    should("send stats to statsd after each consume") {
      var record = ConsumerRecord("topic", 1, 0, "key", "value")
      var partition = TopicPartition("topic", 1)
      var records = ConsumerRecords(mapOf(partition to listOf(record))) as ConsumerRecords<Any, Any>

      interceptor.onConsume(records)
      server.waitForMessage(5);

      var messages = server.messagesReceived
      println(messages.first())

      messages should haveSize(1)
      messages.contains("kafka.consumer.host.Roberts-MacBook-Pro.local.messages:1|c") shouldBe true
      messages should containInAnyOrder("kafka.consumer.host.Roberts-MacBook-Pro.local.messages:1|c")
    }
  }
}
