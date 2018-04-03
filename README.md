# IRI Stream Provider

This library gives you a nice Interface to the IRI zeroMQ Streaming API.

## Usage

### 1. Publish the library to your local repository
Since this library is not available in a maven repository at the moment, you need to publish it locally.

```bash
sbt
sbt:iri-stream-provider> clean
sbt:iri-stream-provider> compile
sbt:iri-stream-provider> publishLocal
```

### 2. Activate the ZeroMQ stream in IRI
Add the following line to the IRI .ini file
```
ZMQ_ENABLED = true
```

## Library features

### All messages
If you are interested in all message, just use the foreach method. You can simply use pattern matching
to match against the different kind of message classes. 

```scala
iriStream.foreach{
    case m: UnconfirmedTransactionMessage => handle(m)
    case m: ConfirmedTransactionMessage => handle(m)
    case m: InvalidTransactionMessage =>
    case m: NodeStatisticMessage => handle(m)
    case m: AddedNeighborMessage => handle(m)
    case m: AddedNonTetheredNeighborMessage => handle(m)
    case m: ChangedIPMessage => handle(m)
    case m: RefusedNonTetheredNeighborMessage => handle(m)
    case m: ValidatingDNSMessage => handle(m)
    case m: ValidDNSMessage => handle(m)
    case _ => logger.error("Unhandled Message type")
}
```

Available messages classes can be found in the package 
com.gameole.iri.stream.messages.transactionMessages

### Filter
If you are only interested in one specific message type, the filter method is the way to go.

```scala
// Create a new IRIStream Instance
val iriStream = new IRIStream("ZeroMQhost", "zeroMQPort", "tcp")  

// Only get UnconfirmedTransactionMessage
iriStream.filter(UnconfirmedTransactionMessage()).foreach(message => 
  println(message.tagHash)
)
```

