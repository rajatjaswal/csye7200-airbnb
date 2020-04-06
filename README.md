# csye7200-airbnb

## Run Kafka, Zookeper and Producer
1. bin/zookeeper-server-start.sh config/zookeeper.properties &
2. bin/kafka-server-start.sh $path/config/server.properties &
3. bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic airbnb &
4. bin/kafka-console-producer.sh --broker-list localhost:9092 --topic airbnb