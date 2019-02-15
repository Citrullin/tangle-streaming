package org.iota.tangle.stream

import org.iota.tangle.stream.messages.transactionMessages.ConfirmedTransactionMessage
import org.specs2._

class ZeroMQParseMethodSpec extends mutable.Specification{

    val zeroMQMessageParser = new ZeroMQMessageParser

    "ZeroMQMessageParser" >> {
      "parse must return a ConfirmedTransactionMessage when given valid input" in {
        val message = new ZeroMQMessage(
          "OFFLINE9SPAM9ADDRESS99999999999999999999999999999999999999999999999999999999TYPPI",
          List("QKDNCLOPWNERYHAMIIPCXMOCMI9TZEHW9EYYOANQRVZPUJIWVPQOCEIW99VKRMBXAENMMROLGUMZ99999", "405289", "sn")
        )

        val output = zeroMQMessageParser.parse(message)

        output.isDefined must_== true
        output.get.isInstanceOf[ConfirmedTransactionMessage] must_== true
      }
    }
}
