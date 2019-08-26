#!/bin/bash
set -euxo pipefail

##############################################################################
##
##  Travis CI test script
##
##############################################################################

kubectl get nodes

ISTIO_LATEST=`curl -L -s https://api.github.com/repos/istio/istio/releases/latest | jq -r '.tag_name'`

curl -L https://github.com/istio/istio/releases/download/$ISTIO_LATEST/istio-$ISTIO_LATEST-linux.tar.gz | tar xzvf -

cd istio-$ISTIO_LATEST

for i in install/kubernetes/helm/istio-init/files/crd*yaml; do kubectl apply -f $i; done

kubectl apply -f install/kubernetes/istio-demo.yaml
kubectl get deployments -n istio-system
kubectl label namespace default istio-injection=enabled

cd ..

mvn -q package

docker pull open-liberty

docker build -t system:1.0-SNAPSHOT system/.
docker build -t inventory:1.0-SNAPSHOT inventory/.

sleep 10

kubectl apply -f services.yaml
kubectl apply -f traffic.yaml
kubectl apply -f config.yaml

sleep 50

kubectl get pods

SYSTEM=`kubectl get pods | grep system | sed 's/ .*//'`

kubectl exec -it $SYSTEM /opt/ol/wlp/bin/server pause

sleep 20

COUNT=`kubectl logs $SYSTEM -c istio-proxy | grep -c system-service:9080`

echo $COUNT

echo `minikube ip`

curl -H Host:inventory.example.com http://`minikube ip`:31380/inventory/systems/system-service -I

sleep 20

COUNT=`kubectl logs $SYSTEM -c istio-proxy | grep -c system-service:9080`

echo $COUNT

if [ $COUNT -ne 3 ]
    then
        exit 1
fi
