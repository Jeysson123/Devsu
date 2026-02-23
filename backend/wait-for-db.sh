#!/bin/sh
# wait-for-db.sh
host="$1"
port="$2"
timeout="$3"

echo "Esperando por MySQL en $host:$port ..."

# Bucle de reintento
for i in $(seq 1 $timeout); do
    nc -z "$host" "$port" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "¡MySQL está listo!"
        exit 0
    fi
    echo "Esperando... ($i/$timeout)"
    sleep 1
done

echo "Timeout esperando por MySQL"
exit 1