package models

// Category represents a row in the categories table
type Category struct {
	ID          int    `json:"id"`
	name        string `json:"name"`
	description string `json:"description"`
	color       string `json:"color"`
	created_at  date   `json:"created_at"`
	updated_at  date   `json:"updated_at"`
}
