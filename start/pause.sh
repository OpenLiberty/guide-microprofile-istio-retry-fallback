SYSTEM=`kubectl get pods | grep system | sed 's/ .*//'`
kubectl exec -it $SYSTEM /opt/ol/wlp/bin/server pause
