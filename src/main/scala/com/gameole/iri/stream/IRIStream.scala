package com.gameole.iri.stream

import com.gameole.iri.stream.messages.milestoneMessages.{LatestMilestoneIndexMessage, LatestSolidSubtangleMilestoneMessage}
import com.gameole.iri.stream.messages.nodeMessages._
import com.gameole.iri.stream.messages.transactionMessages._
import org.apache.logging.log4j.scala.Logging
import org.apache.logging.log4j.Level
import scalapb.GeneratedMessage

class IRIStream(val host: String, val port: Int, val protocol: String) extends Logging{

  logger.debug("Create ZeroMQ Connection...")
  val connectionConfiguration = new ServerConnectionConf(host, port, protocol)
  val streamService = new ZeroMQStreamService(connectionConfiguration, "")

  logger.debug("Get ZeroMQ Stream...")
  private val stream = streamService.getMessageStream
  val messageParser = new ZeroMQMessageParser

  def filter(t: UnconfirmedTransactionMessage): Stream[UnconfirmedTransactionMessage] = stream.unconfirmedTransactions
  def filter(t: ConfirmedTransactionMessage): Stream[ConfirmedTransactionMessage] = stream.confirmedTransactions
  def filter(t: InvalidTransactionMessage): Stream[InvalidTransactionMessage] = stream.invalidTransactions
  def filter(t: NodeStatisticMessage): Stream[NodeStatisticMessage] = stream.nodeStatistics
  def filter(t: AddedNeighborMessage): Stream[AddedNeighborMessage] = stream.addedNeighbors
  def filter(t: AddedNonTetheredNeighborMessage): Stream[AddedNonTetheredNeighborMessage] =
    stream.addedNonTetheredNeighbors
  def filter(t: RefusedNonTetheredNeighborMessage): Stream[RefusedNonTetheredNeighborMessage] =
    stream.refusedNonTetheredNeighbors
  def filter(t: ValidatingDNSMessage): Stream[ValidatingDNSMessage] = stream.validatingDNS
  def filter(t: ValidDNSMessage): Stream[ValidDNSMessage] = stream.validDNS
  def filter(t: ChangedIPMessage): Stream[ChangedIPMessage] = stream.changedIP
  def filter(t: LatestMilestoneIndexMessage): Stream[LatestMilestoneIndexMessage] = stream.latestMilestoneIndex
  def filter(t: LatestSolidSubtangleMilestoneMessage): Stream[LatestSolidSubtangleMilestoneMessage] =
    stream.latestSolidSubtangleMilestone

  def foreach(f: GeneratedMessage => Unit): Unit =
    stream.map(messageParser.parse).filter(_.isDefined).map(_.get).foreach(f(_))

  def map[T](f: Option[GeneratedMessage] => T): Stream[T] = stream.map(m => f(messageParser.parse(m)))

  def close(): Unit = {
    logger.debug("Close IRIStream...")
    streamService.close()
  }

  implicit class streamParser(stream: Stream[ZeroMQMessage]){
    val messageParser = new ZeroMQMessageParser

    def unconfirmedTransactions: Stream[UnconfirmedTransactionMessage] =
      stream.filter(_.messageType == "tx")
        .map(messageParser.parseUnconfirmedTransactionMessage)
        .filter(_.isDefined).map(_.get)

    def confirmedTransactions: Stream[ConfirmedTransactionMessage] =
      stream.filter(_.messageType == "sn")
        .map(messageParser.parseConfirmedTransactionMessage)
        .filter(_.isDefined).map(_.get)

    def invalidTransactions: Stream[InvalidTransactionMessage] =
      stream.filter(m => {
        m.messageType == "rtsn" | m.messageType == "rtss" | m.messageType == "rtsv" | m.messageType == "rtsd"
      }).map(messageParser.parseInvalidTransactionMessage).filter(_.isDefined).map(_.get)

    def nodeStatistics: Stream[NodeStatisticMessage] =
      stream.filter(_.messageType == "rstat")
        .map(messageParser.parseNodeStatisticMessage).filter(_.isDefined).map(_.get)

    def addedNeighbors: Stream[AddedNeighborMessage] =
      stream.filter(_.messageType == "->")
        .map(messageParser.parseAddedNeighborMessage).filter(_.isDefined).map(_.get)

    def addedNonTetheredNeighbors: Stream[AddedNonTetheredNeighborMessage] =
      stream.filter(_.messageType == "antn")
        .map(messageParser.parseAddedNonTetheredNeighborMessage).filter(_.isDefined).map(_.get)

    def refusedNonTetheredNeighbors: Stream[RefusedNonTetheredNeighborMessage] =
      stream.filter(_.messageType == "rntn")
        .map(messageParser.parseRefusedNonTetheredNeighborMessage).filter(_.isDefined).map(_.get)

    def validatingDNS: Stream[ValidatingDNSMessage] =
      stream.filter(_.messageType == "dnscv")
        .map(messageParser.parseValidatingDNSMessage).filter(_.isDefined).map(_.get)

    def validDNS: Stream[ValidDNSMessage] =
      stream.filter(_.messageType == "dnscc")
        .map(messageParser.parseValidDNSMessage).filter(_.isDefined).map(_.get)

    def changedIP: Stream[ChangedIPMessage] =
      stream.filter(_.messageType == "dnscu")
        .map(messageParser.parseChangedIPMessage).filter(_.isDefined).map(_.get)

    def latestMilestoneIndex: Stream[LatestMilestoneIndexMessage] =
      stream.filter(_.messageType == "lmi")
        .map(messageParser.parseLatestMilestoneIndexMessage).filter(_.isDefined).map(_.get)

    def latestSolidSubtangleMilestone: Stream[LatestSolidSubtangleMilestoneMessage] =
      stream.filter(_.messageType == "lmhs")
        .map(messageParser.parseLatestSolidSubtangleMilestoneMessage).filter(_.isDefined).map(_.get)
  }
}
