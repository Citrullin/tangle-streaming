package com.gameole.iri.stream

import com.gameole.iri.stream.messages.milestoneMessages.{LatestMilestoneIndexMessage, LatestSolidSubtangleMilestoneMessage}
import com.gameole.iri.stream.messages.nodeMessages._
import com.gameole.iri.stream.messages.transactionMessages.{ConfirmedTransactionMessage, InvalidTransactionMessage, UnconfirmedTransactionMessage}
import org.apache.logging.log4j.scala.Logging
import org.specs2._
import org.zeromq.ZMQ

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._

class IRIStreamSpec extends mutable.Specification with Logging {
  private val host = "localhost"
  private val port = 9098
  private val protocol = "tcp"

  logger.info(s"Open ZeroMQ Server on $protocol://$host:$port")
  private val context: ZMQ.Context = ZMQ.context(2)
  private val publisher = context.socket(ZMQ.PUB)
  publisher.bind(s"$protocol://$host:$port")

  val iriStream = new IRIStream(host, port, protocol)

  "IRIStream" >> {


    /*"filter on UnconfirmedTransactionMessage" should {
      def stream = iriStream.filter(UnconfirmedTransactionMessage())

      "respond with UnconfirmedTransactionMessage when sending valid message" in {
        Future{
          Thread.sleep(500)
          val message = "tx TRANSACTIONHASH ADDRESSHASH 2000 TAGHASH 1522538637 2 3 BUNDLEHASH TRUNKHASH BRANCHHASH"
          publisher.send(message)
        }


        val result = stream.head

        result.branchHash mustEqual "BRANCHHASH"
        result.trunkHash mustEqual "TRUNKHASH"
        result.bundleHash mustEqual "BUNDLEHASH"
        result.amount mustEqual 2000
        result.transactionHash mustEqual "TRANSACTIONHASH"
        result.tagHash mustEqual "TAGHASH"
        result.indexInBundle mustEqual 2
        result.maxIndexInBundle mustEqual 3
        result.timestamp mustEqual 1522538637*1000
      }
    }

    "filter on ConfirmedTransactionMessage" should {
      val stream = iriStream.filter(ConfirmedTransactionMessage())

      "respond with ConfirmedTransactionMessage when sending valid message" in {
        Future{
          Thread.sleep(500)
          val message = "sn 1234 TRANSACTIONHASH ADDRESSHASH TRUNKHASH BRANCHHASH BUNDLEHASH"
          publisher.send(message)
        }


        val result = stream.head

        result.branchHash mustEqual "BRANCHHASH"
        result.transactionHash mustEqual "TRANSACTIONHASH"
        result.milestoneIndex mustEqual 1234
        result.trunkHash mustEqual "TRUNKHASH"
        result.addressHash mustEqual "ADDRESSHASH"
        result.bundleHash mustEqual "BUNDLEHASH"
      }
    }*/

    /*"filter on InvalidTransactionMessage" should {
      def stream = iriStream.filter(InvalidTransactionMessage())

      "respond with InvalidTransactionMessage when sending valid message" in {
        val message = "rtst TRANSACTIONHASH"
        publisher.send(message)

        val invalidTransactionMessage = stream.head

        invalidTransactionMessage.transactionHash mustEqual "TRANSACTIONHASH"
      }
    }*/

    /*"filter on LatestMilestoneIndexMessage" should {
      def stream = iriStream.filter(LatestMilestoneIndexMessage())

      "respond with LatestMilestoneIndexMessage when sending valid message" in {
        val message = "lmi 123455 123456"
        publisher.send(message)

        val latestMilestoneIndexMessage = stream.head

        latestMilestoneIndexMessage.previousIndex mustEqual 123455
        latestMilestoneIndexMessage.latestIndex mustEqual 123456
      }
    }*/

    /*"filter on NodeStatisticMessage" should {
      def stream = iriStream.filter(NodeStatisticMessage())

      "respond with NodeStatisticMessage when sending valid message" in {
        val message = "rstat 30 23 80 24 2323"
        publisher.send(message)

        val nodeStatistic = stream.head

        nodeStatistic.toProcess mustEqual 30
        nodeStatistic.toBroadcast mustEqual 23
        nodeStatistic.toRequest mustEqual 80
        nodeStatistic.toReply mustEqual 24
        nodeStatistic.totalTransactions mustEqual 2323
      }
    }*/

    /*"filter on AddedNeighborMessage" should {
      def stream = iriStream.filter(AddedNeighborMessage())

      "respond with AddedNeighborMessage when sending valid message" in {
        val message = "-> a.new.neighbor3.com"
        publisher.send(message)

        val addedNeighbor = stream.head

        addedNeighbor.address mustEqual "a.new.neighbor3.com"
      }
    }*/

    /*"filter on AddedNonTetheredNeighborMessage" should {
      def stream = iriStream.filter(AddedNonTetheredNeighborMessage())

      "respond with AddedNonTetheredNeighborMessage when sending valid message" in {
        val message = "antn a.new.neighbor.com"
        publisher.send(message)

        val addedNonTetheredNeighbor = stream.head

        addedNonTetheredNeighbor.uri mustEqual "a.new.neighbor.com"
      }
    }*/

    /*"filter on RefusedNonTetheredNeighborMessage" should {
      def stream = iriStream.filter(RefusedNonTetheredNeighborMessage())

      "respond with RefusedNonTetheredNeighborMessage when sending valid message" in {
        val message = "rntn a.new.neighbor.com 10"
        publisher.send(message)

        val refusedNeighbor = stream.head

        refusedNeighbor.uri mustEqual "a.new.neighbor.com"
        refusedNeighbor.maxPeersAllowed mustEqual 10
      }
    }*/

    /*"filter on ValidatingDNSMessage" should {
      def stream = iriStream.filter(ValidatingDNSMessage())

      "respond with ValidatingDNSMessage when sending valid message" in {
        val message = "dnscv a.new.neighbor3.com 123.123.234.122"
        publisher.send(message)

        val validatingDNS = stream.head

        validatingDNS.hostname mustEqual "a.new.neighbor3.com"
        validatingDNS.ip mustEqual "123.123.234.122"
      }
    }*/

    /*"filter on ValidDNSMessage" should {
      def stream = iriStream.filter(ValidatingDNSMessage())

      "respond with ValidDNSMessage when sending valid message" in {
        val message = "dnscc a.new.neighbor.com"
        publisher.send(message)

        val validDNS = stream.head

        validDNS.hostname mustEqual "a.new.neighbor.com"
      }
    }*/

    /*"filter on ChangedIPMessage" should {
      def stream = iriStream.filter(ValidatingDNSMessage())

      "respond with ChangedIPMessage when sending valid message" in {
        val message = "dnscu a.new.neighbor3.com"
        publisher.send(message)

        val changedIP = stream.head

        changedIP.hostname mustEqual "a.new.neighbor3.com"
      }
    }*/

    /*"filter on LatestSolidSubtangleMilestoneMessage" should {
      def stream = iriStream.filter(LatestSolidSubtangleMilestoneMessage())

      "respond with LatestSolidSubtangleMilestoneMessage when sending valid message" in {
        val message = "lmhs ANHASH"
        publisher.send(message)

        val latestSolidSubtangleMilestone = stream.head

        latestSolidSubtangleMilestone.hash mustEqual "ANHASH"
      }
    }*/
  }

  step{
    publisher.close()
    context.term()
    iriStream.close()
  }
}
