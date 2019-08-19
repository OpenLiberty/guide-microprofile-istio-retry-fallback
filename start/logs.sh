INVENTORY=`kubectl get pods | grep inventory | sed 's/ .*//'`
kubectl logs $INVENTORY -c inventory-container
kubectl logs $INVENTORY -c istio-proxy
