variable "proxmox_host" {
  description = "Proxmox server hostname or IP"
}

variable "proxmox_user" {
  description = "Proxmox user"
  default     = "root@pam"
}

variable "proxmox_password" {
  description = "Proxmox password"
  sensitive   = true
}

variable "proxmox_node" {
  description = "Proxmox node name"
}

variable "vm_count" {
  description = "Number of VMs to create"
  default     = 3
}

variable "cluster_name" {
  description = "Cluster name"
  default     = "kube"
}

variable "cloud_init_template" {
  description = "Cloud-init template name"
  default     = "ubuntu-2404-cloud-template"
}

variable "vm_cores" {
  description = "Number of CPU cores for VMs"
  default     = 2
}

variable "vm_memory" {
  description = "Memory size (in MB) for VMs"
  default     = 2048
}

variable "vm_disk_size" {
  description = "Disk size (in GB) for VMs"
  default     = "20G"
}

variable "vm_storage" {
  description = "Storage pool for VMs"
  default     = "workloads-storage"
}

variable "vm_network_base" {
  description = "Base network for VMs"
  default     = "192.168.5"
}

variable "vm_gateway" {
  description = "Gateway for the VM network"
  default     = "192.168.4.1"
}

variable "cloud_init_user" {
  description = "Default cloud-init user"
  default     = "ubuntu"
}

variable "cloud_init_password" {
  description = "Default cloud-init user password"
  default     = "password"
  sensitive   = true
}

variable "ssh_public_key" {
  description = "Path to the SSH public key"
  default     = "~/.ssh/id_rsa.pub"
}