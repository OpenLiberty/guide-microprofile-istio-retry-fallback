INVENTORY=`kubectl get pods | grep inventory | sed 's/ .*//'`
#kubectl logs $INVENTORY -c istio-proxy
kubectl logs $INVENTORY -c istio-proxy | grep -c system-service:9080
SYSTEM=`kubectl get pods | grep system | sed 's/ .*//'`
#kubectl logs $SYSTEM -c istio-proxy
kubectl logs $SYSTEM -c istio-proxy | grep -c system-service:9080
