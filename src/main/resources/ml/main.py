import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'  # Suppress TensorFlow warnings

import tensorflow as tf
physical_devices = tf.config.list_physical_devices('GPU')
print("Num GPUs Available: ", len(physical_devices))
tf.config.experimental.set_memory_growth(physical_devices[0], True)
exit
# Initialize a simple TensorFlow constant
x = tf.constant(4.0, shape=(1,1), dtype=tf.float64, name='x')
x= tf.constant([[1, 2, 3], [4, 5, 6]], dtype=tf.float32)
x= tf.ones((3, 3), dtype=tf.float32)
x = tf.eye(3, dtype=tf.float32)
x = tf.random.normal((3, 3), mean=0.0, stddev=1.0, dtype=tf.float32)
x = tf.random.uniform((1, 3), minval=0, maxval=1, dtype=tf.float32)
x = tf.range(start=1, limit=10, delta=2, dtype=tf.int32)
x = tf.cast(x, dtype=tf.float64)
print(x)
x = tf.constant([1, 2, 3])
y = tf.constant([9, 8, 7])
z= tf.add(x, y)
print (x)
print (y)
print (z)
print (tf.subtract(x, y))
print (tf.multiply(x, y))
print (tf.divide(x, y))
z = tf.tensordot(x, y, axes=1)
print (z)

z = x ** 5
print (z)

# Mathematical operations

# Indexing and slicing

# Reshaping tensors
