terraform {
  required_providers {
    proxmox = {
      source  = "Telmate/proxmox"
      version = "3.0.1-rc6"
    }
  }
}

provider "proxmox" {
  pm_api_url      = "https://${var.proxmox_host}:8006/api2/json"
  pm_user         = var.proxmox_user
  pm_password     = var.proxmox_password
  pm_tls_insecure = true
}

terraform { 
  cloud { 
    
    organization = "homelab-satish" 

    workspaces { 
      name = "dev-cluster" 
    } 
  } 
}

resource "proxmox_vm_qemu" "kube_node" {
  count       = var.vm_count
  name        = "${var.cluster_name}-node-${count.index + 1}"
  desc        = "Kubernetes node for dev cluster"
  target_node = var.proxmox_node
  clone       = var.cloud_init_template

  # Cloud-Init and VM Configuration
  agent    = 1 # Ensure QEMU agent is enabled for IP retrieval
  os_type  = "cloud-init"
  cores    = var.vm_cores
  sockets  = 1
  cpu_type = "host"
  memory   = var.vm_memory
  scsihw   = "virtio-scsi-pci"

  # Disk Configuration
  disks {
    scsi {
      scsi0 {
        disk {
          size      = var.vm_disk_size
          storage   = var.vm_storage
          cache     = "writeback"
          replicate = true
        }
      }
    }
    ide {
      ide2 {
        cloudinit {
          storage = var.vm_storage
        }
      }
    }
  }

  # Network Configuration
  network {
    id     = 0
    model  = "virtio"
    bridge = "vmbr0"
  }

  # Cloud-Init Settings
  boot      = "order=scsi0"
  ipconfig0 = "ip=192.168.5.${200 + count.index}/22,gw=${var.vm_gateway}"

  # User Configuration
  ciuser     = var.cloud_init_user
  cipassword = var.cloud_init_password

  # Inject SSH public key into VM
  sshkeys = file(var.ssh_public_key)
}
