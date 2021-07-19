
-- Select by delivery zone
SELECT COUNT(*), deliveryZone FROM "snapshot_orderinfo" GROUP BY deliveryZone;

-- Group by order state
SELECT COUNT(*), orderState FROM "snapshot_orderstate" GROUP BY orderState;

-- 1 How many deliveries per area are late (are in preparation phase more than 10 minutes)
SELECT COUNT(*), deliveryZone FROM "snapshot_orderinfo" t1 JOIN "snapshot_orderstate" t2 USING(partitionKey) WHERE orderState='VENDOR_ACCEPTED' AND lateTimestamp<LOCALTIMESTAMP GROUP BY deliveryZone;

-- 2 How many deliveries are available per shop category?
SELECT COUNT(*), vendorCategory FROM "snapshot_orderinfo" t1 JOIN "snapshot_orderstate" t2 USING(partitionKey) WHERE orderState='NOTIFIED' OR orderState='ACCEPTED' GROUP BY vendorCategory;

-- 3 How many deliveries are being prepared per area?
SELECT COUNT(*), deliveryZone FROM "snapshot_orderinfo" t1 JOIN "snapshot_orderstate" t2 USING(partitionKey) WHERE orderState='VENDOR_ACCEPTED' GROUP BY deliveryZone;

-- 4 How many deliveries are in transit per area?
SELECT COUNT(*), deliveryZone FROM "snapshot_orderinfo" t1 JOIN "snapshot_orderstate" t2 USING(partitionKey) WHERE orderState='PICKED_UP' OR orderState='LEFT_PICKUP' OR orderState='NEAR_CUSTOMER' GROUP BY deliveryZone;
