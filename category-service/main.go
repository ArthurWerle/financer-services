package main

import (
	"category-service/database"
	"category-service/routes"
	"fmt"
	"log"
	"net/http"
)

func main() {
	db, err := database.ConnectDatabase()
	if err != nil {
		log.Fatalf("Database connection failed: %v", err)
	}

	router := routes.AppRouter(db)

	fmt.Println("Server is running on http://localhost:8080")
	if err := http.ListenAndServe(":8080", router); err != nil {
		log.Fatalf("Server failed to start: %v", err)
	}
}
