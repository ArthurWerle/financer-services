#!/bin/bash
# backup-postgres.sh

# Source environment variables
source /stack.env

# Set timestamp (this needs to be generated at runtime)
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Ensure backup directory exists
mkdir -p "$BACKUP_DIR"

# Install mutt if not present
if ! command -v mutt &> /dev/null; then
    apk add --no-cache mutt
fi

# Perform the backup
BACKUP_FILE="$BACKUP_DIR/backup_${DB_NAME}_${TIMESTAMP}.sql.gz"

if PGPASSWORD=${POSTGRES_PASSWORD} pg_dump -h postgres -U "$DB_USER" "$DB_NAME" | gzip > "$BACKUP_FILE"; then
    # Calculate backup size
    BACKUP_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)

    # Email body
    SUCCESS_MESSAGE="Database backup completed successfully at $(date)
Backup file: backup_${DB_NAME}_${TIMESTAMP}.sql.gz
Size: $BACKUP_SIZE
Location: $BACKUP_DIR"

    # Send email with the backup file as an attachment
    echo "$SUCCESS_MESSAGE" | mutt -s "Database Backup Successful - ${DB_NAME}" -a "$BACKUP_FILE" -- "$EMAIL_TO"

    # Delete backups older than 30 days
    find "$BACKUP_DIR" -name "backup_${DB_NAME}_*.sql.gz" -mtime +30 -delete

    # Log the backup
    echo "Backup completed at $(date): $BACKUP_FILE" >> "$BACKUP_DIR/backup.log"
else
    # Failure email body
    FAILURE_MESSAGE="Database backup failed at $(date)
Please check the backup system and database container status."

    # Send failure email
    echo "$FAILURE_MESSAGE" | mutt -s "Database Backup FAILED - ${DB_NAME}" -- "$EMAIL_TO"

    # Log the failure
    echo "Backup FAILED at $(date)" >> "$BACKUP_DIR/backup.log"
    exit 1
fi
