package models

import (
	"time"

	"gorm.io/gorm"
)

// Base model with common fields
type BaseModel struct {
	ID        uint           `gorm:"primaryKey"`
	CreatedAt time.Time      `gorm:"column:created_at"`
	UpdatedAt time.Time      `gorm:"column:updated_at"`
	DeletedAt gorm.DeletedAt `gorm:"index"`
}

// Type represents the types table
type Type struct {
	BaseModel
	Name        string `gorm:"column:name;not null;size:255"`
	Description string `gorm:"column:description;type:text"`
}

func (Type) TableName() string {
	return "types"
}

// Category represents the categories table
type Category struct {
	BaseModel
	Name        string `gorm:"column:name;not null;size:255"`
	Description string `gorm:"column:description;type:text"`
	Color       string `gorm:"column:color;size:50"`
}

func (Category) TableName() string {
	return "categories"
}

func InitializeDatabase(db *gorm.DB) error {
	return db.AutoMigrate(
		&Type{},
		&Category{},
	)
}
