kubectl delete -f config.yaml 
kubectl delete -f traffic.yaml
kubectl delete -f istio.yaml
#kubectl delete -f kubernetes.yaml
echo waiting...
sleep 20
./list.sh