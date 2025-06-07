package utils

import (
	"log"
	"os"
)

func FailOnError(err error, msg string) {
	if err != nil {
		log.Panicf("%s: %s", msg, err)
	}
}

func GetEnvVar(envVar string) (value string) {
	return os.Getenv(envVar)
}
