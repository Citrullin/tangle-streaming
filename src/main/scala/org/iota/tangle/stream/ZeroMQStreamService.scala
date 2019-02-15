package org.iota.tangle.stream

import org.apache.logging.log4j.scala.Logging
import org.zeromq.ZMQ
import org.zeromq.ZMQ.Socket


class ZeroMQStreamService(zeroMQServer: ServerConnectionConf, topic: String) extends Logging{
  logger.info("Start ZeroMQStreamService...")

  val context: ZMQ.Context = ZMQ.context(4)
  val subscriber: Socket = context.socket(ZMQ.SUB)

  logger.info(s"Open Connection to ZMQ stream on host ${zeroMQServer.host} and port ${zeroMQServer.port}")
  subscriber.connect(s"${zeroMQServer.protocol}://${zeroMQServer.host}:${zeroMQServer.port}")

  if(topic.isEmpty)
    logger.info("Subscribe to all ZMQ messages")
  else
    logger.info(s"Subscribe to ZQM topic $topic")

  subscriber.subscribe(topic.getBytes)


  def getMessageStream: Stream[ZeroMQMessage] = {
    val message: Array[String] = subscriber.recv(0).map(_.toChar).mkString.split(" ")
    val zmqMessage: ZeroMQMessage = new ZeroMQMessage(message.head, message.tail.toList)

    logger.info(s"Incoming ZMQ Message of type ${zmqMessage.messageType}...")

    zmqMessage #:: getMessageStream
  }

  def close(): Unit = {
    subscriber.close()
    context.term()
  }
}
