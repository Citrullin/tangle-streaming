# IRI Stream Provider

This library gives you a nice Interface to the IRI zeroMQ Streaming API.

## Usage

```scala
// Create a new IRIStream Instance
val iriStream = new IRIStream(zeroMQHost, zeroMQPort, "tcp")  

// Filter a topic. E.g. get all UnconfirmedTransactionMessage
iriStream.filter(UnconfirmedTransactionMessage()).foreach(message => 
  println(message.tagHash)
)
```

Available messages can be found in the package 
com.gameole.iri.stream.messages.transactionMessages

## Installation

Since this library is not available in a maven repository at the moment, you need to publish it locally.

```bash
sbt
sbt:iri-stream-provider> clean
sbt:iri-stream-provider> compile
sbt:iri-stream-provider> publishLocal
```
