package com.gameole.iri.stream

import com.gameole.iri.stream.messages.nodeMessages._
import com.gameole.iri.stream.messages.transactionMessages._
import org.apache.logging.log4j.scala.Logging
import org.apache.logging.log4j.Level

class IRIStream(val host: String, val port: Int, val protocol: String) extends Logging{

  logger.debug("Create ZeroMQ Connection...")
  val connectionConfiguration = new ServerConnectionConf(host, port, protocol)
  val streamService = new ZeroMQStreamService(connectionConfiguration, "")

  logger.debug("Get ZeroMQ Stream...")
  private def stream = streamService.getMessageStream

  def filter(t: UnconfirmedTransactionMessage): Stream[UnconfirmedTransactionMessage] = stream.unconfirmedTransactions
  def filter(t: ConfirmedTransactionMessage): Stream[ConfirmedTransactionMessage] = stream.confirmedTransactions
  def filter(t: InvalidTransactionMessage): Stream[InvalidTransactionMessage] = stream.invalidTransactions
  def filter(t: NodeStatisticMessage): Stream[NodeStatisticMessage] = stream.nodeStatistics
  def filter(t: AddedNeighborMessage): Stream[AddedNeighborMessage] = stream.addedNeighbors
  def filter(t: AddedNonTetheredNeighborMessage): Stream[AddedNonTetheredNeighborMessage] = stream.addedNonTetheredNeighbors
  def filter(t: RefusedNonTetheredNeighborMessage): Stream[RefusedNonTetheredNeighborMessage] = stream.refusedNonTetheredNeighbors
  def filter(t: ValidatingDNSMessage): Stream[ValidatingDNSMessage] = stream.validatingDNS
  def filter(t: ValidDNSMessage): Stream[ValidDNSMessage] = stream.validDNS
  def filter(t: ChangedIPMessage): Stream[ChangedIPMessage] = stream.changedIP

  implicit class streamParser(stream: Stream[ZeroMQMessage]){
    val zeroMQMessageParser = new ZeroMQMessageParser

    def unconfirmedTransactions: Stream[UnconfirmedTransactionMessage] =
      stream.filter(_.messageType == "tx")
        .map(zeroMQMessageParser.parseUnconfirmedTransactionMessage)
        .filter(_.isDefined).map(_.get)

    def confirmedTransactions: Stream[ConfirmedTransactionMessage] =
      stream.filter(_.messageType == "sn")
        .map(zeroMQMessageParser.parseConfirmedTransactionMessage)
        .filter(_.isDefined).map(_.get)

    def invalidTransactions: Stream[InvalidTransactionMessage] =
      stream.filter(m => {
        m.messageType == "rtsn" | m.messageType == "rtss" | m.messageType == "rtsv" | m.messageType == "rtsd"
      }).map(zeroMQMessageParser.parseInvalidTransactionMessage).filter(_.isDefined).map(_.get)

    def nodeStatistics: Stream[NodeStatisticMessage] =
      stream.filter(_.messageType == "rstat")
        .map(zeroMQMessageParser.parseNodeStatisticMessage).filter(_.isDefined).map(_.get)

    def addedNeighbors: Stream[AddedNeighborMessage] =
      stream.filter(_.messageType == "->")
        .map(zeroMQMessageParser.parseAddedNeighborMessage).filter(_.isDefined).map(_.get)

    def addedNonTetheredNeighbors: Stream[AddedNonTetheredNeighborMessage] =
      stream.filter(_.messageType == "antn")
        .map(zeroMQMessageParser.parseAddedNonTetheredNeighborMessage).filter(_.isDefined).map(_.get)

    def refusedNonTetheredNeighbors: Stream[RefusedNonTetheredNeighborMessage] =
      stream.filter(_.messageType == "rntn")
        .map(zeroMQMessageParser.parseRefusedNonTetheredNeighborMessage).filter(_.isDefined).map(_.get)

    def validatingDNS: Stream[ValidatingDNSMessage] =
      stream.filter(_.messageType == "dnscv")
        .map(zeroMQMessageParser.parseValidatingDNSMessage).filter(_.isDefined).map(_.get)

    def validDNS: Stream[ValidDNSMessage] =
      stream.filter(_.messageType == "dnscc")
        .map(zeroMQMessageParser.parseValidDNSMessage).filter(_.isDefined).map(_.get)

    def changedIP: Stream[ChangedIPMessage] =
      stream.filter(_.messageType == "dnscu")
        .map(zeroMQMessageParser.parseChangedIPMessage).filter(_.isDefined).map(_.get)
  }
}
