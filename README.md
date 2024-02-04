Flow.Publisher:

It's a core interface in the Reactive Streams API, introduced in Java 9.
Acts as a producer of data streams or events, providing data to Flow.Subscribers in an asynchronous, non-blocking manner.

* Ensures ordered delivery of data and handles errors and completion signals.

* subscribe(Flow.Subscriber<? super T> subscriber): Registers a subscriber, starting the data flow if necessary.

* onComplete(): Signals the end of the stream, notifying subscribers.

* onError(Throwable error): Reports an error to subscribers, stopping the stream.
