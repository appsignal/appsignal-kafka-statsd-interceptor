package com.appsignal.kafka

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import java.nio.charset.Charset
import java.util.ArrayList

class DummyStatsDServer(port: Int) {
  public var messagesReceived: MutableList<String> = ArrayList()
  var server: DatagramSocket = DatagramSocket(port)

  init {
    Thread {
      var packet: DatagramPacket = DatagramPacket(ByteArray(256), 256);
      server.receive(packet)
      messagesReceived.add(String(packet.getData(), Charset.forName("UTF-8")).trim())
    }.start();
  }

  public fun stop() {
    server.close();
  }

  public fun waitForMessage(seconds: Int) {
    waitForMessages(1, seconds)
  }

  public fun waitForMessages(messageCount: Int, seconds: Int) {
    var sleepCycles = 0
    var wantedSleepCycles = (seconds * 1000) / 50

    while (
      (messagesReceived.size < messageCount) &&
      (sleepCycles < wantedSleepCycles)
    ) {
      try {
        Thread.sleep(50L)
      } catch (e: InterruptedException) {}
      sleepCycles = sleepCycles + 1
    }
  }
}
