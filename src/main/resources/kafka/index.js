const kafka = require('kafka-node');
const client = new kafka.KafkaClient(
    { kafkaHost: 'localhost:9092' }
);

const producer = new kafka.Producer(client);
const consumer = new kafka.Consumer(
    client,
     [{ topic: 'hello-world', partition: 0  }],
     { autoCommit: true }
);

producer.on('ready', () =>
    producer.send(
        [{ topic: 'hello-world', messages: ['Hello, world!'] }],
        () => console.log('Message sent')
    )
);

consumer.on('message', (message) =>
    console.log('Received', message.value));
