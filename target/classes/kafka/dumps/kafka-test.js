const { Kafka } = require('kafkajs');

const kafka = new Kafka({
  clientId: 'kafka-test-client',
  brokers: ['127.0.0.1:9093'], // your Kafka address
});

const topic = 'test-topic';

async function run() {
  const producer = kafka.producer();
  const consumer = kafka.consumer({ groupId: 'test-group' });

  await producer.connect();
  await consumer.connect();

  // Ensure topic exists (auto-create must be enabled in broker)
  await consumer.subscribe({ topic, fromBeginning: true });

  console.log('Producing message...');
  await producer.send({
    topic,
    messages: [
      { value: 'Hello from Kafka on 9093 🚀' }
    ],
  });

  console.log('Message produced.');

  console.log('Consuming message...');

  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      console.log(`Received message: ${message.value.toString()}`);
      process.exit(0);
    },
  });
}

run().catch(console.error);
