package org.iota.tangle.stream

import org.specs2._

class ZeroMQMessageParserSpec extends mutable.Specification {

  val zeroMQMessageParser = new ZeroMQMessageParser

  "ZeroMQMessageParser" >> {
    "parseConfirmedTransactionMessage" should {
      "be able to parse a valid ConfirmedTransactionMessage" in {
        val message = new ZeroMQMessage(
          "sn",
          List(
            "1234",
            "TRANSACTIONHASH",
            "ADDRESSHASH",
            "TRUNKHASH",
            "BRANCHHASH",
            "BUNDLEHASH"
          )
        )

        val confirmedTransactionMessage = zeroMQMessageParser.parseSolidMilestoneConfirmedTransactionMessage(message)

        confirmedTransactionMessage.get.milestoneIndex mustEqual 1234
        confirmedTransactionMessage.get.transactionHash mustEqual "TRANSACTIONHASH"
        confirmedTransactionMessage.get.address mustEqual "ADDRESSHASH"
        confirmedTransactionMessage.get.bundleHash mustEqual "BUNDLEHASH"
        confirmedTransactionMessage.get.trunkHash mustEqual "TRUNKHASH"
        confirmedTransactionMessage.get.branchHash mustEqual "BRANCHHASH"
      }

      "respond with None when invalid MessageType given for ConfirmedTransactionMessage" in {
        val message = new ZeroMQMessage(
          "notvalid",
          List(
            "1234",
            "TRANSACTIONHASH",
            "ADDRESSHASH",
            "TRUNKHASH",
            "BRANCHHASH",
            "BUNDLEHASH"
          )
        )

        val response = zeroMQMessageParser.parseSolidMilestoneConfirmedTransactionMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when invalid Trytes in Hash given for ConfirmedTransactionMessage" in {
        val message = new ZeroMQMessage(
          "sn",
          List(
            "1234",
            "TRANSACTIONHASH001113444",
            "ADDRESSHASH",
            "TRUNKHASH",
            "BRANCHHASH",
            "BUNDLEHASH"
          )
        )

        val response = zeroMQMessageParser.parseSolidMilestoneConfirmedTransactionMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseUnconfirmedTransactionMessage" should {
      "be able to parse a valid UnconfirmedTransactionMessage" in {
        val message = new ZeroMQMessage(
          "tx",
          List(
            "EIHARWZELNXMG9EXAYOKVZPJNVFGLWKVYNSHCLM9VAMFTBGGZI9EESQUM9GYADRCTBRAPLWYVKWCZ9999",
            "ICWALLET9999999999999999999999999999999999999999999999999999999999999999999999999",
            "0",
            "MJA9999999999999999ICWALLET",
            "1522637630",
            "0",
            "0",
            "CHQHIJ9VKNPLPJAYBOFHPDXFEGCUHCLBHZZQVKBQUXBIYEG9HJXUFKNZEYELCWUCDPJBVCZPOHANJ9COY",
            "TZVFKXIZJTWVGGECZNNMINFGCGYAPPDLIVUCIKTSARSRECTTTYRUYHKDV9UPYBWHCDBXLI9QDGFE99999",
            "9SBRKYJGRAXBRGMCOOUJYVXD9Y9AQOGIKVWWZDHFFAJOKTDJKZMWHMJC9RDNWGOYTXJYEBLWTHVZZ9999",
            "1522637652362",
            "IOTATESTTAG9999999999999999"
          )
        )

        val unconfirmedTransactionMessage = zeroMQMessageParser.parseUnconfirmedTransactionMessage(message)

        unconfirmedTransactionMessage.get.transactionHash mustEqual
          "EIHARWZELNXMG9EXAYOKVZPJNVFGLWKVYNSHCLM9VAMFTBGGZI9EESQUM9GYADRCTBRAPLWYVKWCZ9999"
        unconfirmedTransactionMessage.get.address mustEqual
          "ICWALLET9999999999999999999999999999999999999999999999999999999999999999999999999"
        unconfirmedTransactionMessage.get.amount mustEqual 0
        unconfirmedTransactionMessage.get.obsoleteTag mustEqual "MJA9999999999999999ICWALLET"
        unconfirmedTransactionMessage.get.timestampBundleCreation mustEqual 1522637630L
        unconfirmedTransactionMessage.get.indexInBundle mustEqual 0
        unconfirmedTransactionMessage.get.maxIndexInBundle mustEqual 0
        unconfirmedTransactionMessage.get.bundleHash mustEqual
          "CHQHIJ9VKNPLPJAYBOFHPDXFEGCUHCLBHZZQVKBQUXBIYEG9HJXUFKNZEYELCWUCDPJBVCZPOHANJ9COY"
        unconfirmedTransactionMessage.get.trunkHash mustEqual
          "TZVFKXIZJTWVGGECZNNMINFGCGYAPPDLIVUCIKTSARSRECTTTYRUYHKDV9UPYBWHCDBXLI9QDGFE99999"
        unconfirmedTransactionMessage.get.branchHash mustEqual
          "9SBRKYJGRAXBRGMCOOUJYVXD9Y9AQOGIKVWWZDHFFAJOKTDJKZMWHMJC9RDNWGOYTXJYEBLWTHVZZ9999"
      }

     /* "respond with None when invalid MessageType given for UnconfirmedTransactionMessage" in {
        val message = new ZeroMQMessage(
          "eierkuchen",
          List(
            "TRANSACTIONHASH",
            "ADDRESSHASH",
            "2000",
            "TAGHASH",
            "1522538637",
            "2",
            "3",
            "BUNDLEHASH",
            "TRUNKHASH",
            "BRANCHHASH",
            "1522637652362"
          )
        )

        val response = zeroMQMessageParser.parseUnconfirmedTransactionMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when invalid Trytes given for UnconfirmedTransactionMessage" in {
        val message = new ZeroMQMessage(
          "tx",
          List(
            "TRANSACTIONHASH",
            "ADDRESSHASH",
            "2000",
            "TAGHASH5665",
            "1522538637",
            "2",
            "3",
            "BUNDLEHASH",
            "TRUNKHASH",
            "BRANCHHASH",
            "1522637652362"
          )
        )

        val response = zeroMQMessageParser.parseUnconfirmedTransactionMessage(message)

        response.isEmpty mustEqual true
      }*/
    }


    "parseInvalidTransactionMessage" should {
      "be able to parse a valid InvalidTransactionMessage" in {
        val message = new ZeroMQMessage(
          "rtst",
          List(
            "TRANSACTIONHASH"
          )
        )

        val invalidTransaction = zeroMQMessageParser.parseInvalidTransactionMessage(message)

        invalidTransaction.get.transactionHash mustEqual "TRANSACTIONHASH"
        invalidTransaction.get.reason mustEqual "tip"
      }

      "respond with None when invalid Trytes given for InvalidTransactionMessage" in {
        val message = new ZeroMQMessage(
          "rtst",
          List(
            "TRANSACTIONHASH42533"
          )
        )

        val response = zeroMQMessageParser.parseInvalidTransactionMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseLatestMilestoneIndexMessage" should {
      "be able to parse a valid LatestMilestoneIndexMessage" in {
        val message = new ZeroMQMessage(
          "lmi",
          List(
            "123455",
            "123456"
          )
        )

        val latestMilestoneIndex = zeroMQMessageParser.parseLatestMilestoneIndexMessage(message)

        latestMilestoneIndex.get.previousIndex mustEqual 123455
        latestMilestoneIndex.get.latestIndex mustEqual 123456
      }

      "respond with None when invalid MessageType given for LatestMilestoneIndexMessage" in {
        val message = new ZeroMQMessage(
          "spaetzle",
          List(
            "123455",
            "123456"
          )
        )

        val response = zeroMQMessageParser.parseLatestMilestoneIndexMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when invalid Index given for LatestMilestoneIndexMessage" in {
        val message = new ZeroMQMessage(
          "lmi",
          List(
            "123455DDDD",
            "123456"
          )
        )

        val response = zeroMQMessageParser.parseLatestMilestoneIndexMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseNodeStatisticMessage" should {
      "be able to parse a valid NodeStatisticMessage" in {
        val message = new ZeroMQMessage(
          "rstat",
          List(
            "30",
            "23",
            "80",
            "24",
            "2323"
          )
        )

        val nodeStatistic = zeroMQMessageParser.parseNodeStatisticMessage(message)

        nodeStatistic.get.toProcess mustEqual 30
        nodeStatistic.get.toBroadcast mustEqual 23
        nodeStatistic.get.toRequest mustEqual 80
        nodeStatistic.get.toReply mustEqual 24
        nodeStatistic.get.totalTransactions mustEqual 2323
      }

      "respond with None when invalid ZeroMQMessage given for NodeStatisticMessage" in {
        val message = new ZeroMQMessage(
          "wuff",
          List(
            "30",
            "23",
            "80",
            "24",
            "2323"
          )
        )

        val response = zeroMQMessageParser.parseNodeStatisticMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when content contains alphabetic character" in {
        val message = new ZeroMQMessage(
          "rstat",
          List(
            "30",
            "23",
            "80DDDI",
            "24",
            "2323"
          )
        )

        val response = zeroMQMessageParser.parseNodeStatisticMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseAddedNeighborMessage" should {
      "be able to parse a valid AddedNeighborMessage" in {
        val message = new ZeroMQMessage(
          "->",
          List(
            "a.new.neighbor3.com"
          )
        )

        val addedNeighbor = zeroMQMessageParser.parseAddedNeighborMessage(message)

        addedNeighbor.get.address mustEqual "a.new.neighbor3.com"
      }

      "respond with None when invalid MessageType given for AddedNeighborMessage" in {
        val message = new ZeroMQMessage(
          "quack",
          List(
            "a.new.neighbor.com"
          )
        )

        val response = zeroMQMessageParser.parseAddedNeighborMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when invalid Hostname given for AddedNeighborMessage" in {
        val message = new ZeroMQMessage(
          "->",
          List(
            "a.new.neighbor.com!"
          )
        )

        val response = zeroMQMessageParser.parseAddedNeighborMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseAddedNonTetheredNeighborMessage" should {
      "be able to parse a valid AddedNonTetheredNeighborMessage" in {
        val message = new ZeroMQMessage(
          "antn",
          List(
            "a.new.neighbor.com"
          )
        )

        val addedNonTetheredNeighbor = zeroMQMessageParser.parseAddedNonTetheredNeighborMessage(message)

        addedNonTetheredNeighbor.get.uri mustEqual "a.new.neighbor.com"
      }

      "respond with None when invalid ZeroMQMessage given for AddedNonTetheredNeighborMessage" in {
        val message = new ZeroMQMessage(
          "miau",
          List(
            "a.new.neighbor.com"
          )
        )

        val response = zeroMQMessageParser.parseAddedNonTetheredNeighborMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when invalid Hostname given for AddedNonTetheredNeighborMessage" in {
        val message = new ZeroMQMessage(
          "antn",
          List(
            "a.new.neighbor.com!"
          )
        )

        val response = zeroMQMessageParser.parseAddedNonTetheredNeighborMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseRefusedNonTetheredNeighborMessage" should {
      "be able to parse a valid RefusedNonTetheredNeighborMessage" in {
        val message = new ZeroMQMessage(
          "rntn",
          List(
            "a.new.neighbor.com",
            "10"
          )
        )

        val refusedNeighbor = zeroMQMessageParser.parseRefusedNonTetheredNeighborMessage(message)

        refusedNeighbor.get.uri mustEqual "a.new.neighbor.com"
        refusedNeighbor.get.maxPeersAllowed mustEqual 10
      }

      "respond with None when invalid ZeroMQMessage given for  RefusedNonTetheredNeighborMessage" in {
        val message = new ZeroMQMessage(
          "huibuh",
          List(
            "a.new.neighbor.com",
            "10"
          )
        )

        val response = zeroMQMessageParser.parseRefusedNonTetheredNeighborMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when non numeric max given for RefusedNonTetheredNeighborMessage" in {
        val message = new ZeroMQMessage(
          "rntn",
          List(
            "a.new.neighbor.com",
            "10E"
          )
        )

        val response = zeroMQMessageParser.parseRefusedNonTetheredNeighborMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseValidatingDNSMessage" should {
      "be able to parse a valid ValidatingDNSMessage" in {
        val message = new ZeroMQMessage(
          "dnscv",
          List(
            "a.new.neighbor3.com",
            "123.123.234.122"
          )
        )

        val validatingDNS = zeroMQMessageParser.parseValidatingDNSMessage(message)

        validatingDNS.get.hostname mustEqual "a.new.neighbor3.com"
        validatingDNS.get.ip mustEqual "123.123.234.122"
      }

      "respond with None when invalid ZeroMQMessage given for ValidatingDNSMessage" in {
        val message = new ZeroMQMessage(
          "discobob",
          List(
            "a.new.neighbor.com",
            "123.123.234.122"
          )
        )

        val response = zeroMQMessageParser.parseValidatingDNSMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when invalid IP given for ValidatingDNSMessage" in {
        val message = new ZeroMQMessage(
          "dnscv",
          List(
            "a.new.neighbor.com",
            "123.123.234.122D"
          )
        )

        val response = zeroMQMessageParser.parseValidatingDNSMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseValidDNSMessage" should {
      "be able to parse a valid ValidDNSMessage" in {
        val message = new ZeroMQMessage(
          "dnscc",
          List(
            "a.new.neighbor.com"
          )
        )

        val validDNS = zeroMQMessageParser.parseValidDNSMessage(message)

        validDNS.get.hostname mustEqual "a.new.neighbor.com"
      }

      "respond with None when invalid ZeroMQMessage given for ValidDNSMessage" in {
        val message = new ZeroMQMessage(
          "dada",
          List(
            "a.new.neighbor.com"
          )
        )

        val response = zeroMQMessageParser.parseValidDNSMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when invalid Hostname given for ValidDNSMessage" in {
        val message = new ZeroMQMessage(
          "dnscc",
          List(
            "a.new.neighbor.com!"
          )
        )

        val response = zeroMQMessageParser.parseValidDNSMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseChangedIPMessage" should {
      "be able to parse a valid ChangedIPMessage" in {
        val message = new ZeroMQMessage(
          "dnscu",
          List(
            "a.new.neighbor3.com"
          )
        )

        val changedIP = zeroMQMessageParser.parseChangedIPMessage(message)

        changedIP.get.hostname mustEqual "a.new.neighbor3.com"
      }

      "respond with None when invalid ZeroMQMessage given for ChangedIPMessage" in {
        val message = new ZeroMQMessage(
          "muuuhh",
          List(
            "a.new.neighbor.com"
          )
        )

        val response = zeroMQMessageParser.parseChangedIPMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when invalid Hostname given for ChangedIPMessage" in {
        val message = new ZeroMQMessage(
          "dnscu",
          List(
            "a.new.neighbor$.com"
          )
        )

        val response = zeroMQMessageParser.parseChangedIPMessage(message)

        response.isEmpty mustEqual true
      }
    }


    "parseLatestSolidSubtangleMilestoneMessage" should {
      "be able to parse a valid LatestSolidSubtangleMilestoneMessage" in {
        val message = new ZeroMQMessage(
          "lmhs",
          List(
            "ANHASH"
          )
        )

        val latestSolidSubtangleMilestone = zeroMQMessageParser.parseLatestSolidSubtangleMilestoneMessage(message)

        latestSolidSubtangleMilestone.get.hash mustEqual "ANHASH"
      }

      "respond with None when invalid ZeroMQMessage given for LatestSolidSubtangleMilestoneMessage" in {
        val message = new ZeroMQMessage(
          "tabtab",
          List(
            "ANHASH"
          )
        )

        val response = zeroMQMessageParser.parseLatestSolidSubtangleMilestoneMessage(message)

        response.isEmpty mustEqual true
      }

      "respond with None when invalid Trytes Hash given for LatestSolidSubtangleMilestoneMessage" in {
        val message = new ZeroMQMessage(
          "lmhs",
          List(
            "ANHASH3"
          )
        )

        val response = zeroMQMessageParser.parseLatestSolidSubtangleMilestoneMessage(message)

        response.isEmpty mustEqual true
      }
    }

  }
}
