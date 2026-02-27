const { Kafka } = require('kafkajs');

const kafka = new Kafka({
  clientId: 'pmtv-test-client',
  brokers: ['192.168.0.165:9093'],
  retry: {
    initialRetryTime: 100,
    retries: 8
  }
});

const topic = 'pmtv-test-topic';
const producer = kafka.producer();
const consumer = kafka.consumer({ groupId: 'pmtv-test-group' });

async function run() {
  try {
    console.log('Connecting producer...');
    await producer.connect();

    console.log('Connecting consumer...');
    await consumer.connect();

    console.log(`Subscribing to topic: ${topic}`);
    await consumer.subscribe({ topic, fromBeginning: true });

    console.log('Consumer is running and waiting for messages...');
    await consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        console.log(`Received message: ${message.value.toString()}`);
      },
    });

    await new Promise((resolve) => setTimeout(resolve, 2000));

    console.log('Sending 500 messages...');
    for (let i = 1; i <= 10000; i++) {
      const timestamp = new Date().toISOString();
      const messageValue = `Message #${i} from PaymeTv at ${timestamp}`;

      await producer.send({
        topic,
        messages: [{ value: messageValue }],
      });

      if (i % 100 === 0) {
        console.log(`Sent ${i} messages...`);
      }
    }
    console.log('All 500 messages sent successfully');

    await new Promise((resolve) => setTimeout(resolve, 5000));

  } catch (error) {
    console.error('Error:', error);
  } finally {
    console.log('Disconnecting...');
    await producer.disconnect();
    await consumer.disconnect();
  }
}

run().catch(console.error);

