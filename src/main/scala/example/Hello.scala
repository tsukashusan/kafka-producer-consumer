package example

object Hello extends Greeting with App {
  if (args.length < 2) {
    usage()
    sys.exit
  }else{
    run(args)
  }
  println(greeting)


  def usage(): Unit = {
    println("Usage:");
    println("kafka-example.jar <producer|consumer> brokerhosts [groupid]");
    sys.exit(1);
  }


  def run(args: Array[String]): Unit = {
    import java.util.UUID
    val brokers = args(1)
    var groupId: String = null.asInstanceOf[String]
    args(0) match {
      case "producer" => produce(brokers)
      case "consumer" =>{
        if(args.length == 3) {
          groupId = args(2)
        }
        else{
          groupId = UUID.randomUUID.toString
        }
        consume(brokers, groupId)
      }
      case _ => usage
    }
    sys.exit
  }


  def produce(brokers: String): Unit = {
    import java.util.{Properties, Random}
    import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
    // Set properties used to configure the producer
    val properties = new Properties()
    // Set the brokers (bootstrap servers)
    println(s"bootstrap.servers:$brokers")
    properties.setProperty("bootstrap.servers", brokers)
    // Set how to serialize key/value pairs
    properties.setProperty("key.serializer","org.apache.kafka.common.serialization.StringSerializer")
    properties.setProperty("value.serializer","org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](properties)

    // So we can generate random sentences
    val random = new Random()
    val sentences = Seq(
            "the cow jumped over the moon",
            "an apple a day keeps the doctor away",
            "four score and seven years ago",
            "snow white and the seven dwarfs",
            "i am at two with nature")

    val progressAnimation = "|/-\\"
    // Produce a bunch of records 
    (0 to (1000000 -1)).foreach{ i =>{
        // Pick a sentence at random
        val sentence = sentences(random.nextInt(sentences.length))
        // Send the sentence to the test topic
        producer.send(new ProducerRecord[String, String]("test", sentence))
        val progressBar = "\r" + progressAnimation.charAt(i % progressAnimation.length()) + " " + i
        System.out.write(progressBar.getBytes())
      }
    }
  }


  def consume(brokers: String, groupId: String): Unit = {
    
    import java.util.Properties
    import org.apache.kafka.clients.consumer.KafkaConsumer
    import scala.collection.JavaConverters._
    // Configure the consumer
    val properties = new Properties()
    // Point it to the brokers
    println(s"bootstrap.servers:$brokers")
    properties.setProperty("bootstrap.servers", brokers)
    // Set the consumer group (all consumers must belong to a group).
    properties.setProperty("group.id", groupId)
    // Set how to serialize key/value pairs
    properties.setProperty("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer")
    properties.setProperty("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer")
    // When a group is first created, it has no offset stored to start reading from. This tells it to start
    // with the earliest record in the stream.
    properties.setProperty("auto.offset.reset","earliest")
    // Create a consumer
    val consumer = new KafkaConsumer[String, String](properties)

    // Subscribe to the 'test' topic
    consumer.subscribe(Seq("test").asJava)

    // Loop until ctrl + c
    val count: Int = 0
    while(true) {
      // Poll for records
      val records = consumer.poll(200)
      // Did we get any?
      if (records.count() == 0) {
          // timeout/nothing to read
      } else {
        // Yes, loop over records
        var count = 0
        for(record <- records.asScala){
            // Display record and count
            count = count + 1
            println( count + ": " + record.value())
        }
      }
    }
  }
}

trait Greeting {
  lazy val greeting: String = "hello"
}
