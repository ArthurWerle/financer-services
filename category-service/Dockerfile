FROM golang:1.23.4

# Set the working directory
WORKDIR /app

# Copy go.mod and go.sum
COPY go.mod go.sum ./

# Download all dependencies
RUN go mod download

# Copy the source code
COPY . .

# Build the application
RUN go build -o main .

# Expose the port
EXPOSE 8080

# Run the application
CMD ["./main"]