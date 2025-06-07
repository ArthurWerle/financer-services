package routes

import (
	"encoding/json"
	"net/http"

	"category-service/logger"
	"category-service/models"

	"github.com/gorilla/handlers"
	"github.com/gorilla/mux"
	"gorm.io/gorm"
)

type Router struct {
	DB *gorm.DB
}

func AppRouter(db *gorm.DB) http.Handler {
	router := mux.NewRouter()
	handler := &Router{DB: db}

	router.HandleFunc("/api/healthcheck", handler.Healthcheck).Methods("GET")

	router.HandleFunc("/api/category", handler.ListCategoriesHandler).Methods("GET")
	router.HandleFunc("/api/category", handler.CreateCategoryHandler).Methods("POST")

	router.HandleFunc("/api/category/{id}", handler.GetCategoryByIdHandler).Methods("GET")
	router.HandleFunc("/api/category/{id}", handler.DeleteCategoryByIdHandler).Methods("DELETE")
	router.HandleFunc("/api/category/{id}", handler.UpdateCategoryByIdHandler).Methods("PUT")

	router.HandleFunc("/api/type", handler.ListTypesHandler).Methods("GET")
	router.HandleFunc("/api/type", handler.CreateTypeHandler).Methods("POST")

	router.HandleFunc("/api/type/{id}", handler.GetTypeByIdHandler).Methods("GET")
	router.HandleFunc("/api/type/{id}", handler.DeleteTypeByIdHandler).Methods("DELETE")
	router.HandleFunc("/api/type/{id}", handler.UpdateTypeByIdHandler).Methods("PUT")

	corsMiddleware := handlers.CORS(
		handlers.AllowedOrigins([]string{"http://localhost:3000", "http://192.168.0.7:3000"}),
		handlers.AllowedMethods([]string{"GET", "POST", "PUT", "DELETE", "OPTIONS"}),
		handlers.AllowedHeaders([]string{"Content-Type", "Authorization"}),
	)

	return corsMiddleware(router)
}

func (r *Router) Healthcheck(w http.ResponseWriter, req *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(map[string]string{"status": "ok"}); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}
}

func (r *Router) ListCategoriesHandler(w http.ResponseWriter, req *http.Request) {
	var categories []models.Category

	logger.GetLogger().Info("Hitting /api/category -> ListCategoriesHandler")

	result := r.DB.Order("name ASC").Find(&categories)
	if result.Error != nil {
		http.Error(w, "Failed to query categories", http.StatusInternalServerError)
		logger.GetLogger().Error("Database query error: " + result.Error.Error())
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(categories); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + result.Error.Error())
	}
}

func (r *Router) CreateCategoryHandler(w http.ResponseWriter, req *http.Request) {
	var category models.Category

	if err := json.NewDecoder(req.Body).Decode(&category); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		logger.GetLogger().Error("JSON decoding error: " + err.Error())
		return
	}

	result := r.DB.Create(&category)
	if result.Error != nil {
		http.Error(w, "Failed to create category", http.StatusInternalServerError)
		logger.GetLogger().Error("Database create error: " + result.Error.Error())
		return
	}

	// Set response headers and encode the created category
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(category); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}
}

func (r *Router) GetCategoryByIdHandler(w http.ResponseWriter, req *http.Request) {
	var category models.Category

	params := mux.Vars(req)
	id := params["id"]

	result := r.DB.First(&category, id)
	if result.Error != nil {
		http.Error(w, "Category not found", http.StatusNotFound)
		logger.GetLogger().Error("Database query error: " + result.Error.Error())
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(category); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}
}

func (r *Router) DeleteCategoryByIdHandler(w http.ResponseWriter, req *http.Request) {
	var category models.Category

	params := mux.Vars(req)
	id := params["id"]

	result := r.DB.Delete(&category, id)
	if result.Error != nil {
		http.Error(w, "Category not found", http.StatusNotFound)
		logger.GetLogger().Error("Database query error: " + result.Error.Error())
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(category); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}

	w.WriteHeader(http.StatusOK)
}

