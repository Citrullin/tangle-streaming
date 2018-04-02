package com.gameole.iri.stream

import com.gameole.iri.stream.messages.milestoneMessages._
import com.gameole.iri.stream.messages.nodeMessages._
import com.gameole.iri.stream.messages.transactionMessages._
import org.apache.logging.log4j.scala.Logging
import org.apache.logging.log4j.Level


class ZeroMQMessageParser extends Logging{
  logger.info("Create Instance of ZeroMQMessageParser")

  private def isTrytes(s: String): Boolean =
    s.map(char => (char.isLetter && char.isUpper) || char.toInt == 9).forall(_ == true)
  private def isAlpha(s: String): Boolean = s.forall(_.isLetter)
  private def isNumber(s: String): Boolean = s.forall(_.isDigit)
  private def isHostname(s: String): Boolean =
    s.map(char => char == '.' || (char.isLetter && char.isLower) || char.isDigit).forall(_ == true)
  private def isIP(s: String): Boolean = s.map(char => char == '.' || char.isDigit).forall(_ == true)
  private def isURI(s: String): Boolean = isHostname(s) || isIP(s)

  private def logWrongFormat(message: ZeroMQMessage): Unit = {
    logger.debug("Message is not in the expected format.")
    logger.debug("Message type: " + message.messageType)
    logger.debug("Message content: " + message.message)
  }

