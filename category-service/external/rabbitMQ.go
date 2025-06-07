package external

import (
	"category-service/utils"
	"context"
	"log"
	"time"

	amqp "github.com/rabbitmq/amqp091-go"
)

// RabbitMQConnection holds the connection and channel for cleanup
type RabbitMQConnection struct {
	Conn    *amqp.Connection
	Channel *amqp.Channel
	Queue   amqp.Queue
	Ctx     context.Context
}

func StartRabbitMQ() *RabbitMQConnection {
	rabbitmqURL := utils.GetEnvVar("RABBITMQ_URL")

	if rabbitmqURL == "" {
		log.Fatal("RABBITMQ_URL environment variable is not set")
	}

	conn, err := amqp.Dial(rabbitmqURL)
	if err != nil {
		log.Panicf("%s: %s", "Failed to connect to RabbitMQ", err)
	}

	ch, err := conn.Channel()
	if err != nil {
		log.Panicf("%s: %s", "Failed to open a channel", err)
	}

	q, err := ch.QueueDeclare(
		"logs", // name
		true,   // durable
		false,  // delete when unused
		false,  // exclusive
		false,  // no-wait
		nil,    // arguments
	)
	if err != nil {
		log.Panicf("%s: %s", "Failed to declare a queue", err)
	}

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	return &RabbitMQConnection{
		Conn:    conn,
		Channel: ch,
		Queue:   q,
		Ctx:     ctx,
	}
}

// CleanupRabbitMQ closes the RabbitMQ connection and channel
func CleanupRabbitMQ(rmq *RabbitMQConnection) {
	if rmq == nil {
		return
	}

	if rmq.Channel != nil {
		if err := rmq.Channel.Close(); err != nil {
			log.Printf("Error closing RabbitMQ channel: %v", err)
		}
	}

	if rmq.Conn != nil {
		if err := rmq.Conn.Close(); err != nil {
			log.Printf("Error closing RabbitMQ connection: %v", err)
		}
	}
}

func SendMessage(message string, rmq *RabbitMQConnection) {
	err := rmq.Channel.PublishWithContext(rmq.Ctx,
		"",             // exchange
		rmq.Queue.Name, // routing key
		false,          // mandatory
		false,          // immediate
		amqp.Publishing{
			ContentType: "text/plain",
			Body:        []byte(message),
		})
	if err != nil {
		log.Panicf("%s: %s", "Failed to publish a message", err)
	}
	log.Printf(" [x] Sent %s\n", message)
}
