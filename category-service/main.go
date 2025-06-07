package main

import (
	"category-service/database"
	"category-service/external"
	"category-service/logger"
	"category-service/routes"
	"fmt"
	"net/http"
)

func main() {
	rmq := external.StartRabbitMQ()
	defer external.CleanupRabbitMQ(rmq)

	logger.Initialize(rmq.Channel, rmq.Queue, rmq.Ctx)
	log := logger.GetLogger()

	db, err := database.ConnectDatabase()
	if err != nil {
		log.Fatal("Database connection failed: " + err.Error())
	}

	router := routes.AppRouter(db)

	fmt.Println("Server is running on http://localhost:8080")
	if err := http.ListenAndServe(":8080", router); err != nil {
		log.Fatal("Server failed to start: " + err.Error())
	}
}
