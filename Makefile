rebuild-run:
	@echo "Rebuilding and restarting the 'app' service using docker-compose..."
	docker-compose up --build -d app
	@echo "Done! The 'app' service has been rebuilt and restarted, connected to the 'db' service."
	@echo "You can check logs with: docker-compose logs -f app"

.PHONY: rebuild-run
