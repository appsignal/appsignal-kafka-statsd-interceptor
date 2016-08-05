package com.appsignal.kafka

import java.net.InetAddress

import com.timgroup.statsd.StatsDClient
import com.timgroup.statsd.NonBlockingStatsDClient

import org.apache.kafka.clients.producer.ProducerInterceptor
import org.apache.kafka.common.Configurable
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.config.ConfigDef

import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

import org.slf4j.LoggerFactory

class StatsdProducerInterceptor : ProducerInterceptor<Any, Any> {
  var statsd: StatsDClient? = null
  var hostname: String? = null

  override fun configure(configs: Map<String,*>) {
    var ip = InetAddress.getLocalHost();
    hostname = ip.getHostName();

    var host = (configs.get(STATSD_HOST) ?: STATSD_HOST_DEFAULT) as String
    var port = (configs.get(STATSD_PORT) ?: STATSD_PORT_DEFAULT) as Int
    var prefix = (configs.get(STATSD_PREFIX) ?: STATSD_PREFIX_DEFAULT) as String

    log.info("Starting StatsD ProducerInterceptor with config: ${host}:${port}/${prefix}")
    statsd = NonBlockingStatsDClient(prefix, host, port)
  }

  override fun onSend(record: ProducerRecord<Any, Any>):ProducerRecord<Any, Any> {
    return record;
  }

  override fun onAcknowledgement(metadata: RecordMetadata, exception: Exception?) {
    statsd!!.incrementCounter("kafka.producer.host.${hostname!!}.messages")
    statsd!!.incrementCounter("kafka.producer.topic.${metadata.topic()}.messages")
    statsd!!.incrementCounter("kafka.producer.topic.${metadata.topic()}.partition.${metadata.partition()}.messages")
  }

  override fun close() {
    statsd!!.stop()
  }

  companion object {
    val STATSD_HOST = "interceptor.statsd.host"
    val STATSD_PORT = "interceptor.statsd.port"
    val STATSD_PREFIX = "interceptor.statsd.prefix"

    val STATSD_HOST_DEFAULT = "localhost"
    val STATSD_PORT_DEFAULT = 8125
    val STATSD_PREFIX_DEFAULT = ""

    private val CONFIG_DEF = ConfigDef()
      .define(STATSD_HOST, ConfigDef.Type.STRING, STATSD_HOST_DEFAULT, ConfigDef.Importance.HIGH, "StatsD host")
      .define(STATSD_PORT, ConfigDef.Type.INT, STATSD_PORT_DEFAULT, ConfigDef.Importance.HIGH, "StatsD port")
      .define(STATSD_PREFIX, ConfigDef.Type.STRING, STATSD_PREFIX_DEFAULT, ConfigDef.Importance.LOW, "StatsD prefix")

    private val log = LoggerFactory.getLogger(StatsdProducerInterceptor::class.java)
  }

}
