output "monitoring_server_ip" {
  value = regex("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+", proxmox_vm_qemu.monitoring_server.ipconfig0)
  description = "The IP address of the ArgoCD server"
}