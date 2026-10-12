# Peon, a Jobs Platform

A small Spring Boot app for submitting jobs and watching them run. Enter a job type on the web page and it is saved to PostgreSQL as `QUEUED`. A background worker picks jobs up one at a time and moves them through `RUNNING` to `COMPLETED` (or `FAILED`). The page shows every job's id, type, and status.

- **Backend:** Spring Boot 4, Java 21, Spring Data JPA, PostgreSQL 16
- **Front end:** a single static page (`index.html` + `styles.css`) served by Spring Boot
- **Scaling:** workers claim jobs with `FOR UPDATE SKIP LOCKED`, so you can run many copies of the app against one database and no job is processed twice
- **Recovery:** jobs stuck in `RUNNING` for over 60 seconds (for example after a crash) are put back in the queue

## Run it with Docker Compose

**You need:** [Docker Desktop](https://www.docker.com/products/docker-desktop/), running. Nothing else (no Java, Maven, or Postgres).

```bash
git clone https://github.com/Wilen456/peon.git
cd peon
docker compose up --build
```

Open **http://localhost:8082**.

The first build takes a few minutes. It is ready when the log shows `Started JobPlatformApplication`. Stop it with `Ctrl+C`.

**Useful commands**

```bash
docker compose up -d --build           # run in the background
docker compose ps                      # see what's running
docker compose logs -f app             # follow the app log
docker compose up -d --scale app=3     # run 3 copies of the app
docker compose restart proxy           # run this after scaling so nginx sees the new copies
docker compose down                    # stop, keep the data
docker compose down -v                 # stop and delete the database
```

**Settings (optional):** everything has a working default. To change the port or database credentials, create a file named `.env` next to `docker-compose.yml` (see `.env.example`). If you change the database password after the first run, use `docker compose down -v` once so the database is recreated.

## Run it on Kubernetes (kind)

This runs the same app on a local Kubernetes cluster inside Docker Desktop.

**You need:** Docker Desktop (running), [kind](https://kind.sigs.k8s.io/), and [kubectl](https://kubernetes.io/docs/tasks/tools/).

Stop the Compose version first (`docker compose down`) so the two don't compete for ports.

```bash
# 1. Create the cluster (once)
kind create cluster --name jobs

# 2. Build the image and load it into the cluster
docker build -t job-platform:dev .
kind load docker-image job-platform:dev --name jobs

# 3. Deploy the app and database
kubectl apply -f k8s.yaml

# 4. Wait until both pods show 1/1 Running (Ctrl+C to stop watching)
kubectl get pods -w
```

The app may restart once or twice at first while the database finishes starting. That is normal.

```bash
# 5. Open a tunnel to the app (leave this running)
kubectl port-forward deploy/job-platform 8080:8080
```

Open **http://localhost:8080**.

**Useful commands**

```bash
kubectl get pods                                          # what's running
kubectl logs -l app=job-platform --prefix -f              # follow all app pods
kubectl scale deployment job-platform --replicas=3        # run 3 copies
kubectl scale deployment job-platform db --replicas=0     # pause (keeps data)
kubectl scale deployment db job-platform --replicas=1     # resume
kind delete cluster --name jobs                           # remove everything, data included
```

**After changing code:**

```bash
docker build -t job-platform:dev .
kind load docker-image job-platform:dev --name jobs
kubectl rollout restart deployment/job-platform
```

## Try it out

Add jobs on the web page, or add 100 at once straight to the database:

```bash
# Docker Compose
docker compose exec db psql -U jobs -d jobsdb -c "INSERT INTO jobs (type, status) SELECT (ARRAY['email','backup','request','report','cleanup'])[1 + floor(random()*5)::int], 'QUEUED' FROM generate_series(1,100);"

# Kubernetes
kubectl exec deploy/db -- psql -U jobs -d jobsdb -c "INSERT INTO jobs (type, status) SELECT (ARRAY['email','backup','request','report','cleanup'])[1 + floor(random()*5)::int], 'QUEUED' FROM generate_series(1,100);"
```

Then scale up the app and watch the copies share the queue through the logs.

To delete all jobs and restart the ids at 1:

```bash
# Docker Compose
docker compose exec db psql -U jobs -d jobsdb -c "TRUNCATE jobs RESTART IDENTITY;"

# Kubernetes
kubectl exec deploy/db -- psql -U jobs -d jobsdb -c "TRUNCATE jobs RESTART IDENTITY;"
```

## API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/jobs` | List all jobs |
| `POST` | `/api/jobs` | Create a job. Body: `{"type": "email"}` (required, up to 50 characters) |
| `GET` | `/actuator/health` | Health check |

## Troubleshooting

- **"port is already allocated" (Compose):** something else is using 8081. Set `APP_PORT=8082` (or any free port) in a `.env` file and run `docker compose up` again.
- **The database container keeps restarting, or the app says no datasource URL:** a variable is blank. Make sure you haven't set empty `POSTGRES_*` values in `.env`, then run `docker compose down -v` and start again.
- **502 Bad Gateway after scaling or restarting the app:** run `docker compose restart proxy`.
- **A Kubernetes pod won't start:** `kubectl describe pod <name>` and `kubectl logs <name>` show why.
- **Kubernetes can't find the image:** run `kind load docker-image job-platform:dev --name jobs` again after building.

## Project layout

```
src/main/java/jobplatform/      controller, dto, model, repository, service (including the worker)
src/main/resources/static/      index.html, styles.css
Dockerfile                      two-stage build (Maven, then a slim Java runtime)
docker-compose.yml              app + nginx proxy + PostgreSQL
nginx.conf                      proxy config for Compose
k8s.yaml                        Kubernetes manifests for the app and database
.env.example                    optional settings for Compose
```