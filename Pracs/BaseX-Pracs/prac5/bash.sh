# cp the Musicians file to docker
docker cp Musicians.xml cos326-dockerised-databases-basex-1:/srv/basex/data/Musicians.xml

# open a terminal in the docker container
docker exec -it cos326-dockerised-databases-basex-1 basexclient -Uadmin -Padmin