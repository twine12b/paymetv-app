const { kafka} = require('kafkajs');

async function run({
    const kafka = new Kafka({
        clientId: 'paymetv-client',
        brokers: ['192.168.0.165:9095'],
//        brokers: ['kafka-broker-4.kafka.svc.cluster.local:9095'],
        logLevel: logLevel.ERROR,
        retry: {
            initialRetryTime: 100,
            retries: 8
        }
    });
})

const topic = 'test-topic';
const producer = kafka.producer();
const consumer = kafka.consumer({ groupId: 'paymetv-group' });

try {
    console.log('Subscribing consumer to topic: ${topic}');
    await consumer.subscribe({ topic, fromBeginning: true });

    // start the consumer
    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            console.log({ 'Received message: ': ${message.value.toString()};
        },
    });

    await new promise((res) => setTimeout(res, 10000));

    const messageValue = 'Hello from Paymetv at ${new Date().toISOString()}';
    console.log('Sending message: ${messageValue}');
    await producer.send({
        topic,
        messages: [ { value: messageValue } ],
    });
    console.log('Message sent successfully');

    await new promise((res) => setTimeout(res, 10000));
    } catch (error) {
      console.error('Error:', error);
    } finally {
    console.lo('Disconnecting ...');
    await producer.disconnect();
//    await consumer.stop();
    await consumer.disconnect();
    }
}

run().catch(console.error);