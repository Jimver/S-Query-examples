
-- Select by delivery zone
SELECT COUNT(*), deliveryZone FROM "snapshot_orderinfo" GROUP BY deliveryZone;
-- 1 How many deliveries per area are late (are in preparation phase more than 10 minutes)
SELECT COUNT(*), deliveryZone FROM "snapshot_orderinfo" JOIN "snapshot_orderstate" USING(partitionKey) WHERE orderState='VENDOR_ACCEPTED' AND (updateTimestamp + 10)<CURRENT_TIMESTAMP GROUP BY deliveryZone;

-- 2 How many deliveries are available per shop category?
SELECT COUNT(*), vendorCategory FROM "snapshot_orderinfo" JOIN "snapshot_orderstate" USING(partitionKey) WHERE orderState='DELIVERED' OR orderState='ORDER_COMPLETED' GROUP BY vendorCategory;

-- 3 How many deliveries are being prepared per area?
SELECT COUNT(*), deliveryZone FROM "snapshot_orderinfo" JOIN "snapshot_orderstate" USING(partitionKey) WHERE orderState='VENDOR_ACCEPTED' GROUP BY deliveryZone;

-- 4 How many deliveries are in transit per area?
SELECT COUNT(*), deliveryZone FROM "snapshot_orderinfo" JOIN "snapshot_orderstate" USING(partitionKey) WHERE orderState='PICKED_UP' OR orderState='LEFT_PICKUP' OR orderState='NEAR_CUSTOMER' GROUP BY deliveryZone;