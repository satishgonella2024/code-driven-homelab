output "vm_ips" {
  description = "IP addresses of the created VMs"
  value       = [for vm in proxmox_vm_qemu.jenkins_server : vm.ipconfig0]
}

output "vm_names" {
  description = "Names of the created VMs"
  value       = [for vm in proxmox_vm_qemu.jenkins_server : vm.name]
}