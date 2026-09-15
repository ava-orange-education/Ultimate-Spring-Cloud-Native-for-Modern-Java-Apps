# CampusFlow Kubernetes example (Chapter 9)

Small, runnable companion manifests that deploy the existing **CampusFlow monolith** and **PostgreSQL** into a local or learning cluster.

This is intentionally compact. It demonstrates Deployments, Services, ConfigMaps, Secrets, probes, non-root security contexts, graceful shutdown alignment, and an optional TLS Ingress. It does **not** include Helm, Kustomize, operators, HPA, NetworkPolicies, or additional application services.

## What is included

| File | Purpose |
|------|---------|
| `namespace.yaml` | `campusflow` namespace |
| `configmap.yaml` | Non-secret application settings |
| `secret.example.yaml` | Example credentials (copy before use) |
| `postgres.yaml` | PostgreSQL Deployment + Service |
| `deployment.yaml` | Monolith Deployment (probes, security, `/tmp` volume) |
| `service.yaml` | ClusterIP Service for the monolith (`80` → `8080`) |
| `ingress.yaml` | **Optional** TLS Ingress (`campusflow.local`) |

## Prerequisites

- A Kubernetes cluster (for example kind, minikube, or Docker Desktop Kubernetes)
- `kubectl` configured for that cluster
- The **Chapter 8** image available to the cluster as `campusflow/monolith:0.1.0`  
  Build from the repository root (see `docker/Dockerfile`):

  ```bash
  docker build -f docker/Dockerfile -t campusflow/monolith:0.1.0 .
  ```

  For kind, load the image with `kind load docker-image campusflow/monolith:0.1.0` (or your cluster’s equivalent).

## Secrets (local learning only)

Copy the example Secret to an untracked file, then apply it:

```bash
cp k8s/secret.example.yaml k8s/secret.yaml
```

`k8s/secret.yaml` is listed in `.gitignore` so it is not committed.

The sample username and password (`campusflow` / `campusflow`) are for **local learning only**. Do not use them in production, and do not commit real secret values or TLS private keys.

## Apply order

From the repository root:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

Optional Ingress (only after an NGINX Ingress controller is installed and a `campusflow-tls` Secret exists):

```bash
kubectl apply -f k8s/ingress.yaml
```

## Wait for readiness

```bash
kubectl -n campusflow rollout status deployment/postgres
kubectl -n campusflow rollout status deployment/campusflow-monolith
```

## Inspect

```bash
kubectl -n campusflow get pods,svc
kubectl -n campusflow logs -l app=campusflow-monolith --tail=100
kubectl -n campusflow get ingress   # only if you applied ingress.yaml
```

Port-forward without Ingress:

```bash
kubectl -n campusflow port-forward svc/campusflow-monolith 8080:80
curl http://localhost:8080/actuator/health
```

## Optional TLS Ingress

`ingress.yaml` is **optional**. It expects:

1. An **NGINX** Ingress controller (`ingressClassName: nginx`)
2. DNS or a local hosts entry for `campusflow.local`
3. A TLS Secret named **`campusflow-tls`** in the `campusflow` namespace

Create a learning TLS Secret yourself (self-signed is fine for local demos). Do **not** put certificate private keys into this repository.

## Remove the example

```bash
kubectl delete namespace campusflow
```

This deletes all CampusFlow resources in that namespace, including any optional Ingress and TLS Secret you created there.

## Design notes (Chapter 9 scope)

- Pod and container **security contexts** match the Chapter 8 image user (**UID/GID 10001**).
- **`readOnlyRootFilesystem: true`** with an `emptyDir` mounted at `/tmp`.
- **Startup**, readiness, and liveness probes use Spring Boot Actuator endpoints.
- **`terminationGracePeriodSeconds: 30`** aligns with the monolith’s graceful shutdown (`server.shutdown=graceful`, `spring.lifecycle.timeout-per-shutdown-phase=20s`).
- Advanced chapter topics (service mesh, autoscaling, sealed secrets, operators) are **not** implemented here.
