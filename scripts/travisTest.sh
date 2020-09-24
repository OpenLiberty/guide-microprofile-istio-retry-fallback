#!/bin/bash
set -euxo pipefail

##############################################################################
##
##  Travis CI test script
##
##############################################################################

kubectl get nodes

ISTIO_LATEST=1.2.5

curl -L https://github.com/istio/istio/releases/download/$ISTIO_LATEST/istio-$ISTIO_LATEST-linux.tar.gz | tar xzvf -

cd istio-$ISTIO_LATEST

for i in install/kubernetes/helm/istio-init/files/crd*yaml; do kubectl apply -f $i; done

kubectl apply -f install/kubernetes/istio-demo.yaml

sleep 240

kubectl get deployments -n istio-system

kubectl label --overwrite namespace default istio-injection=enabled

cd ..

mvn -q package

docker pull openliberty/open-liberty:kernel-java8-openj9-ubi

docker build -t system:1.0-SNAPSHOT system/.
docker build -t inventory:1.0-SNAPSHOT inventory/.

sleep 10

kubectl apply -f services.yaml
kubectl apply -f traffic.yaml

sleep 180 

kubectl get pods

kubectl get deployments

kubectl get all -n istio-system

SYSTEM=`kubectl get pods | grep system | sed 's/ .*//'`

kubectl exec -it $SYSTEM -- /opt/ol/wlp/bin/server pause

sleep 60

echo `minikube ip`

curl -H Host:inventory.example.com http://`minikube ip`:31380/inventory/systems/system-service -I

if [ $? -ne 0 ]
    then
        exit 1
fi

sleep 30

COUNT=`kubectl logs $SYSTEM -c istio-proxy | grep -c system-service:9080`

echo COUNT=$COUNT

kubectl exec $SYSTEM -- cat /logs/messages.log | grep product
kubectl exec $SYSTEM -- cat /logs/messages.log | grep java

if [ $COUNT -lt 3 ]
    then
        exit 1
fi
