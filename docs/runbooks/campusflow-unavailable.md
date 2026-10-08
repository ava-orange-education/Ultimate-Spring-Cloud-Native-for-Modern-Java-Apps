# Runbook: CampusFlow unavailable

## Purpose and scope

Use this runbook when CampusFlow is unreachable, pods do not become ready, or a rollout of `deployment/campusflow-monolith` fails in the `campusflow` namespace.

It assumes the companion Kubernetes manifests in `k8s/` and does not require Alertmanager, Argo Rollouts, or an external observability platform.

## 1. Assess

```bash
kubectl -n campusflow get deployment,pods,service
kubectl -n campusflow rollout status deployment/campusflow-monolith
kubectl -n campusflow get events --sort-by=.lastTimestamp
```

Confirm which pods are not Ready, whether the Deployment rollout is stuck, and whether recent events point to image, config, probe, or scheduling problems.

## 2. Stabilize

Limit impact before deep debugging. If a bad rollout is the likely cause, undo it in a controlled way:

```bash
kubectl -n campusflow rollout undo deployment/campusflow-monolith
kubectl -n campusflow rollout status deployment/campusflow-monolith
```

Rollback only when a recent image or manifest change is the probable cause. Do not roll back if the database, secrets, or cluster itself is the failure mode.

## 3. Diagnose

```bash
kubectl -n campusflow describe pod <pod-name>
kubectl -n campusflow logs <pod-name> --tail=200
kubectl -n campusflow logs <pod-name> --previous --tail=200
```

Use `--previous` when a pod is in `CrashLoopBackOff` and you need logs from the last terminated container.

Check especially:

- Image-pull errors (`ImagePullBackOff`, wrong tag, image not loaded into the cluster)
- Missing ConfigMaps or Secrets referenced by the Deployment
- Failed startup, readiness, or liveness probes
- Resource pressure or OOM kills
- Database connectivity to the companion PostgreSQL Service
- The image version currently rolled out on `campusflow-monolith`

## 4. Verify recovery

```bash
kubectl -n campusflow get pods
kubectl -n campusflow rollout status deployment/campusflow-monolith
kubectl -n campusflow port-forward service/campusflow-monolith 8080:80
curl http://localhost:8080/actuator/health
```

Treat the incident as resolved only when the rollout has finished, all intended pods are Ready, and `/actuator/health` returns successfully.

## 5. Close the loop

- Document root cause and timeline
- Record mitigation or rollback steps used
- Note missing metrics, logs, or alerts that would have shortened diagnosis
- Harden recurring causes in code, manifests, or CI checks
- Update this runbook if the procedure changed

## Security note

- `kubectl exec` and `kubectl debug` require broad privileges.
- In production environments, restrict those rights with RBAC to authorized people only.
- Access should be auditable through Kubernetes audit logging.
- Heap dumps, thread dumps, environment variables, and debug output can contain secrets, tokens, or personal data.
- Treat diagnostic files as sensitive: store them securely and delete them in a controlled way after investigation.
