const { Kafka, logLevel } = require('kafkajs');

const kafka = new Kafka({
  clientId: 'my-producer',
  brokers: ['localhost:9092'],
  logLevel: logLevel.DEBUG, // ERROR | WARN | INFO | DEBUG | NOTHING
});

const producer = kafka.producer();

async function run() {
  await producer.connect();

  await producer.send({
    topic: 'test-topic',
    messages: [
      { value: 'Hello Kafka!' },
    ],
  });

  console.log('Message sent successfully');
}

run().catch(console.error);