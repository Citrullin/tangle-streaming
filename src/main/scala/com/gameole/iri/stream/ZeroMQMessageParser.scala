package com.gameole.iri.stream

import com.gameole.iri.stream.messages.milestoneMessages.{LatestMilestoneIndexMessage, LatestSolidSubtangleMilestoneMessage}
import com.gameole.iri.stream.messages.nodeMessages._
import com.gameole.iri.stream.messages.transactionMessages.{ConfirmedTransactionMessage, InvalidTransactionMessage, UnconfirmedTransactionMessage}
import org.slf4j.LoggerFactory


class ZeroMQMessageParser {
  private val logger = LoggerFactory.getLogger(classOf[ZeroMQMessageParser])

  def parseConfirmedTransactionMessage(zeroMQMessage: ZeroMQMessage): ConfirmedTransactionMessage = {
    logger.debug("Parse ConfirmedTransactionMessage [ZeroMQ message]...")

    val confirmedTransactionMessage = ConfirmedTransactionMessage(
      milestoneIndex = zeroMQMessage.message.head.toInt,
      transactionHash = zeroMQMessage.message(1),
      addressHash = zeroMQMessage.message(2),
      trunkHash = zeroMQMessage.message(3),
      branchHash = zeroMQMessage.message(4),
      bundleHash = zeroMQMessage.message(5)
    )

    logger.debug("Confirmed with milestone index: " + confirmedTransactionMessage.milestoneIndex)
    logger.debug("Transaction Hash: " + confirmedTransactionMessage.transactionHash)
    logger.debug("Address Hash: " + confirmedTransactionMessage.addressHash)
    logger.debug("Trunk Hash: " + confirmedTransactionMessage.trunkHash)
    logger.debug("Branch Hash: " + confirmedTransactionMessage.branchHash)
    logger.debug("Bundle Hash: " + confirmedTransactionMessage.bundleHash)

    confirmedTransactionMessage
  }

  def parseUnconfirmedTransactionMessage(zeroMQMessage: ZeroMQMessage): UnconfirmedTransactionMessage = {
    logger.debug("Parse UnconfirmedTransactionMessage [ZeroMQ message]...")


    val unconfirmedTransactionMessage = UnconfirmedTransactionMessage(
      transactionHash = zeroMQMessage.message.head,
      addressHash = zeroMQMessage.message(1),
      amount = zeroMQMessage.message(2).toLong,
      tagHash = zeroMQMessage.message(3),
      timestamp = zeroMQMessage.message(4).toLong * 1000,
      indexInBundle = zeroMQMessage.message(5).toInt,
      maxIndexInBundle = zeroMQMessage.message(6).toInt,
      bundleHash = zeroMQMessage.message(7),
      trunkHash = zeroMQMessage.message(8),
      branchHash = zeroMQMessage.message(9)
    )

    logger.debug("Transaction hash: " + unconfirmedTransactionMessage.transactionHash)
    logger.debug("Iota amount: " + unconfirmedTransactionMessage.amount)
    logger.debug("Tag hash: " + unconfirmedTransactionMessage.tagHash)
    logger.debug("Index in bundle: " + unconfirmedTransactionMessage.indexInBundle)
    logger.debug("Max index of bundle: " + unconfirmedTransactionMessage.maxIndexInBundle)
    logger.debug("Trunk hash: " + unconfirmedTransactionMessage.trunkHash)
    logger.debug("Branch hash: " + unconfirmedTransactionMessage.branchHash)
    logger.debug("Address hash: " + unconfirmedTransactionMessage.addressHash)
    logger.debug("Date Time: " + unconfirmedTransactionMessage.timestamp)

    unconfirmedTransactionMessage
  }

  def parseInvalidTransactionMessage(zeroMQMessage: ZeroMQMessage): InvalidTransactionMessage = {
    logger.debug("Parse InvalidTransactionMessage [ZeroMQ message]...")

    val reason: String = zeroMQMessage.messageType match{
      case "rtsn" => "null value"
      case "rtss" => "!checkSolidity"
      case "rtsv" => "!LedgerValidator"
      case "rtsd" => "extraTip"
      case "rtst" => "tip"
      case _ => "unknown"
    }

    val invalidTransactionMessage = InvalidTransactionMessage(zeroMQMessage.message.head, reason)

    logger.debug("Transaction hash: " + invalidTransactionMessage.transactionHash)
    logger.debug("Reason for Invalidity: " + invalidTransactionMessage.reason)

    invalidTransactionMessage
  }

