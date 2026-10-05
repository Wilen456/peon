Important Commands


.\mvnw spring-boot:run

 docker compose up --build -d
    builds one instance 

docker compose up --build --scale app=X --pull always -d 
    makes swarm of X

docker compose exec db psql -U jobs -d jobsdb -c "INSERT INTO jobs (type, status) SELECT (ARRAY['email','backup','request','report','cleanup'])[1 + floor(random()*5)::int], 'QUEUED' FROM generate_series(1,100);"
    fills table with 100 entries

 docker compose exec db psql -U jobs -d jobsdb -c "TRUNCATE jobs RESTART IDENTITY;"
    empties table

kubernetes

kubectl exec -i (kubectl get pods -l app=db -o jsonpath='{.items[0].metadata.name}') -- psql -U jobs -d jobsdb -c "INSERT INTO jobs (type, status) SELECT (ARRAY['email','backup','request','report','cleanup'])[1 + floor(random()*5)::int], 'QUEUED' FROM generate_series(1,100);"
