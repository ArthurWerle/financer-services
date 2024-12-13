package database

import (
	"fmt"
	"log"
	"os"

	"category-service/models"

	"github.com/joho/godotenv"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

// ConnectDatabase establishes a connection to the PostgreSQL database
func ConnectDatabase() (*gorm.DB, error) {
	// Retrieve database connection parameters from environment variables
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, using environment variables")
	}

	host := os.Getenv("DB_HOST")
	port := os.Getenv("DB_PORT")
	user := os.Getenv("DB_USER")
	password := os.Getenv("DB_PASSWORD")
	dbname := os.Getenv("DB_NAME")

	// Construct the connection string
	dsn := fmt.Sprintf("host=%s port=%s user=%s password=%s dbname=%s sslmode=disable TimeZone=UTC",
		host, port, user, password, dbname)

	// Configure GORM logger
	newLogger := logger.New(
		log.New(os.Stdout, "\r\n", log.LstdFlags),
		logger.Config{
			LogLevel: logger.Info, // Log level
		},
	)

	// Open connection to the database
	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{
		Logger: newLogger,
	})
	if err != nil {
		return nil, fmt.Errorf("failed to connect to database: %v", err)
	}

	// Auto-migrate the models
	if err := models.InitializeDatabase(db); err != nil {
		return nil, fmt.Errorf("failed to auto-migrate database: %v", err)
	}

	return db, nil
}