  def parseLatestMilestoneIndexMessage(zeroMQMessage: ZeroMQMessage): LatestMilestoneIndexMessage = {
    logger.debug("Parse LatestMilestoneIndexMessage [ZeroMQ message]...")

    val latestMilestoneIndexMessage =
      LatestMilestoneIndexMessage(zeroMQMessage.message.head.toInt, zeroMQMessage.message(1).toInt)

    logger.debug("Previous Milestone: " + latestMilestoneIndexMessage.previousIndex)
    logger.debug("Latest Milestone: " + latestMilestoneIndexMessage.latestIndex)

    latestMilestoneIndexMessage
  }

  def parseNodeStatisticMessage(zeroMQMessage: ZeroMQMessage): NodeStatisticMessage = {
    logger.debug("Parse NodeStatisticMessage [ZeroMQ message]...")

    val nodeStatisticMessage = NodeStatisticMessage(
      zeroMQMessage.message.head.toInt,
      zeroMQMessage.message(1).toInt,
      zeroMQMessage.message(2).toInt,
      zeroMQMessage.message(3).toInt,
      zeroMQMessage.message(4).toInt
    )

    logger.debug("To process: " + nodeStatisticMessage.toProcess)
    logger.debug("To broadcast: " + nodeStatisticMessage.toBroadcast)
    logger.debug("To Request: " + nodeStatisticMessage.toRequest)
    logger.debug("To Reply: " + nodeStatisticMessage.toReply)
    logger.debug("Total Transactions: " + nodeStatisticMessage.totalTransactions)

    nodeStatisticMessage
  }

  def parseAddedNeighborMessage(zeroMQMessage: ZeroMQMessage): AddedNeighborMessage = {
    logger.debug("Parse AddedNeighborMessage [ZeroMQ message]...")

    val addedNeighborMessage = AddedNeighborMessage(zeroMQMessage.message.head)

    logger.debug("Address: " + addedNeighborMessage.address)

    addedNeighborMessage
  }

  def parseNonTetheredNeighborMessage(zeroMQMessage: ZeroMQMessage): AddedNonTetheredNeighborMessage = {
    logger.debug("Parse NonTetheredNeighborMessage [ZeroMQ message]...")

    val nonTetheredNeighborMessage = AddedNonTetheredNeighborMessage(zeroMQMessage.message.head)

    logger.debug("URI: " + nonTetheredNeighborMessage.uri)

    nonTetheredNeighborMessage
  }

  def parseRefusedNonTetheredNeighborMessage(zeroMQMessage: ZeroMQMessage): RefusedNonTetheredNeighborMessage = {
    logger.debug("Parse RefusedNonTetheredNeighborMessage [ZeroMQ message]...")

    val refusedNonTetheredNeighborMessage =
      RefusedNonTetheredNeighborMessage(zeroMQMessage.message.head, zeroMQMessage.message(1).toInt)

    logger.debug("URI: " + refusedNonTetheredNeighborMessage.uri)
    logger.debug("Max peers allowed: " + refusedNonTetheredNeighborMessage.maxPeersAllowed)

    refusedNonTetheredNeighborMessage
  }

  def parseValidatingDNSMessage(zeroMQMessage: ZeroMQMessage): ValidatingDNSMessage = {
    logger.debug("Parse ValidatingDNSMessage [ZeroMQ message]...")

    val validatingDNSMessage = ValidatingDNSMessage(zeroMQMessage.message.head, zeroMQMessage.message(1))

    logger.debug("Hostname: " + validatingDNSMessage.hostname)
    logger.debug("IP: " + validatingDNSMessage.ip)

    validatingDNSMessage
  }

  def parseValidDNSMessage(zeroMQMessage: ZeroMQMessage): ValidDNSMessage = {
    logger.debug("Parse ValidDNSMessage [ZeroMQ message]...")

    val validDNSMessage = ValidDNSMessage(zeroMQMessage.message.head)

    logger.debug("Hostname: " + validDNSMessage.hostname)

    validDNSMessage
  }

  def parseChangedIPMessage(zeroMQMessage: ZeroMQMessage): ChangedIPMessage = {
    logger.debug("Parse ValidDNSMessage [ZeroMQ message]...")

    val changedIPMessage = ChangedIPMessage(zeroMQMessage.message.head)

    logger.debug("Hostname: " + changedIPMessage.hostname)

    changedIPMessage
  }

  def parseLatestSolidSubtangleMilestoneHashMessage(zeroMQMessage: ZeroMQMessage): LatestSolidSubtangleMilestoneMessage = {
    logger.debug("Parse LatestMilestoneHashMessage [ZeroMQ message]...")

    val latestSolidSubtangleMilestoneMessage = LatestSolidSubtangleMilestoneMessage(zeroMQMessage.message.head)

    logger.debug("Latest solid subtangle milestone hash: " + latestSolidSubtangleMilestoneMessage.hash)

    latestSolidSubtangleMilestoneMessage
  }

}
