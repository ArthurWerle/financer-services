package main

import (
	"fmt"
	"net/http"
)

func main() {
	connectToDatabase()
	defer db.Close()

	http.HandleFunc("/api/category", getCategoriesHandler)

	fmt.Println("Server is running on http://localhost:8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatalf("Server failed to start: %v", err)
	}
}