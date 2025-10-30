#!/usr/bin/env bash
set -euo pipefail

BS="${1:-localhost:29092}"

for t in reservation.hold.requested reservation.hold.approved reservation.hold.denied \
         reservation.confirmed reservation.cancelled \
         payment.requested payment.authorized payment.failed
do
  echo "Creating topic $t (if not exists)"
  docker exec rr-kafka kafka-topics --bootstrap-server $BS --create --if-not-exists --topic "$t" --replication-factor 1 --partitions 3
done

docker exec rr-kafka kafka-topics --bootstrap-server $BS --list