func (r *Router) UpdateCategoryByIdHandler(w http.ResponseWriter, req *http.Request) {
	var category models.Category

	params := mux.Vars(req)
	id := params["id"]
	result := r.DB.First(&category, id)
	if result.Error != nil {
		http.Error(w, "Category not found", http.StatusNotFound)
		logger.GetLogger().Error("Database query error: " + result.Error.Error())
		return
	}
	// Decode the incoming JSON request
	if err := json.NewDecoder(req.Body).Decode(&category); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		logger.GetLogger().Error("JSON decoding error: " + err.Error())
		return
	}

	// Update the category in the database
	result = r.DB.Save(&category)
	if result.Error != nil {
		http.Error(w, "Failed to update category", http.StatusInternalServerError)
		logger.GetLogger().Error("Database save error: " + result.Error.Error())
		return
	}
	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(category); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}
}

func (r *Router) ListTypesHandler(w http.ResponseWriter, req *http.Request) {
	var types []models.Type

	result := r.DB.Find(&types)
	if result.Error != nil {
		http.Error(w, "Failed to fetch types", http.StatusInternalServerError)
		logger.GetLogger().Error("Database query error: " + result.Error.Error())
		return
	}
	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(types); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}
}

func (r *Router) CreateTypeHandler(w http.ResponseWriter, req *http.Request) {
	var typeModel models.Type

	// Decode the incoming JSON request
	if err := json.NewDecoder(req.Body).Decode(&typeModel); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		logger.GetLogger().Error("JSON decoding error: " + err.Error())
		return
	}

	// Create the category in the database
	result := r.DB.Create(&typeModel)
	if result.Error != nil {
		http.Error(w, "Failed to create category", http.StatusInternalServerError)
		logger.GetLogger().Error("Database create error: " + result.Error.Error())
		return
	}

	// Set response headers and encode the created category
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusCreated)
	if err := json.NewEncoder(w).Encode(typeModel); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}

	w.WriteHeader(http.StatusOK)
}

func (r *Router) GetTypeByIdHandler(w http.ResponseWriter, req *http.Request) {
	var typeModel models.Type

	params := mux.Vars(req)
	id := params["id"]

	result := r.DB.First(&typeModel, id)
	if result.Error != nil {
		http.Error(w, "Type not found", http.StatusNotFound)
		logger.GetLogger().Error("Database query error: " + result.Error.Error())
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(typeModel); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}
}

func (r *Router) DeleteTypeByIdHandler(w http.ResponseWriter, req *http.Request) {
	var typeModel models.Type

	params := mux.Vars(req)
	id := params["id"]

	result := r.DB.Delete(&typeModel, id)
	if result.Error != nil {
		http.Error(w, "Type not found", http.StatusNotFound)
		logger.GetLogger().Error("Database query error: " + result.Error.Error())
		return
	}

	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(typeModel); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}

	w.WriteHeader(http.StatusOK)
}

func (r *Router) UpdateTypeByIdHandler(w http.ResponseWriter, req *http.Request) {
	var typeModel models.Type

	params := mux.Vars(req)
	id := params["id"]
	result := r.DB.First(&typeModel, id)
	if result.Error != nil {
		http.Error(w, "Type not found", http.StatusNotFound)
		logger.GetLogger().Error("Database query error: " + result.Error.Error())
		return
	}
	// Decode the incoming JSON request
	if err := json.NewDecoder(req.Body).Decode(&typeModel); err != nil {
		http.Error(w, "Invalid request body", http.StatusBadRequest)
		logger.GetLogger().Error("JSON decoding error: " + err.Error())
		return
	}

	// Update the category in the database
	result = r.DB.Save(&typeModel)
	if result.Error != nil {
		http.Error(w, "Failed to update type", http.StatusInternalServerError)
		logger.GetLogger().Error("Database save error: " + result.Error.Error())
		return
	}
	w.Header().Set("Content-Type", "application/json")
	if err := json.NewEncoder(w).Encode(typeModel); err != nil {
		http.Error(w, "Failed to encode response as JSON", http.StatusInternalServerError)
		logger.GetLogger().Error("JSON encoding error: " + err.Error())
	}

	w.WriteHeader(http.StatusOK)
}
