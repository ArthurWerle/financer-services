package routes

import (
	"myproject/database"
	"myproject/models"
	"net/http"

	"github.com/gin-gonic/gin"
)

func getCategoriesHandler(w http.ResponseWriter, r *http.Request) {
	// Ensure the request method is GET
	if r.Method != http.MethodGet {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	// Query the categories table
	rows, err := db.Query("SELECT id, name FROM categories")
	if err != nil {
		http.Error(w, "Failed to query database", http.StatusInternalServerError)
		log.Printf("Database query error: %v", err)
		return
	}
	defer rows.Close()

	// Parse the results into a slice of Category
	var categories []Category
	for rows.Next() {
		var category Category
		if err := rows.Scan(&category.ID, &category.Name); err != nil {
			http.Error(w, "Failed to parse database results", http.StatusInternalServerError)
			log.Printf("Row scan error: %v", err)
			return
		}
		categories = append(categories, category)
	}

	// Convert the result to JSON
	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(categories); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		log.Printf("JSON encoding error: %v", err)
	}
}
