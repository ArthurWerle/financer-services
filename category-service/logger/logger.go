package logger

import (
	"context"
	"fmt"
	"log"
	"os"
	"sync"

	amqp "github.com/rabbitmq/amqp091-go"
)

type Logger struct {
	*log.Logger
	channel *amqp.Channel
	queue   amqp.Queue
	ctx     context.Context
	mu      sync.Mutex
}

var (
	instance *Logger
	once     sync.Once
)

func Initialize(channel *amqp.Channel, queue amqp.Queue, ctx context.Context) {
	once.Do(func() {
		instance = &Logger{
			Logger:  log.New(os.Stdout, "", log.LstdFlags),
			channel: channel,
			queue:   queue,
			ctx:     ctx,
		}
	})
}

func GetLogger() *Logger {
	if instance == nil {
		log.Fatal("Logger not initialized")
	}

	return instance
}

func (l *Logger) Log(level, message string) {
	l.mu.Lock()
	defer l.mu.Unlock()

	l.Printf("[%s] %s", level, message)

	if l.channel == nil || l.channel.IsClosed() {
		log.Println("RabbitMQ channel or connection is closed")
	}

	log.Printf("Publishing to queue: %s", l.queue.Name)

	logMessage := fmt.Sprintf("[%s] %s", level, message)
	err := l.channel.PublishWithContext(l.ctx,
		"",           // exchange
		l.queue.Name, // routing key
		false,        // mandatory
		false,        // immediate
		amqp.Publishing{
			ContentType: "text/plain",
			Body:        []byte(logMessage),
		})
	if err != nil {
		// If we can't send to RabbitMQ, at least log to stdout
		l.Printf("Failed to send log to RabbitMQ: %v", err)
	}
}

func (l *Logger) Info(message string) {
	l.Log("INFO", message)
}

func (l *Logger) Error(message string) {
	l.Log("ERROR", message)
}

func (l *Logger) Fatal(message string) {
	l.Log("FATAL", message)
	os.Exit(1)
}
