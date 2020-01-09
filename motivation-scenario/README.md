>> docker exec -i -t CONTAINER_ID /bin/sh

## start rabbitMQ

docker run -d --hostname my-rabbit --name my-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management

## start docker swarm

docker stack deploy -c docker-compose.yaml motivation-scenario

docker stack ps motivation-scenario
docker stack rm motivation-scenario

docker service scale motivation-scenario_inventory-service=INSTANCE_Count
## Rabbit Mq

guest/ guest
