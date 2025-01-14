
# 🚀 The Code-Driven Homelab: From Chaos to Kubernetes Harmony

Welcome to the Code-Driven Homelab repository! 🎉 This project represents my journey from a chaotic tangle of virtual machines to a scalable, reproducible, and enterprise-grade Kubernetes-powered environment.

## 🗺️ Project Structure

```bash
.
├── README.md
├── ansible
│   ├── inventory
│   │   ├── dev
│   │   │   └── inventory.ini
│   │   └── prod
│   └── playbooks
│       ├── common.yml
│       ├── master.yml
│       └── worker.yml
├── argocd
│   ├── dev
│   └── prod
├── terraform
│   ├── common
│   ├── dev-cluster
│   │   ├── main.tf
│   │   ├── outputs.tf
│   │   ├── terraform.tfstate
│   │   ├── terraform.tfstate.backup
│   │   ├── terraform.tfvars
│   │   └── variables.tf
│   └── prod-cluster
└── terraform.tfstate
```

### Structure Breakdown
- **ansible/**: Modular playbooks for automating VM configuration and Kubernetes setup.
- **terraform/**: Infrastructure-as-code definitions for provisioning VMs on Proxmox.
- **argocd/**: Future enhancements for GitOps workflows.

## 🎯 Key Features
1. **Proxmox + Terraform**: Automated VM creation with Cloud-Init integration.
2. **Ansible Playbooks**: Seamless configuration of nodes into a Kubernetes-ready cluster.
3. **Kubernetes Cluster**: Built for scalability and experimentation.
4. **GitOps-Ready**: Designed with tools like ArgoCD and Jenkins in mind for future CI/CD workflows.

## 🛠️ Tech Stack
- **Proxmox**: Virtualization platform for hosting VMs.
- **Terraform**: Infrastructure as Code for VM provisioning.
- **Ansible**: Configuration management for automated cluster setup.
- **Kubernetes**: Orchestrating workloads in a cloud-native environment.

## 📝 Setup Instructions

### 1️⃣ Provision Infrastructure

Use Terraform to create Proxmox VMs:
```bash
cd terraform/dev-cluster
terraform init
terraform apply
```

### 2️⃣ Configure VMs

Run Ansible playbooks to configure nodes:
```bash
cd ansible
ansible-playbook -i inventory/dev/inventory.ini playbooks/common.yml
ansible-playbook -i inventory/dev/inventory.ini playbooks/master.yml
ansible-playbook -i inventory/dev/inventory.ini playbooks/worker.yml
```

### 3️⃣ Verify Kubernetes Cluster

Check the cluster status:
```bash
kubectl get nodes
```

Expected Output:
```
NAME                 STATUS   ROLES           AGE   VERSION
dev-cluster-node-1   Ready    control-plane   58s   v1.30.8
dev-cluster-node-2   Ready    <none>          24s   v1.30.8
dev-cluster-node-3   Ready    <none>          23s   v1.30.8
```

## 🌟 Future Plans
- **Short-Term Goals:**
  - Deploy ArgoCD for GitOps workflows.
  - Add observability with Prometheus and Grafana.
  - Implement RBAC for secure cluster access.
- **Long-Term Goals:**
  - Extend the cluster to AWS EKS and Azure AKS for hybrid multi-cloud experiments.
  - Experiment with Longhorn and Ceph for advanced storage.
  - Simulate disaster recovery with Velero.

## 🤝 Contributing

Want to collaborate? Open issues or submit pull requests to help improve this project.

## 🌐 Connect
- **Blog**: [Medium Article](https://medium.com/@ssatish.gonella/the-code-driven-homelab-from-chaos-to-kubernetes-harmony-3a851e07efd5)
- **GitHub**: Explore the code, share ideas, and contribute to the project!

## ✨ Acknowledgments

This project wouldn’t be possible without the vibrant DevOps and Kubernetes community. Every challenge was a chance to learn, grow, and create something incredible.

## 📜 License

This project is licensed under the MIT License.

> “Every challenge taught me something new. Every success fueled my curiosity. This isn’t just a homelab—it’s a journey into the heart of DevOps.” 🚀
