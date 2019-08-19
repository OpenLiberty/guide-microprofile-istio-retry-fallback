curl -H Host:inventory.example.com http://localhost/inventory/systems/count
#curl -H Host:inventory.example.com http://localhost/metrics/application
#curl -H Host:inventory.example.com http://localhost/metrics/application/ft.io.openliberty.guides.inventory.InventoryResource.getPropertiesForHost.retry.retries.total
echo
echo
#curl -H Host:inventory.example.com http://localhost/inventory/systems/system-service
curl -H Host:inventory.example.com http://localhost/inventory/systems/system-service -I
echo
echo
curl -H Host:inventory.example.com http://localhost/inventory/systems/count
#curl -H Host:inventory.example.com http://localhost/metrics/application
#curl -H Host:inventory.example.com http://localhost/metrics/application/ft.io.openliberty.guides.inventory.InventoryResource.getPropertiesForHost.retry.retries.total
echo
