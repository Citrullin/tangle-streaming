package com.gameole.iri.stream

import com.gameole.iri.stream.messages.nodeMessages._
import com.gameole.iri.stream.messages.transactionMessages._
import org.slf4j.LoggerFactory

class IRIStream(val host: String, val port: Int, val protocol: String) {
  private val logger = LoggerFactory.getLogger("IRIStream")

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


  def unconfirmedTransactions: Stream[UnconfirmedTransactionMessage] = stream.unconfirmedTransactions
  def confirmedTransactions: Stream[ConfirmedTransactionMessage] = stream.confirmedTransactions
  def invalidTransactions: Stream[InvalidTransactionMessage] = stream.invalidTransactions
  def nodeStatistics: Stream[NodeStatisticMessage] = stream.nodeStatistics
  def addedNeighbors: Stream[AddedNeighborMessage] = stream.addedNeighbors
  def addedNonTetheredNeighbors: Stream[AddedNonTetheredNeighborMessage] = stream.addedNonTetheredNeighbors
  def refusedNonTetheredNeighbors: Stream[RefusedNonTetheredNeighborMessage] = stream.refusedNonTetheredNeighbors
  def validatingDNS: Stream[ValidatingDNSMessage] = stream.validatingDNS
  def validDNS: Stream[ValidDNSMessage] = stream.validDNS
  def changedIPs: Stream[ChangedIPMessage] = stream.changedIP

  implicit class streamParser(stream: Stream[ZeroMQMessage]){
    val zeroMQMessageParser = new ZeroMQMessageParser

    def unconfirmedTransactions: Stream[UnconfirmedTransactionMessage] =
      stream.filter(_.messageType == "tx").map(zeroMQMessageParser.parseUnconfirmedTransactionMessage)

    def confirmedTransactions: Stream[ConfirmedTransactionMessage] =
      stream.filter(_.messageType == "sn").map(zeroMQMessageParser.parseConfirmedTransactionMessage)

    def invalidTransactions: Stream[InvalidTransactionMessage] =
      stream.filter(m => {
        m.messageType == "rtsn" | m.messageType == "rtss" | m.messageType == "rtsv" | m.messageType == "rtsd"
      }).map(zeroMQMessageParser.parseInvalidTransactionMessage)

    def nodeStatistics: Stream[NodeStatisticMessage] =
      stream.filter(_.messageType == "rstat").map(zeroMQMessageParser.parseNodeStatisticMessage)

    def addedNeighbors: Stream[AddedNeighborMessage] =
      stream.filter(_.messageType == "->").map(zeroMQMessageParser.parseAddedNeighborMessage)

    def addedNonTetheredNeighbors: Stream[AddedNonTetheredNeighborMessage] =
      stream.filter(_.messageType == "antn").map(zeroMQMessageParser.parseNonTetheredNeighborMessage)

    def refusedNonTetheredNeighbors: Stream[RefusedNonTetheredNeighborMessage] =
      stream.filter(_.messageType == "rntn").map(zeroMQMessageParser.parseRefusedNonTetheredNeighborMessage)

    def validatingDNS: Stream[ValidatingDNSMessage] =
      stream.filter(_.messageType == "dnscv").map(zeroMQMessageParser.parseValidatingDNSMessage)

    def validDNS: Stream[ValidDNSMessage] =
      stream.filter(_.messageType == "dnscc").map(zeroMQMessageParser.parseValidDNSMessage)

    def changedIP: Stream[ChangedIPMessage] =
      stream.filter(_.messageType == "dnscu").map(zeroMQMessageParser.parseChangedIPMessage)
  }
}
