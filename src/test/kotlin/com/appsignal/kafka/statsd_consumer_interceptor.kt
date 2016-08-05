package com.appsignal.kafka

import kotlin.test.*
import com.nhaarman.mockito_kotlin.*
import org.junit.Test as test
import org.junit.Before as before

import java.net.InetAddress
import com.timgroup.statsd.NonBlockingStatsDClient

class StatsdConsumerInterceptorTest {
  var interceptor = StatsdConsumerInterceptor()
  var config = hashMapOf(
    "consumer.statsd.host" to "statsd.host",
    "consumer.statsd.port" to 1234,
    "consumer.statsd.prefix" to "prefix"
  )
//
  //@before fun initialize() {
  //  task.start(config)
  //  task.collection!!.deleteMany(Document())
  //}

  //@test fun testConfigure() {
//
  //  val ip : InetAddress = mock()
//
  //  doReturn("foo.local").whenever(ip).getHostName()
//
  //  interceptor.configure(config)
//
  //  assertEquals("foo.local", interceptor.hostname)
  //}
//
  @test fun testStop() {

    val client : NonBlockingStatsDClient = mock()

    interceptor.configure(config)
    interceptor.close()

    verify(client).stop()
  }
}