  def parseConfirmedTransactionMessage(zeroMQMessage: ZeroMQMessage): Option[ConfirmedTransactionMessage] = {
    logger.debug("Parse ConfirmedTransactionMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(
      messageContent.length == 6 && messageType == "sn" &&
        isNumber(messageContent.head) && messageContent.tail.forall(isTrytes)
    ){
      val confirmedTransactionMessage = ConfirmedTransactionMessage(
        milestoneIndex = messageContent.head.toInt,
        transactionHash = messageContent(1),
        addressHash = messageContent(2),
        trunkHash = messageContent(3),
        branchHash = messageContent(4),
        bundleHash = messageContent(5)
      )

      logger.debug("Confirmed with milestone index: " + confirmedTransactionMessage.milestoneIndex)
      logger.debug("Transaction Hash: " + confirmedTransactionMessage.transactionHash)
      logger.debug("Address Hash: " + confirmedTransactionMessage.addressHash)
      logger.debug("Trunk Hash: " + confirmedTransactionMessage.trunkHash)
      logger.debug("Branch Hash: " + confirmedTransactionMessage.branchHash)
      logger.debug("Bundle Hash: " + confirmedTransactionMessage.bundleHash)

      Some(confirmedTransactionMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseUnconfirmedTransactionMessage(zeroMQMessage: ZeroMQMessage): Option[UnconfirmedTransactionMessage] = {
    logger.debug("Parse UnconfirmedTransactionMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(
      messageType == "tx" && messageContent.length == 10 &&
        messageContent.takeRight(3).forall(isTrytes) &&
        messageContent.take(2).forall(isTrytes) &&
        isTrytes(messageContent(3)) &&
        messageContent.slice(4, 6).forall(isNumber)
    ){
      val unconfirmedTransactionMessage = UnconfirmedTransactionMessage(
        transactionHash = zeroMQMessage.message.head,
        addressHash = zeroMQMessage.message(1),
        amount = zeroMQMessage.message(2).toLong,
        tagHash = zeroMQMessage.message(3),
        timestamp = zeroMQMessage.message(4).toInt * 1000,
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
      logger.debug("Timstamp: " + unconfirmedTransactionMessage.timestamp)

      Some(unconfirmedTransactionMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseInvalidTransactionMessage(zeroMQMessage: ZeroMQMessage): Option[InvalidTransactionMessage] = {
    logger.debug("Parse InvalidTransactionMessage [ZeroMQ message]...")

    val messageContent = zeroMQMessage.message
    val messageType = zeroMQMessage.messageType

    if(messageContent.length == 1 && isTrytes(messageContent.head) && isAlpha(messageType)){
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

      Some(invalidTransactionMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseLatestMilestoneIndexMessage(zeroMQMessage: ZeroMQMessage): Option[LatestMilestoneIndexMessage] = {
    logger.debug("Parse LatestMilestoneIndexMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(messageType == "lmi" && messageContent.forall(isNumber)){
      val latestMilestoneIndexMessage =
        LatestMilestoneIndexMessage(zeroMQMessage.message.head.toInt, zeroMQMessage.message(1).toInt)

      logger.debug("Previous Milestone: " + latestMilestoneIndexMessage.previousIndex)
      logger.debug("Latest Milestone: " + latestMilestoneIndexMessage.latestIndex)

      Some(latestMilestoneIndexMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseNodeStatisticMessage(zeroMQMessage: ZeroMQMessage): Option[NodeStatisticMessage] = {
    logger.debug("Parse NodeStatisticMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(messageType == "rstat" && messageContent.length == 5 && messageContent.forall(isNumber)){
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

      Some(nodeStatisticMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseAddedNeighborMessage(zeroMQMessage: ZeroMQMessage): Option[AddedNeighborMessage] = {
    logger.debug("Parse AddedNeighborMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(messageType == "->" && messageContent.length == 1 && isURI(messageContent.head)){
      val addedNeighborMessage = AddedNeighborMessage(zeroMQMessage.message.head)

      logger.debug("Address: " + addedNeighborMessage.address)

      Some(addedNeighborMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseAddedNonTetheredNeighborMessage(zeroMQMessage: ZeroMQMessage): Option[AddedNonTetheredNeighborMessage] = {
    logger.debug("Parse AddedNonTetheredNeighborMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(messageType == "antn" && messageContent.length == 1 && isURI(messageContent.head)){
      val nonTetheredNeighborMessage = AddedNonTetheredNeighborMessage(zeroMQMessage.message.head)

      logger.debug("URI: " + nonTetheredNeighborMessage.uri)

      Some(nonTetheredNeighborMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseRefusedNonTetheredNeighborMessage(zeroMQMessage: ZeroMQMessage):
  Option[RefusedNonTetheredNeighborMessage] = {
    logger.debug("Parse RefusedNonTetheredNeighborMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(
      messageType == "rntn" && messageContent.length == 2 &&
        isURI(messageContent.head) && isNumber(messageContent(1))
    ){
        val refusedNonTetheredNeighborMessage =
          RefusedNonTetheredNeighborMessage(zeroMQMessage.message.head, zeroMQMessage.message(1).toInt)

        logger.debug("URI: " + refusedNonTetheredNeighborMessage.uri)
        logger.debug("Max peers allowed: " + refusedNonTetheredNeighborMessage.maxPeersAllowed)

        Some(refusedNonTetheredNeighborMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseValidatingDNSMessage(zeroMQMessage: ZeroMQMessage): Option[ValidatingDNSMessage] = {
    logger.debug("Parse ValidatingDNSMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(
      messageType == "dnscv" && messageContent.length == 2 &&
        isHostname(messageContent.head) && isIP(messageContent(1))
    ){
      val validatingDNSMessage = ValidatingDNSMessage(zeroMQMessage.message.head, zeroMQMessage.message(1))

      logger.debug("Hostname: " + validatingDNSMessage.hostname)
      logger.debug("IP: " + validatingDNSMessage.ip)

      Some(validatingDNSMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseValidDNSMessage(zeroMQMessage: ZeroMQMessage): Option[ValidDNSMessage] = {
    logger.debug("Parse ValidDNSMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(messageType == "dnscc" && messageContent.length == 1 && isHostname(messageContent.head)){
      val validDNSMessage = ValidDNSMessage(zeroMQMessage.message.head)

      logger.debug("Hostname: " + validDNSMessage.hostname)

      Some(validDNSMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseChangedIPMessage(zeroMQMessage: ZeroMQMessage): Option[ChangedIPMessage] = {
    logger.debug("Parse ValidDNSMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(messageType == "dnscu" && messageContent.length == 1 && isHostname(messageContent.head)){
      val changedIPMessage = ChangedIPMessage(zeroMQMessage.message.head)

      logger.debug("Hostname: " + changedIPMessage.hostname)

      Some(changedIPMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

  def parseLatestSolidSubtangleMilestoneMessage(zeroMQMessage: ZeroMQMessage):
  Option[LatestSolidSubtangleMilestoneMessage] = {
    logger.debug("Parse LatestSolidSubtangleMilestoneMessage [ZeroMQ message]...")

    val messageType = zeroMQMessage.messageType
    val messageContent = zeroMQMessage.message

    if(messageType == "lmhs" && messageContent.length == 1 && isTrytes(messageContent.head)){
      val latestSolidSubtangleMilestoneMessage = LatestSolidSubtangleMilestoneMessage(zeroMQMessage.message.head)

      logger.debug("Latest solid subtangle milestone hash: " + latestSolidSubtangleMilestoneMessage.hash)

      Some(latestSolidSubtangleMilestoneMessage)
    }else{
      logWrongFormat(zeroMQMessage)
      None
    }
  }

}
