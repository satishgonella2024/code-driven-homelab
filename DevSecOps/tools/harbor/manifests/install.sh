#!/bin/bash

# Add Harbor helm repository
helm repo add harbor https://helm.goharbor.io
helm repo update

# Create namespace
kubectl apply -f namespace.yaml

# Create secrets
kubectl apply -f secrets.yaml

# Install Harbor
helm upgrade --install harbor harbor/harbor \
  --namespace harbor \
  -f ../values.yaml \
  --create-namespace