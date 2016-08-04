package com.appsignal.kafka

import java.lang.System.*

import com.timgroup.statsd.StatsDClient
import com.timgroup.statsd.NonBlockingStatsDClient

import org.apache.kafka.clients.consumer.ConsumerInterceptor
import org.apache.kafka.common.Configurable
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.ConsumerRecord

class StatsdConsumerInterceptor : ConsumerInterceptor<Any, Any> {

  override fun configure(configs: Map<String,*>) {
    out.println("Configs: ${configs}")
  }

  override fun onConsume(records: ConsumerRecords<Any, Any>):ConsumerRecords<Any, Any> {

    out.println("Record count: ${records.count()}")
    out.println("Record partitions: ${records.partitions()}")

    for (record in records) {

      out.println("Record: ${record}")
    }

    return records;
  }

  override fun onCommit(offsets: Map<TopicPartition, OffsetAndMetadata>) {

    out.println("Records: ${offsets}")
  }

  override fun close() {
    // Nothing to do
  }
}
