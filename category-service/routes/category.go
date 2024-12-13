package routes

import (
	"encoding/json"
	"log"
	"net/http"

	"category-service/models"

	"github.com/gorilla/mux"
	"gorm.io/gorm"
)

type Router struct {
	DB *gorm.DB
}

func NewRouter(db *gorm.DB) *mux.Router {
	router := mux.NewRouter()
	handler := &Router{DB: db}

	router.HandleFunc("/api/category", handler.GetCategoriesHandler).Methods("GET")
	router.HandleFunc("/api/category", handler.CreateCategoryHandler).Methods("POST")

	return router
}

func (r *Router) GetCategoriesHandler(w http.ResponseWriter, req *http.Request) {
	var categories []models.Category

	// Use GORM to find all categories
	result := r.DB.Find(&categories)
	if result.Error != nil {
		http.Error(w, "Failed to query categories", http.StatusInternalServerError)
		log.Printf("Database query error: %v", result.Error)
		return
	}

	// Set response headers and encode JSON
	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(categories); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		log.Printf("JSON encoding error: %v", err)
	}
}

func (r *Router) CreateCategoryHandler(w http.ResponseWriter, req *http.Request) {
	var category models.Category

	// Decode the incoming JSON request
	if err := json.NewDecoder(req.Body).Decode(&category); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		log.Printf("JSON decoding error: %v", err)
		return
	}

	// Create the category in the database
	result := r.DB.Create(&category)
	if result.Error != nil {
		http.Error(w, "Failed to create category", http.StatusInternalServerError)
		log.Printf("Database create error: %v", result.Error)
		return
	}

	// Set response headers and encode the created category
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(category); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		log.Printf("JSON encoding error: %v", err)
	}
}
