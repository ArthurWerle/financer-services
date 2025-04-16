#!/bin/bash
# backup-postgres.sh

set -euo pipefail

# Source environment variables
source /stack.env

# Configuration
BACKUP_DIR="/backups"
LOG_FILE="${BACKUP_DIR}/backup.log"
RETENTION_DAYS=30
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/backup_${POSTGRES_DB}_${TIMESTAMP}.sql.gz"

# Logging function
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "$LOG_FILE"
}

# Error handling
handle_error() {
    log "ERROR: $1"
    echo "Database backup failed at $(date)
Error: $1
Please check the backup system and database container status." | mutt -s "Database Backup FAILED - ${POSTGRES_DB}" -- "$EMAIL_TO"
    exit 1
}

# Skip in test environment
if [ "$ENV" = "test" ]; then
    log "Skipping backup in test environment"
    exit 0
fi

# Ensure backup directory exists
mkdir -p "$BACKUP_DIR" || handle_error "Failed to create backup directory"

# Check if required environment variables are set
for var in POSTGRES_DB POSTGRES_USER POSTGRES_PASSWORD EMAIL_TO; do
    if [ -z "${!var}" ]; then
        handle_error "Required environment variable $var is not set"
    fi
done

# Install mutt if not present
if ! command -v mutt &> /dev/null; then
    apk add --no-cache mutt
fi

# Perform the backup
log "Starting backup of ${POSTGRES_DB} database"

if PGPASSWORD="${POSTGRES_PASSWORD}" pg_dump -h postgres -U "${POSTGRES_USER}" "${POSTGRES_DB}" | gzip > "$BACKUP_FILE"; then
    # Calculate backup size
    BACKUP_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
    
    log "Backup completed successfully: $BACKUP_FILE (Size: $BACKUP_SIZE)"

    # Email notification
    echo "Database backup completed successfully at $(date)
Backup file: $(basename "$BACKUP_FILE")
Size: $BACKUP_SIZE
Location: $BACKUP_DIR" | mutt -s "Database Backup Successful - ${POSTGRES_DB}" -a "$BACKUP_FILE" -- "$EMAIL_TO"

    # Cleanup old backups
    log "Cleaning up backups older than $RETENTION_DAYS days"
    find "$BACKUP_DIR" -name "backup_${POSTGRES_DB}_*.sql.gz" -mtime +$RETENTION_DAYS -delete

    # Log cleanup
    log "Cleanup completed"
else
    handle_error "pg_dump command failed"
fi
