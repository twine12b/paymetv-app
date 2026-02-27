const { Kafka, logLevel } = require('kafkajs');

const kafka = new Kafka({
  clientId: 'my-producer',
  brokers: ['192.168.0.165:9093'],
  logLevel: logLevel.DEBUG // ERROR | WARN | INFO | DEBUG | NOTHING
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