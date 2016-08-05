package com.appsignal.kafka

import java.net.InetAddress

import com.timgroup.statsd.StatsDClient
import com.timgroup.statsd.NonBlockingStatsDClient

import org.apache.kafka.clients.consumer.ConsumerInterceptor
import org.apache.kafka.common.Configurable
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.config.ConfigDef

import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.ConsumerRecord

import org.slf4j.LoggerFactory

class StatsdConsumerInterceptor : ConsumerInterceptor<Any, Any> {
  var statsd: StatsDClient? = null
  var hostname: String? = null

  override fun configure(configs: Map<String,*>) {
    var ip = InetAddress.getLocalHost();
    hostname = ip.getHostName();

    var host = (configs.get(STATSD_HOST) ?: STATSD_HOST_DEFAULT) as String
    var port = (configs.get(STATSD_PORT) ?: STATSD_PORT_DEFAULT) as Int
    var prefix = (configs.get(STATSD_PREFIX) ?: STATSD_PREFIX_DEFAULT) as String

    log.info("Starting StatsD ConsumerInterceptor with config: ${host}:${port}/${prefix}")
    statsd = NonBlockingStatsDClient(prefix, host, port)
  }

  override fun onConsume(records: ConsumerRecords<Any, Any>):ConsumerRecords<Any, Any> {
    // Send global count for this host
    statsd!!.count("kafka.consumer.host.${hostname!!}.messages", records.count().toLong())

    // Send count/lag for each topic/partition
    for (partition in records.partitions()) {
      var count = records.records(partition).count().toLong()

      statsd!!.count("kafka.consumer.topic.${partition.topic()}.messages", count)
      statsd!!.count("kafka.consumer.topic.${partition.topic()}.partition.${partition.partition()}.messages", count)

      // Calculate the lag time, only use the first record sice it's the oldest in the queue?
      if (!records.isEmpty()) {
        var lag = System.currentTimeMillis() - records.first().timestamp()

        statsd!!.recordExecutionTime("kafka.consumer.topic.${partition.topic()}.lag", lag)
        statsd!!.recordExecutionTime("kafka.consumer.topic.${partition.topic()}.partition.${partition.partition()}.lag", lag)
      }
    }

    return records;
  }

  override fun onCommit(offsets: Map<TopicPartition, OffsetAndMetadata>) {
    // Nothing to do
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

    private val log = LoggerFactory.getLogger(ConsumerInterceptor::class.java)
  }

}
