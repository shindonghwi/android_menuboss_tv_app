.PHONY: disconnect connect devices reconnect

disconnect:
	@adb disconnect 172.30.1.6:5555 || echo "Failed to disconnect from the device."

connect:
	@adb connect 172.30.1.6:5555 || echo "Failed to connect to the device."

devices:
	@adb devices || echo "Failed to list the devices."

reconnect: disconnect connect devices
	@echo "Reconnected successfully."
